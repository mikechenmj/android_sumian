package com.sumian.sleepdoctor.improve.doctor.activity;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.browser.X5BrowserActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//import com.sumian.sleepdoctor.improve.browser.X5BrowserActivity;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanDoctorQrCodeActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {

    private static final String TAG = ScanDoctorQrCodeActivity.class.getSimpleName();

    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    @BindView(R.id.fl_content)
    FrameLayout mFrameLayout;

    @BindView(R.id.zxing_view)
    ZBarView mZXingView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_scan_doctor_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        StatusBarUtil.setTransparent(this);
        this.mZXingView.setDelegate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCodeQRCodePermissions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZXingView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mZXingView != null) {
            mZXingView.stopCamera();
            mZXingView.onDestroy();
        }
    }

    @OnClick({R.id.iv_back})
    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.e(TAG, "onScanQRCodeSuccess: --------scan--->" + result);
        vibrate();

        if (TextUtils.isEmpty(result)) {
            showCenterToast("无效的二维码，请重新扫描...");
            return;
        }

        Uri uri = Uri.parse(result);

        String uriQuery = Uri.decode(uri.getQueryParameter("scheme"));
        if (TextUtils.isEmpty(uriQuery)) {
            showCenterToast("无效的二维码，请重新扫描...");
            return;
        }

        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(X5BrowserActivity.ARGS_URL, uriQuery);
        X5BrowserActivity.show(this, X5BrowserActivity.class, extras);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast(R.string.open_camera_failed);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToast(R.string.scan_qr_code_denied);
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.scan_qr_code_warn), REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        } else {
            preScanQrCode();
        }
    }

    private void preScanQrCode() {
        this.mZXingView.startCamera();
        this.mZXingView.showScanRect();
        this.mZXingView.startSpot();
    }

    @SuppressWarnings("ConstantConditions")
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }
}
