package com.sumian.common.qr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.common.R;
import com.sumian.common.base.BaseActivity;
import com.sumian.common.helper.ToastHelper;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2018/1/18.
 * desc:
 */

public class QrCodeActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {

    private static final String TAG = QrCodeActivity.class.getSimpleName();

    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    private ZXingView mZXingView;


    public static void show(Context context) {
        context.startActivity(new Intent(context, QrCodeActivity.class));
    }

    @Override
    protected void initWindow() {
        super.initWindow();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_scan_qr_code;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        FrameLayout titleBar = findViewById(R.id.title_bar);
        //4.4版本之后沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            titleBar.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.space_24), 0, 0);
            titleBar.setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.space_72));
        }
        findViewById(R.id.iv_back).setOnClickListener(this);
        this.mZXingView = findViewById(R.id.zxing_view);
        this.mZXingView.setDelegate(this);
    }

    @Override
    protected void initData() {
        super.initData();
        requestCodeQRCodePermissions();
    }

    @Override
    protected void onStop() {
        this.mZXingView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.mZXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        ToastHelper.show(result, Gravity.CENTER);
        vibrate();
        this.mZXingView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        ToastHelper.show(R.string.open_camera_failed);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsGranted: ------------>");
        runUiThread(() -> {
            this.mZXingView.startCamera();
            this.mZXingView.showScanRect();
            this.mZXingView.startSpot();
        },1000);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastHelper.show(R.string.scan_qr_code_denied);
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.scan_qr_code_warn), REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        } else {
            this.mZXingView.startCamera();
            this.mZXingView.showScanRect();
            this.mZXingView.startSpot();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }
}
