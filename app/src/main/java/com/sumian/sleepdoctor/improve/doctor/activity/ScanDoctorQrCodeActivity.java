package com.sumian.sleepdoctor.improve.doctor.activity;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.browser.X5BrowserActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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
    ZXingView mZXingView;

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
    protected void initData() {
        super.initData();
        requestCodeQRCodePermissions();
    }

    @Override
    public void onStop() {
        if (mZXingView != null) {
            mZXingView.stopCamera();
            mZXingView.onDestroy();
        }
        super.onStop();
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

        String decodeUrl = null;
        try {
            decodeUrl = URLDecoder.decode(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "onScanQRCodeSuccess: -----decodeUrl--->" + decodeUrl);

        Uri uri = Uri.parse(decodeUrl);

        Bundle extras = new Bundle();

        String uriQuery = Uri.encode(uri.getQueryParameter("scheme"), "utf-8");
        Log.e(TAG, "onScanQRCodeSuccess: ----uriQuery------>" + uriQuery);

        String encodedPath = uri.getEncodedPath();
        Log.e(TAG, "onScanQRCodeSuccess: ---encodedPath------>" + encodedPath);

        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(X5BrowserActivity.ARGS_URL, decodeUrl);
        X5BrowserActivity.show(this, X5BrowserActivity.class, extras);

        String encodedQuery = uri.getEncodedQuery();
        Log.e(TAG, "onScanQRCodeSuccess: ------encodedQuery----->" + encodedQuery);
    }

    private void showSnackBar() {
        Snackbar.make(mFrameLayout, R.string.invalid_qr_code, Snackbar.LENGTH_LONG).setAction(R.string.re_scan_qr_code, v -> {

        }).show();
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
        preScanQrCode();
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
