package com.sumian.sd.widget;

import android.Manifest;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseFragment;
import com.sumian.sd.kefu.KefuManager;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public class RequestScanQrCodeView extends LinearLayout implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = RequestScanQrCodeView.class.getSimpleName();

    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    private ImageView mIvOpenScan;
    private ImageView mIvCustomService;

    private WeakReference<Fragment> mFragmentWeakReference;
    private OnGrantedCallback mOnGrantedCallback;

    public RequestScanQrCodeView(Context context) {
        this(context, null);
    }

    public RequestScanQrCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RequestScanQrCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        setVisibility(GONE);
    }

    private void init(Context context) {
        View rootView = inflate(context, R.layout.lay_scan_2_bind_doctor_default, this);
        this.mIvOpenScan = rootView.findViewById(R.id.ib_scan);
        mIvOpenScan.setOnClickListener(this);
        this.mIvCustomService = rootView.findViewById(R.id.siv_customer_service);
        mIvCustomService.setOnClickListener(this);
    }

    public RequestScanQrCodeView setOnGrantedCallback(OnGrantedCallback onGrantedCallback) {
        mOnGrantedCallback = onGrantedCallback;
        return this;
    }

    public RequestScanQrCodeView setFragment(SdBaseFragment fragment) {
        if (mFragmentWeakReference == null) {
            mFragmentWeakReference = new WeakReference<>(fragment);
        }
        return this;
    }

    public void onRequestPermissionsResultDelegate(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //permissionsGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastHelper.show(R.string.scan_qr_code_denied);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    public void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            Fragment fragment = mFragmentWeakReference.get();
            EasyPermissions.requestPermissions(fragment, getResources().getString(R.string.scan_qr_code_warn), REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        } else {
            permissionsGranted();
        }
    }

    private void permissionsGranted() {
        if (mOnGrantedCallback != null) {
            mOnGrantedCallback.onGrantedSuccess();
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_scan:
                requestCodeQRCodePermissions();
                break;
            case R.id.siv_customer_service:
                KefuManager.launchKefuActivity();
                break;
        }
    }

    public interface OnGrantedCallback {

        void onGrantedSuccess();
    }

    public void showMsgDot(boolean isHaveMsg) {
        mIvCustomService.setImageResource(isHaveMsg ? R.drawable.ic_info_customerservice_reply : R.drawable.ic_info_customerservice);
    }
}
