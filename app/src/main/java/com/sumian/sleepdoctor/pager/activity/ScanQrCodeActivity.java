package com.sumian.sleepdoctor.pager.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;

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

public class ScanQrCodeActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    @BindView(R.id.zxing_view)
    ZXingView mZXingView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_scan_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        StatusBarUtil.setTranslucent(this);
        StatusBarUtil.setTranslucent(this, 0);
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
        vibrate();

        String decodeUrl = null;
        try {
            decodeUrl = URLDecoder.decode(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(decodeUrl) && (decodeUrl.startsWith("http") || decodeUrl.startsWith("https"))) {
            if (decodeUrl.contains("scheme=sleepdoctor://addgroup")) {
                String scheme = decodeUrl.substring(decodeUrl.indexOf("scheme"));
                String groupId = scheme.substring(scheme.indexOf("id=") + 3);

                Bundle args = new Bundle();
                args.putInt(ScanGroupResultActivity.ARGS_GROUP_ID, Integer.parseInt(groupId, 10));
                ScanGroupResultActivity.show(this, ScanGroupResultActivity.class, args);
                finish();
            } else {
                showToast(R.string.invalid_qr_code);
            }
        } else {
            showCenterToast(decodeUrl);
        }
        this.mZXingView.startSpot();
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
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        preScanQrCode();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
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
