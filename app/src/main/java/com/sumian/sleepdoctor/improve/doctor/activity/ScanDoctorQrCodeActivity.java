package com.sumian.sleepdoctor.improve.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.widget.qr.QrCodeView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanDoctorQrCodeActivity extends BaseActivity implements View.OnClickListener, QrCodeView.OnShowQrCodeCallback, EasyPermissions.PermissionCallbacks {

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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onShowQrCode(String qrCode) {
        Log.e(TAG, "onScanQRCodeSuccess: --------scan--->" + qrCode);

        if (TextUtils.isEmpty(qrCode)) {
            showCenterToast("无效的二维码，请重新扫描...");
            mZXingView.beginSpot();
            Snackbar.make(mZXingView, qrCode, Snackbar.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(qrCode);

        String uriQuery = Uri.decode(uri.getQueryParameter("scheme"));
        if (TextUtils.isEmpty(uriQuery)) {
            showCenterToast("无效的二维码，请重新扫描...");
            mZXingView.beginSpot();
            Snackbar.make(mZXingView, qrCode, Snackbar.LENGTH_SHORT).show();
            return;
        }
        DoctorWebActivity.launch(this, uriQuery);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToast("扫描二维码需要打开相机和存储权限,权限已被禁止,请在设置中授予app 相关权限");
    }
}
