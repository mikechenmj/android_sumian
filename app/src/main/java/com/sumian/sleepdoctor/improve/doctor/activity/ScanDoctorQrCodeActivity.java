package com.sumian.sleepdoctor.improve.doctor.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.widget.qr.RequestQrCodeView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanDoctorQrCodeActivity extends BaseActivity implements View.OnClickListener, RequestQrCodeView.OnShowQrCodeCallback {

    private static final String TAG = ScanDoctorQrCodeActivity.class.getSimpleName();

    @BindView(R.id.zxing_view)
    RequestQrCodeView mZXingView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_scan_doctor_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mZXingView.setOnShowQrCodeCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mZXingView.requestCodeQRCodePermissions(this);
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

    @OnClick({R.id.iv_back})
    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mZXingView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onShowQrCode(String qrCode) {
        Log.e(TAG, "onScanQRCodeSuccess: --------scan--->" + qrCode);

        if (TextUtils.isEmpty(qrCode)) {
            showCenterToast("无效的二维码，请重新扫描...");
            return;
        }

        Uri uri = Uri.parse(qrCode);

        String uriQuery = Uri.decode(uri.getQueryParameter("scheme"));
        if (TextUtils.isEmpty(uriQuery)) {
            showCenterToast("无效的二维码，请重新扫描...");
            return;
        }

        Bundle extras = new Bundle();
        //"https://sd-dev.sumian.com/doctor/1?scheme=" + uriQuery
        extras.putString(DoctorWebViewActivity.ARGS_URL, uriQuery);
        DoctorWebViewActivity.show(this, DoctorWebViewActivity.class, extras);

        //TestNativeActivity.show(this, TestNativeActivity.class);

    }
}
