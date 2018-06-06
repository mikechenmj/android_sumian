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
import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.chat.adapter.MsgAdapter;
import com.sumian.sleepdoctor.chat.contract.MsgContract;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.chat.holder.delegate.AdapterDelegate;
import com.sumian.sleepdoctor.chat.presenter.MsgPresenter;
import com.sumian.sleepdoctor.chat.sheet.PictureBottomSheet;
import com.sumian.sleepdoctor.chat.sheet.SelectPictureBottomSheet;
import com.sumian.sleepdoctor.chat.utils.UiUtil;
import com.sumian.sleepdoctor.chat.widget.KeyboardView;
import com.sumian.sleepdoctor.chat.widget.MsgRecycleView;
import com.sumian.sleepdoctor.pager.activity.GroupDetailActivity;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class MsgActivity extends BaseActivity<MsgContract.Presenter> implements MsgContract.View,
        ViewTreeObserver.OnGlobalLayoutListener, SelectPictureBottomSheet.OnTakePhotoCallback,
        KeyboardView.onKeyboardActionListener, TitleBar.OnBackListener, ChatEngine.OnMsgCallback,
        AdapterDelegate.OnReplyCallback, TitleBar.OnMoreListener, MsgRecycleView.OnLoadDataCallback, MsgRecycleView.OnCloseKeyboardCallback {

    private static final String TAG = MsgActivity.class.getSimpleName();

    public static final String ARGS_GROUP_DETAIL = "group_detail";

    @BindView(R.id.lay_msg_container)
    LinearLayout mLayMsgContainer;

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.recycler)
    MsgRecycleView mRecyclerView;

    @BindView(R.id.keyboardView)
    KeyboardView mKeyboardView;

    private MsgAdapter mMsgAdapter;

    private MsgContract.Presenter mPresenter;

    private int mOpenKeyboardHeight = 0;
    private int mInitBottomHeight = 0;

    private GroupDetail<UserProfile, UserProfile> mGroupDetail;

    private AVIMTypedMessage mReplyMsg;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupDetail = (GroupDetail<UserProfile, UserProfile>) bundle.getSerializable(ARGS_GROUP_DETAIL);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_msg;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));

        mTitleBar.addOnBackListener(this).setMenuOnClickListener(this);
        //  mRefreshView.setOnRefreshListener(this);

        if (mGroupDetail.role == 0) {//患者
            mKeyboardView.showQuestionAction();
        } else {
            mKeyboardView.hideQuestionAction();
        }

        mKeyboardView.setActivity(this).setOnKeyboardActionListener(this);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMsgAdapter = new MsgAdapter(this).bindGroupId(mGroupDetail.id).bindRole(mGroupDetail.role);
        this.mRecyclerView.setAdapter(mMsgAdapter);

        this.mRecyclerView.setOnLoadDataCallback(this);
        this.mRecyclerView.setOnCloseKeyboardCallback(this);

        // this.mLayMsgContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        MsgPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mTitleBar.setTitle(mGroupDetail.name);
        mPresenter.syncMsgHistory(mGroupDetail.conversation_id);
        AppManager.getChatEngine().addOnMsgCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.resultCodeDelegate(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mKeyboardView.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults);
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
        //mRefreshView.setRefreshing(false);
    }

    @Override
    public void onNoHaveMsg() {

    }

    @Override
    public void onNoHaveMoreMsg() {
        showToast("没有更多历史消息");
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
        mPresenter.sendPicMsg(this, MsgPresenter.PIC_REQUEST_CODE_CAMERA, mReplyMsg);
    }

    @Override
    public void onPicPictureCallback() {
        mPresenter.sendPicMsg(this, MsgPresenter.PIC_REQUEST_CODE_LOCAL, mReplyMsg);
    }

    @Override
    public void sendText(String content) {
        mPresenter.sendTextMsg(content, mKeyboardView.isQuestion(), mReplyMsg);
    }

    @Override
    public void sendPic() {
        //launcher send pic callback
        getSupportFragmentManager()
                .beginTransaction()
                .add(SelectPictureBottomSheet.newInstance().addOnTakePhotoCallback(this), PictureBottomSheet.class.getSimpleName())
                .commitAllowingStateLoss();
    }

    @Override
    public void sendVoice(String path, int duration) {
        mPresenter.senAudioMsg(path, duration, mReplyMsg);
    }

    @Override
    public void clearReplyMsg() {
        mReplyMsg = null;
    }

    @Override
    public void setPresenter(MsgContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onReceiverMsgCallback(AVIMTypedMessage msg) {
        mMsgAdapter.addMsg(msg);
        this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
        AppManager.getChatEngine().getAVIMConversation(msg.getConversationId()).read();
    }

    @Override
    public void onReply(AVIMTypedMessage msg) {
        //应用 msg
        mKeyboardView.setAnswerLabel(((AVIMTextMessage) msg).getText());

        this.mReplyMsg = msg;

        Log.e(TAG, "setAnswerLabel: --------被引用的消息----->text=" + ((AVIMTextMessage) msg).getText() + "  timeStamp=" + msg.getTimestamp() + " msg_id=" + msg.getMessageId());
    }

    @Override
    public void onMenuClick(View v) {
        Bundle extras = new Bundle();
        extras.putInt(GroupDetailActivity.ARGS_GROUP_ID, mGroupDetail.id);
        GroupDetailActivity.show(this, GroupDetailActivity.class, extras);
    }

    @Override
    public void onLoadPre() {
        mPresenter.syncPreMsgHistory();
    }

    @Override
    public void onCloseKeyboard() {
        UiUtil.closeKeyboard(mKeyboardView.getInputView());
    }
}
