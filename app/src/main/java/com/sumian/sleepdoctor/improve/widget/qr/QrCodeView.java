package com.sumian.sleepdoctor.improve.widget.qr;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.sumian.common.helper.ToastHelper;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class QrCodeView extends ZBarView implements QRCodeView.Delegate {

    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    private OnShowQrCodeCallback mOnShowQrCodeCallback;

    public QrCodeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QrCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast("打开相机出错,请重新进入页面,再次扫描");
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    public void requestCodeQRCodePermissions(Activity activity) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            beginSpot();
        } else {
            EasyPermissions.requestPermissions(activity, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    public void requestCodeQRCodePermissions(Fragment fragment) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            beginSpot();
        } else {
            EasyPermissions.requestPermissions(fragment, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        }
    }

    public void beginSpot() {
        startSpot();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    private void showToast(String content) {
        ToastHelper.show(content);
    }


    public interface OnShowQrCodeCallback {

        void onShowQrCode(String qrCode);
    }
}
