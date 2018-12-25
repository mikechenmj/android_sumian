package com.sumian.hw.widget;

import android.Manifest;
import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import com.sumian.common.helper.ToastHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RequestQrCodeView extends ZBarView implements QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {


    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    private WeakReference<Fragment> mFragmentWeakReference;

    private OnShowQrCodeCallback mOnShowQrCodeCallback;

    public RequestQrCodeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RequestQrCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDelegate(this);
    }

    public void setOnShowQrCodeCallback(OnShowQrCodeCallback onShowQrCodeCallback) {
        mOnShowQrCodeCallback = onShowQrCodeCallback;
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (!TextUtils.isEmpty(result)) {
            mOnShowQrCodeCallback.onShowQrCode(result);
        }
        vibrate();
        //delayStartSpot();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast("打开相机出错,请重新进入页面,再次扫描");
    }

    public void bindFragment(Fragment f) {
        this.mFragmentWeakReference = new WeakReference<>(f);
    }

    public void showTipText(String tipText) {
        getScanBoxView().setQRCodeTipText(tipText);
        getScanBoxView().requestLayout();
        getScanBoxView().setIsBarcode(false);
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    public void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (isHasPermissions(perms)) {
            delayStartSpot();
        } else {
            EasyPermissions.requestPermissions(mFragmentWeakReference.get(), "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        }
    }

    private boolean isHasPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(getContext(), permissions);
    }

    public void delayStartSpot() {
        startCamera();
        startSpotAndShowRect();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToast("扫描二维码需要打开相机和散光灯的权限,权限已被禁止,请授予app 相关权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void showToast(String content) {
        ToastHelper.show(getContext(), content, Gravity.CENTER);
    }

    public interface OnShowQrCodeCallback {

        void onShowQrCode(String qrCode);
    }
}
