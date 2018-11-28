package com.sumian.sd.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.doctor.bean.DoctorService;
import com.sumian.sd.widget.qr.QrCodeView;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanDoctorQrCodeActivity extends SdBaseActivity implements View.OnClickListener, QrCodeView.OnShowQrCodeCallback, EasyPermissions.PermissionCallbacks {

    public static final String EXTRAS_FROM_RECORD = "com.sumian.sleepdoctor.extras.from.record";
    public static final String EXTRAS_DOCTOR_SERVICE = "com.sumian.sleepdoctor.extras.doctor.service";
    private static final String TAG = ScanDoctorQrCodeActivity.class.getSimpleName();

    private QrCodeView mZXingView;

    private BroadcastReceiver mBroadcastReceiver;

    private boolean mIsFromRecord;
    private DoctorService mDoctorService;


    public static void show(Context context, DoctorService doctorService, boolean isFromRecord) {
        Bundle extras = new Bundle();
        extras.putBoolean(EXTRAS_FROM_RECORD, isFromRecord);
        extras.putParcelable(EXTRAS_DOCTOR_SERVICE, doctorService);
        show(context, ScanDoctorQrCodeActivity.class, extras);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mIsFromRecord = bundle.getBoolean(EXTRAS_FROM_RECORD, false);
            this.mDoctorService = bundle.getParcelable(EXTRAS_DOCTOR_SERVICE);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_scan_doctor_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mZXingView = findViewById(R.id.zxing_view);
        mZXingView.setOnShowQrCodeCallback(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        registerFinishBroadcastReceiver();
    }

    private void registerFinishBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DoctorServiceWebActivity.ACTION_CLOSE_ACTIVE_ACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case DoctorServiceWebActivity.ACTION_CLOSE_ACTIVE_ACTIVITY:
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
        DoctorWebActivity.show(this, uriQuery, mDoctorService, mIsFromRecord);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToast("扫描二维码需要打开相机和存储权限,权限已被禁止,请在设置中授予app 相关权限");
    }
}
