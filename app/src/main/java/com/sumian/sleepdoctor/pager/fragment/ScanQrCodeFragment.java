package com.sumian.sleepdoctor.pager.fragment;

import android.Manifest;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class ScanQrCodeFragment extends BaseFragment implements View.OnClickListener, QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {

    private static final String TAG = ScanQrCodeFragment.class.getSimpleName();

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
        StatusBarUtil.setTranslucent(getActivity());
        StatusBarUtil.setTranslucent(getActivity(), 0);
        this.mZXingView.setDelegate(this);
    }

    @Override
    protected void initData() {
        super.initData();
        requestCodeQRCodePermissions();
    }

    @Override
    public void onStop() {
        mZXingView.post(() -> {
            if (mZXingView != null) {
                mZXingView.stopCamera();
                mZXingView.onDestroy();
            }
        });
        super.onStop();
    }

    @OnClick({R.id.iv_back})
    @Override
    public void onClick(View v) {
        popBack();
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
        preScanQrCode();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastHelper.show(R.string.scan_qr_code_denied);
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.scan_qr_code_warn), REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        } else {
            preScanQrCode();
        }
    }

    private void preScanQrCode() {
        // this.mZXingView.postDelayed(() -> {
        this.mZXingView.startCamera();
        this.mZXingView.showScanRect();
        this.mZXingView.startSpot();
        //}, 500);
    }

    @SuppressWarnings("ConstantConditions")
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }
}
