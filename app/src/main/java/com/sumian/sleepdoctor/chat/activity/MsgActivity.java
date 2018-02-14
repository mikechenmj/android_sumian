package com.sumian.sleepdoctor.chat.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.sumian.sleepdoctor.app.App;
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
import com.sumian.sleepdoctor.pager.activity.GroupDetailActivity;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.sumian.sleepdoctor.chat.presenter.MsgPresenter.PIC_REQUEST_CODE_CAMERA;

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

    public static final String ARGS_GROUP_DETAIL = "group_detail";

    private final static String imagePathName = "/image/";

    private File cameraFile;
    private File storageDir = null;

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

        mTitleBar.addOnBackListener(this).addOnMoreListener(this);
        //  mRefreshView.setOnRefreshListener(this);

        if (mGroupDetail.role == 0) {//患者
            mKeyboardView.showQuestionAction();
        } else {
            mKeyboardView.hideQuestionAction();
        }
        mKeyboardView.setOnKeyboardActionListener(this);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mRecyclerView.setAdapter(mMsgAdapter = new MsgAdapter(this).bindGroupId(mGroupDetail.id).bindRole(mGroupDetail.role));
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
        mTitleBar.setText(mGroupDetail.name);
        mPresenter.syncMsgHistory(mGroupDetail.conversation_id);
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
        showToast("没有历史消息");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void callRecord() {

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

    @AfterPermissionGranted(PIC_REQUEST_CODE_CAMERA)
    @Override
    public void onTakePhotoCallback() {
        mPresenter.sendPicMsg(this, PIC_REQUEST_CODE_CAMERA, mReplyMsg);
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {

            cameraFile = new File(generateImagePath(String.valueOf(AppManager.getAccountViewModel().getToken().user.id), App.Companion.getAppContext()), AppManager.getAccountViewModel().getToken().user.id + System.currentTimeMillis() + ".jpg");
            //noinspection ResultOfMethodCallIgnored
            cameraFile.getParentFile().mkdirs();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //android 7.1之后的相机处理方式
            if (Build.VERSION.SDK_INT < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
            }

        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.str_request_camera_message), PIC_REQUEST_CODE_CAMERA, perms);
        }
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
        getSupportFragmentManager()
                .beginTransaction()
                .add(SelectPictureBottomSheet.newInstance().addOnTakePhotoCallback(this), SelectPictureBottomSheet.class.getSimpleName())
                .commitAllowingStateLoss();
    }

    @Override
    public void sendVoice(String path, int duration) {
        mPresenter.senAudioMsg(path, duration, mReplyMsg);
    }

    @Override
    public void CheckRecordPermission() {
        mPresenter.checkRecordPermission(this);
    }

    @Override
    public void clearReplyMsg() {
        mReplyMsg = null;
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
        mKeyboardView.setAnswerLabel(((AVIMTextMessage) msg).getText());

        this.mReplyMsg = msg;

        Log.e(TAG, "setAnswerLabel: --------被引用的消息----->text=" + ((AVIMTextMessage) msg).getText() + "  timeStamp=" + msg.getTimestamp() + " msg_id=" + msg.getMessageId());
    }

    @Override
    public void onMore(View v) {
        Bundle extras = new Bundle();
        extras.putInt(GroupDetailActivity.ARGS_GROUP_ID, mGroupDetail.id);
        GroupDetailActivity.show(this, GroupDetailActivity.class, extras);
    }

    @Override
    public void onLoadPre() {
        mPresenter.syncPreMsgHistory();
    }

    private File generateImagePath(String userName, Context applicationContext) {
        String path;
        String pathPrefix = "/Android/data/" + applicationContext.getPackageName() + "/";
        path = pathPrefix + userName + imagePathName;
        return new File(getStorageDir(applicationContext), path);
    }

    private File getStorageDir(Context applicationContext) {
        if (storageDir == null) {
            //try to use sd card if possible
            File sdPath = Environment.getExternalStorageDirectory();
            if (sdPath.exists()) {
                return sdPath;
            }
            //use application internal storage instead
            storageDir = applicationContext.getFilesDir();
        }
        return storageDir;
    }
}
