package com.sumian.sleepdoctor.improve.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.widget.qr.QrCodeView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanDoctorQrCodeActivity extends BaseActivity implements View.OnClickListener, QrCodeView.OnShowQrCodeCallback {

    private static final String TAG = ScanDoctorQrCodeActivity.class.getSimpleName();

    @BindView(R.id.zxing_view)
    QrCodeView mZXingView;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_scan_doctor_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mZXingView.setOnShowQrCodeCallback(this);
        registerFinishBroadcastReceiver();
    }

    private void registerFinishBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShoppingCarActivity.ACTION_CLOSE_ACTIVE_ACTIVITY);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case ShoppingCarActivity.ACTION_CLOSE_ACTIVE_ACTIVITY:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }, filter);
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

    @Override
    protected void onRelease() {
        super.onRelease();
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
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
        DoctorWebActivity.launch(this, uriQuery);
    }
}
