package com.sumian.sleepdoctor.widget;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.ib_scan)
    ImageView mIvOpenScan;

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
        setPadding(getResources().getDimensionPixelSize(R.dimen.space_32), 0, getResources().getDimensionPixelSize(R.dimen.space_32), 0);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.b1_color));
        setVisibility(GONE);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_scan_2_bind_doctor_default, this));
    }

    public RequestScanQrCodeView setOnGrantedCallback(OnGrantedCallback onGrantedCallback) {
        mOnGrantedCallback = onGrantedCallback;
        return this;
    }

    public RequestScanQrCodeView setFragment(BaseFragment fragment) {
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

    @OnClick({R.id.ib_scan})
    @Override
    public void onClick(View v) {
        requestCodeQRCodePermissions();
    }

    public interface OnGrantedCallback {

        void onGrantedSuccess();
    }
}
