package com.sumian.sleepdoctor.chat.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.chat.adapter.MsgAdapter;
import com.sumian.sleepdoctor.chat.contract.MsgContract;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.chat.holder.delegate.AdapterDelegate;
import com.sumian.sleepdoctor.chat.presenter.MsgPresenter;
import com.sumian.sleepdoctor.chat.sheet.SelectPictureBottomSheet;
import com.sumian.sleepdoctor.chat.widget.KeyboardView;
import com.sumian.sleepdoctor.chat.widget.MsgRecycleView;
import com.sumian.sleepdoctor.chat.widget.SumianRefreshLayout;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class MsgActivity extends BaseActivity<MsgContract.Presenter> implements MsgContract.View,
        ViewTreeObserver.OnGlobalLayoutListener, SelectPictureBottomSheet.OnTakePhotoCallback,
        KeyboardView.onKeyboardActionListener, TitleBar.OnBackListener, ChatEngine.OnMsgCallback,
        AdapterDelegate.OnReplyCallback, TitleBar.OnMoreListener, MsgRecycleView.OnLoadDataCallback {

    private static final String TAG = MsgActivity.class.getSimpleName();

    public static final String ARGS_CONVERSATION_ID = "args_conversation_id";
    public static final String ARGS_GROUP_ID = "args_group_id";

    @BindView(R.id.lay_msg_container)
    LinearLayout mLayMsgContainer;

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.refresh)
    SumianRefreshLayout mRefreshView;
    @BindView(R.id.recycler)
    MsgRecycleView mRecyclerView;

    @BindView(R.id.keyboardView)
    KeyboardView mKeyboardView;

    private MsgAdapter mMsgAdapter;

    private MsgContract.Presenter mPresenter;

    private int mOpenKeyboardHeight = 0;
    private int mInitBottomHeight = 0;

    private String mConversationId;
    private int mGroupId;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupId = bundle.getInt(ARGS_GROUP_ID);
        this.mConversationId = bundle.getString(ARGS_CONVERSATION_ID);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_msg;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mTitleBar.addOnBackListener(this).addOnMoreListener(this);
        //  mRefreshView.setOnRefreshListener(this);

        mKeyboardView.setOnKeyboardActionListener(this);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mRecyclerView.setAdapter(mMsgAdapter = new MsgAdapter(this).bindGroupId(mGroupId));
        this.mRecyclerView.setOnLoadDataCallback(this);

        //this.mLayMsgContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        MsgPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.joinChatRoom(mConversationId);
        mPresenter.syncMsgHistory(mConversationId);
        AppManager.getChatEngine().setOnMsgCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.resultCodeDelegate(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSendingMsg(AVIMTypedMessage msg) {
        this.mMsgAdapter.addMsg(msg);
        this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
    }

    @Override
    public void onSendMsgSuccess(AVIMTypedMessage msg) {
        onSendMsgFailed(msg);
    }

    @Override
    public void onSendMsgFailed(AVIMTypedMessage msg) {
        this.mMsgAdapter.updateMsg(msg);
    }

    @Override
    public void onSyncMsgHistorySuccess(List<AVIMTypedMessage> messages) {
        this.mMsgAdapter.addMessages(messages);
        this.mRecyclerView.setVisibility(View.VISIBLE);
        this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
    }

    @Override
    public void onSyncPreMsgHistorySuccess(List<AVIMTypedMessage> messages) {
        this.mMsgAdapter.addHistories(messages);
    }

    @Override
    public void onSyncMsgHistoryFailed() {

    }

    @Override
    public void onNoHaveMsg() {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @Override
    protected void onRelease() {
        // mTitleBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        AppManager.getChatEngine().removeOnMsgCallback(this);
        super.onRelease();
    }

    @Override
    public void onGlobalLayout() {

        Rect KeypadRect = new Rect();

        this.mLayMsgContainer.getWindowVisibleDisplayFrame(KeypadRect);

        int screenHeight = mLayMsgContainer.getRootView().getHeight();

        int keypadHeight = screenHeight - KeypadRect.bottom;

        if (keypadHeight == 0 && mInitBottomHeight == 0) {//第一次进入界面或未打开软键盘
            mOpenKeyboardHeight = 0;
            mInitBottomHeight = keypadHeight;
            //Log.e(TAG, "onGlobalLayout: --------->1");
        }

        if (mOpenKeyboardHeight == 0 && keypadHeight > mInitBottomHeight) {//打开软键盘
            mOpenKeyboardHeight = keypadHeight;
            this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
            // Log.e(TAG, "onGlobalLayout: --------->2");
        }

        if (keypadHeight == mInitBottomHeight && keypadHeight < mOpenKeyboardHeight) {//关闭软键盘
            mOpenKeyboardHeight = 0;
            this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
            //Log.e(TAG, "onGlobalLayout: --------->3");
        }

        //  Log.e(TAG, "onGlobalLayout: ------->screenHeight=" + screenHeight + " keypadHeight=" + keypadHeight + " initBottomHeight=" + mInitBottomHeight + " openKeyboardHeight=" + mOpenKeyboardHeight);
    }

    @Override
    public void onTakePhotoCallback() {
        mPresenter.sendPicMsg(this, MsgPresenter.PIC_REQUEST_CODE_CAMERA);
    }

    @Override
    public void onPicPictureCallback() {
        mPresenter.sendPicMsg(this, MsgPresenter.PIC_REQUEST_CODE_LOCAL);
    }

    @Override
    public void sendText(String content) {
        mPresenter.sendTextMsg(content, mKeyboardView.isQuestion(), mKeyboardView.getReplyMsg());
    }

    @Override
    public void sendPic() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(SelectPictureBottomSheet.newInstance().addOnTakePhotoCallback(this), SelectPictureBottomSheet.class.getSimpleName())
                .commitAllowingStateLoss();
    }

    @Override
    public void sendVoice(String path, int duration) {
        mPresenter.sendVoice(this, path, duration);
    }

    @Override
    public void bindPresenter(MsgContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onMsgCallback(AVIMTypedMessage msg) {
        mMsgAdapter.addMsg(msg);
        this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
    }

    @Override
    public void onReply(AVIMTypedMessage msg) {
        //应用 msg
        Log.e(TAG, "onReply: -------->" + msg.toString());

        mKeyboardView.setAnswerLabel((AVIMTextMessage) msg);
    }

    @Override
    public void onMore(View v) {

    }

    @Override
    public void onLoadPre() {
        mPresenter.syncPreMsgHistory(mConversationId);
    }
}
