package com.sumian.sleepdoctor.widget;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.tab.fragment.GroupFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public class GroupRequestScanQrCodeView extends LinearLayout implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = GroupRequestScanQrCodeView.class.getSimpleName();

    private static final int REQUEST_CODE_QR_CODE_PERMISSIONS = 1;

    @BindView(R.id.ib_scan)
    ImageView mIvOpenScan;

    private WeakReference<Fragment> mFragmentWeakReference;
    private OnGrantedCallback mOnGrantedCallback;


    public GroupRequestScanQrCodeView(Context context) {
        this(context, null);
    }

    public GroupRequestScanQrCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupRequestScanQrCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setPadding(getResources().getDimensionPixelSize(R.dimen.space_32), 0, getResources().getDimensionPixelSize(R.dimen.space_32), 0);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.b1_color));
        setVisibility(GONE);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_scan_join_group_default, this));
    }

    public void setOnGrantedCallback(OnGrantedCallback onGrantedCallback) {
        mOnGrantedCallback = onGrantedCallback;
    }

    public GroupRequestScanQrCodeView setFragment(GroupFragment fragment) {
        if (mFragmentWeakReference == null) {
            mFragmentWeakReference = new WeakReference<>(fragment);
        }
        return this;
    }

    public void onRequestPermissionsResultDelegate(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsGranted: ----------->");
        permissionsGranted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastHelper.show(R.string.scan_qr_code_denied);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QR_CODE_PERMISSIONS)
    public void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            Fragment fragment = mFragmentWeakReference.get();
            EasyPermissions.requestPermissions(fragment, getResources().getString(R.string.scan_qr_code_warn), REQUEST_CODE_QR_CODE_PERMISSIONS, perms);
        } else {
            permissionsGranted();
        }
    }

    private void permissionsGranted() {
        if (mOnGrantedCallback != null) {
            mOnGrantedCallback.onGrantedSuccess();
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @OnClick({R.id.ib_scan})
    @Override
    public void onClick(View v) {
        requestCodeQRCodePermissions();
    }

    public interface OnGrantedCallback {

        void onGrantedSuccess();
    }
}
