package com.sumian.sd.common.h5;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.snackbar.Snackbar;
import com.sumian.common.base.BaseActivity;
import com.sumian.sd.R;
import com.sumian.sd.buz.stat.StatConstants;
import com.sumian.sd.widget.qr.QrCodeView;
import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanQrCodeActivity extends BaseActivity implements View.OnClickListener, QrCodeView.OnShowQrCodeCallback {

    private QrCodeView mZXingView;

    public static int RESULT_CODE_SCAN_QR_CODE = 1;

    public static String EXTRA_RESULT_QR_CODE = "extra_result_qr_code";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_h5_qr_code;
    }

    @NotNull
    @Override
    public String getPageName() {
        return StatConstants.page_scan_doctor;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mZXingView = findViewById(R.id.zxing_view);
        mZXingView.setOnShowQrCodeCallback(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera();
        mZXingView.beginSpot();
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
            mZXingView.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onShowQrCode(String qrCode) {
        if (TextUtils.isEmpty(qrCode)) {
            ToastUtils.showShort("无效的二维码，请重新扫描...");
            mZXingView.beginSpot();
            Snackbar.make(mZXingView, qrCode, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_QR_CODE, qrCode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
