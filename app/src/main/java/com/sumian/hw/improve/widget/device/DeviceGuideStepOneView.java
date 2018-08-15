package com.sumian.hw.improve.widget.device;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.blue.callback.BlueAdapterCallback;
import com.sumian.blue.manager.BlueManager;
import com.sumian.hw.improve.device.fragment.DeviceFragment;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;

import java.lang.ref.WeakReference;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:  设备未绑定容器,包括指示手机蓝牙未打开,引导去连接设备等功能
 */

public class DeviceGuideStepOneView extends LinearLayout implements View.OnClickListener, EasyPermissions.PermissionCallbacks, BlueAdapterCallback {

    private static final String TAG = DeviceGuideStepOneView.class.getSimpleName();

    TextView mTvLabelH1;
    TextView mTvLabelH2;
    ImageView mIvIcon;
    TextView mTvLabelGoConnect;

    private BlueManager mBlueManager;

    private WeakReference<Fragment> mWeakReference;
    private OnDeviceGuideCallback mOnDeviceGuideCallback;

    public DeviceGuideStepOneView(Context context) {
        this(context, null);
    }

    public DeviceGuideStepOneView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceGuideStepOneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(getResources().getColor(R.color.deep_content_bg_color));
        setOrientation(VERTICAL);
        inflateView(context);
        initView();
    }

    public void setFragment(Fragment fragment) {
        this.mWeakReference = new WeakReference<>(fragment);
    }

    public void setOnDeviceGuideCallback(OnDeviceGuideCallback onDeviceGuideCallback) {
        mOnDeviceGuideCallback = onDeviceGuideCallback;
    }

    private void initView() {
        mBlueManager = AppManager.getBlueManager();
        if (mBlueManager.isEnable()) {
            invalidState(R.string.bind_device, R.string.please_keep_nearly, R.mipmap.equip_btn_binding, R.string.click_to_bind);

        } else {
            invalidState(R.string.sumian_welcome, R.string.please_turn_on_bluetooth_adapter, R.mipmap.equip_icon_bluetooth, R.string.bluetooth_adapter_is_turn_off);
        }
    }

    private void inflateView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_device_guide_step_view, this);
        mTvLabelH1 = inflate.findViewById(R.id.tv_label_h1);
        mTvLabelH2 = inflate.findViewById(R.id.tv_label_h2);
        mIvIcon = inflate.findViewById(R.id.iv_icon);
        mTvLabelGoConnect = inflate.findViewById(R.id.tv_label_go_connect);

        inflate.findViewById(R.id.iv_icon).setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBlueManager.addBlueAdapterCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBlueManager.removeBlueAdapterCallback(this);
    }

    @Override
    public void onClick(View v) {
        if (mBlueManager.isEnable()) {
            requestCodeQRCodePermissions();
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //intent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            mWeakReference.get().startActivityForResult(intent, DeviceFragment.REQUEST_LOCATION_AND_WRITE_PERMISSIONS);
        }
    }

    @AfterPermissionGranted(DeviceFragment.REQUEST_LOCATION_AND_WRITE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            mOnDeviceGuideCallback.doBindMonitor();
        } else {
            Fragment fragment = mWeakReference.get();
            EasyPermissions.requestPermissions(fragment, getResources().getString(R.string.request_permission_hint), DeviceFragment.REQUEST_LOCATION_AND_WRITE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Log.e(TAG, "onPermissionsGranted: -------->" + perms.toString());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //Log.e(TAG, "onPermissionsDenied: ---------->" + perms.toString());
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(mWeakReference.get(), perms)) {
            new AppSettingsDialog.Builder(mWeakReference.get()).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onActivityResultDelegate(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
        // Do something after user returned from app settings screen, like showing a Toast.
        // Log.e(TAG, "onActivityResult: ---------->");
//        }
    }

    @Override
    public void onAdapterEnable() {
        invalidState(R.string.bind_device, R.string.please_keep_nearly, R.mipmap.equip_btn_binding, R.string.click_to_bind);
    }

    @Override
    public void onAdapterDisable() {
        invalidState(R.string.sumian_welcome, R.string.please_turn_on_bluetooth_adapter, R.mipmap.equip_icon_bluetooth, R.string.bluetooth_adapter_is_turn_off);
    }

    private void invalidState(@StringRes int h1, @StringRes int h2, @DrawableRes int icon, @StringRes int action) {
        mTvLabelH1.setText(h1);
        mTvLabelH2.setText(h2);
        mIvIcon.setImageResource(icon);
        mTvLabelGoConnect.setText(action);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public interface OnDeviceGuideCallback {

        void doBindMonitor();
    }
}
