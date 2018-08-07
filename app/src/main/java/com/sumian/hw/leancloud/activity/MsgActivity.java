package com.sumian.hw.leancloud.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.account.sheet.SelectBottomSheet;
import com.sumian.hw.account.sheet.SelectPictureBottomSheet;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.common.util.UiUtil;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.leancloud.adapter.MsgAdapter;
import com.sumian.hw.leancloud.contract.MsgContract;
import com.sumian.hw.leancloud.presenter.MsgPresenter;
import com.sumian.hw.widget.KeyboardView;
import com.sumian.hw.widget.LCIMRecordButton;
import com.sumian.hw.widget.MsgEmptyView;
import com.sumian.hw.widget.refresh.BlueRefreshView;
import com.sumian.sleepdoctor.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class MsgActivity extends HwBaseActivity implements View.OnClickListener, MsgContract.View,
        ViewTreeObserver.OnGlobalLayoutListener, SelectPictureBottomSheet.OnTakePhotoCallback, EasyPermissions.PermissionCallbacks,
        KeyboardView.onKeyboardActionListener, LCIMRecordButton.RecordEventListener, LCIMRecordButton.OnCheckRecordPermission {

    private static final String TAG = MsgActivity.class.getSimpleName();

    private static final int CAMERA_PERM = 1;
    private static final int RECORD_PERM = 2;

    private static final String EXTRA_SERVICE_TYPE = "service_type";

    LinearLayout mLayMsgContainer;
    View mTitleBar;
    TextView mTvTitle;
    FrameLayout mAdapterPop;
    BlueRefreshView mRefreshView;
    RecyclerView mRecyclerView;
    KeyboardView mKeyboardView;
    MsgEmptyView mMsgEmptyView;

    private MsgAdapter mMsgAdapter;

    private MsgContract.Presenter mPresenter;

    private int mOpenKeyboardHeight = 0;
    private int mInitBottomHeight = 0;

    private int mServiceType = 0x00;

    private boolean mIsLogin;

    public static void show(Context context, int serviceType) {
        Intent intent = new Intent(context, MsgActivity.class);
        intent.putExtra(EXTRA_SERVICE_TYPE, serviceType);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mServiceType = bundle.getInt(EXTRA_SERVICE_TYPE, HwLeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_msg;
    }

    @SuppressWarnings("LambdaParameterTypeCanBeSpecified")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initWidget() {
        super.initWidget();
        mLayMsgContainer = findViewById(R.id.lay_msg_container);
        mTitleBar = findViewById(R.id.title_bar);
        mTvTitle = findViewById(R.id.tv_title);
        mAdapterPop = findViewById(R.id.adapter_pop);
        mRefreshView = findViewById(R.id.refresh);
        mRecyclerView = findViewById(R.id.recycler);
        mKeyboardView = findViewById(R.id.keyboardView);
        mMsgEmptyView = findViewById(R.id.msg_empty_view);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_title).setOnClickListener(this);

        MsgPresenter.init(this);
        @StringRes int textId = R.string.setting_send_msg_hint;
        switch (mServiceType) {
            case HwLeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER:
                textId = R.string.setting_send_msg_hint;
                mKeyboardView.setOnKeyboardActionListener(this)
                        .setRecordEventListener(this)
                        .setCheckRecordPermission(this)
                        .setVisibility(View.VISIBLE);
                break;
            case HwLeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR:
                textId = R.string.consultant_doctor;
                mKeyboardView.setOnKeyboardActionListener(this)
                        .setRecordEventListener(this)
                        .setCheckRecordPermission(this)
                        .setVisibility(View.VISIBLE);
                break;
            case HwLeanCloudHelper.SERVICE_TYPE_MAIL:
                textId = R.string.msg_notice_hint;
                mKeyboardView.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        mTvTitle.setText(textId);

        this.mRecyclerView.setAdapter(mMsgAdapter = new MsgAdapter(mServiceType));
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mServiceType != HwLeanCloudHelper.SERVICE_TYPE_MAIL) {
            this.mLayMsgContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);
            this.mRecyclerView.setOnTouchListener((v, event) -> {
                UiUtil.closeKeyboard(mKeyboardView.getEtInputView());
                return false;//false 向下传递
            });
        }

        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                // Log.e(TAG, "onScrollStateChanged: ------->" + newState + "  position=" + position);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && position == 0) {
                    mPresenter.syncPreMsgHistory(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.e(TAG, "onScrolled: ------->dx=" + dx + "   dy=" + dy);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if (mServiceType == HwLeanCloudHelper.SERVICE_TYPE_MAIL) {
            mPresenter.syncMsgHistory(mServiceType);
        } else {
            mPresenter.loginServiceType(mServiceType);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.resultCodeDelegate(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //ToastHelper.show(R.string.permissions_camera_error);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //ToastHelper.show(R.string.gallery_save_file_not_have_external_storage_permission);
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onSendingMsg(AVIMMessage msg) {
        runUiThread(() -> {
            this.mMsgAdapter.addMsg(msg);
            this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void onSendMsgSuccess(AVIMMessage msg) {
        onSendMsgFailed(msg);
    }

    @Override
    public void onSendMsgFailed(AVIMMessage msg) {
        runUiThread(() -> this.mMsgAdapter.updateMsg(msg));
    }

    @Override
    public void onSyncMsgHistorySuccess(List<AVIMMessage> messages) {
        runUiThread(() -> {
            if (mServiceType == HwLeanCloudHelper.SERVICE_TYPE_MAIL) {
                if (mMsgAdapter.getItemCount() <= 0 && (messages == null || messages.isEmpty())) {
                    mMsgEmptyView.show();
                } else {
                    mMsgEmptyView.hide();
                }
            }
            this.mMsgAdapter.addMessages(messages);
            this.mRecyclerView.setVisibility(View.VISIBLE);
            // this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void onSyncPreMsgHistorySuccess(List<AVIMMessage> messages) {
        runUiThread(() -> this.mMsgAdapter.addHistories(messages));
    }

    @Override
    public void onSyncMsgHistoryFailed() {
    }

    @Override
    public void onReceiveMsg(AVIMMessage msg) {
        runUiThread(() -> {
            this.mMsgAdapter.addMsg(msg);
            this.mRecyclerView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void onNoHaveMsg() {
        runUiThread(() -> {
            if (mServiceType == HwLeanCloudHelper.SERVICE_TYPE_MAIL) {
                if (mMsgAdapter.getItemCount() <= 0) {
                    mMsgEmptyView.show();
                } else {
                    mMsgEmptyView.hide();
                }
            }
        });
    }

    @Override
    public void onPrepareLogin() {
        runUiThread(() -> mTvTitle.setText(mServiceType == HwLeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER ?
                "在线客服(连接中)" : "速眠医生(连接中)"));
    }

    @Override
    public void onLoginSuccess() {
        mIsLogin = true;
        runUiThread(() -> mTvTitle.setText(mServiceType == HwLeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER ?
                "在线客服" : "速眠医生"));
    }

    @Override
    public void onLoginFailed() {
        mIsLogin = false;
        runUiThread(() -> mTvTitle.setText(mServiceType == HwLeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER ? "在线客服(连接失败)" : "速眠医生(连接失败)"));
        runUiThread(() -> ToastHelper.show("连接失败,请尝试重新连接..."));
    }

    @Override
    public void setPresenter(MsgContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {
            finish();
        } else if (i == R.id.tv_title) {
            HwLeanCloudHelper.establishConversationWithService(mServiceType);
        }
    }

    @Override
    protected void onRelease() {
        if (mServiceType != HwLeanCloudHelper.SERVICE_TYPE_MAIL) {
            mTitleBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        mPresenter.release();
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
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        runUiThread(() -> mRefreshView.setRefreshing(true));
    }

    @Override
    public void onFinish() {
        runUiThread(() -> mRefreshView.setRefreshing(false));
    }

    @Override
    public void onTakePhotoCallback() {
        cameraTask();
    }

    @Override
    public void onPicPictureCallback() {
        mPresenter.sendPic(this, MsgPresenter.PIC_REQUEST_CODE_LOCAL);
    }

    @AfterPermissionGranted(CAMERA_PERM)
    private void cameraTask() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mPresenter.sendPic(this, MsgPresenter.PIC_REQUEST_CODE_CAMERA);
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_camera_message), CAMERA_PERM, perms);
        }
    }

    @AfterPermissionGranted(RECORD_PERM)
    private boolean recordTask() {
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this,
                    getResources().getString(R.string.str_request_record_message), RECORD_PERM, perms);
            return false;
        }
    }


    @Override
    public boolean sendText(String input) {
        if (!mIsLogin) {
            ToastHelper.show(R.string.leancloud_login_failed_hint);
            return false;
        }
        mPresenter.doSendTextMsg(input);
        return true;
    }

    @Override
    public void sendPic() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(SelectPictureBottomSheet
                        .newInstance()
                        .addOnTakePhotoCallback(this), SelectBottomSheet.class.getSimpleName())
                .commit();
    }

    @Override
    public void onSoftKeyboardCallback(boolean closeAction) {
        if (closeAction) {
            UiUtil.closeKeyboard(mKeyboardView.getEtInputView());
        } else {
            UiUtil.openKeyboard(mKeyboardView.getEtInputView());
        }
    }

    @Override
    public void onFinishedRecord(String audioPath, int secs) {
        mPresenter.sendVoice(audioPath, secs);
    }

    @Override
    public void onStartRecord() {

    }

    @Override
    public boolean onPermissionCallback() {

        return recordTask();
    }
}
