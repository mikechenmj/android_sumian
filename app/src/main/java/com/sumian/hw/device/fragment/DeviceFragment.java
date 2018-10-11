package com.sumian.hw.device.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sumian.common.utils.ColorCompatUtil;
import com.sumian.hw.base.HwBaseFragment;
import com.sumian.hw.common.util.SpUtil;
import com.sumian.hw.device.bean.BlueDevice;
import com.sumian.hw.device.contract.DeviceContract;
import com.sumian.hw.device.dialog.PaModeDialog;
import com.sumian.hw.device.dialog.PairOnDeviceDialog;
import com.sumian.hw.device.pattern.SyncPatternManager;
import com.sumian.hw.device.presenter.DevicePresenter;
import com.sumian.hw.device.sheet.DeviceBottomSheet;
import com.sumian.hw.job.JobTask;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.utils.LocationManagerUtil;
import com.sumian.hw.widget.device.DeviceGuideStepOneView;
import com.sumian.hw.widget.device.DeviceStatusView;
import com.sumian.hw.widget.dialog.SumianDialog;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.main.OnEnterListener;
import com.sumian.sd.widget.dialog.SumianAlertDialog;
import com.sumian.sd.widget.dialog.theme.BlackTheme;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class DeviceFragment extends HwBaseFragment<DeviceContract.Presenter> implements DeviceGuideStepOneView.OnDeviceGuideCallback, DeviceContract.View, DeviceStatusView.OnDeviceStatusCallback, EasyPermissions.PermissionCallbacks, OnEnterListener {

    public static final int REQUEST_LOCATION_AND_WRITE_PERMISSIONS = 0x01;   // DeviceGuideStepOneView 用到
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 0x02; // DeviceStatusView 用到
    public static final int REQUEST_LOCATION_PERMISSION = 0x03; // DeviceFragment 用到
    public static final int REQUEST_OPEN_LOCATION_SERVICE_FOR_SCAN_AND_CONNECT = 0x04; // DeviceFragment 用到
    public static final int REQUEST_OPEN_LOCATION_SERVICE_FOR_SCAN_BIND_MONITOR = 0x05; // DeviceFragment 用到
    public static final int POPUP_WINDOW_DISMISS_TIME = 150;

    DeviceGuideStepOneView mDeviceGuideStepView;
    DeviceStatusView mDeviceStatusView;

    private BroadcastReceiver mReceiver;
    private PaModeDialog mPaModeDialog;
    private BlueDevice mMonitor;
    private PopupWindow mPopupWindow;
    private long mPopupDismissTime;

    private BroadcastReceiver mGlobalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:
                    LogManager.appendUserOperationLog("手机被用户锁屏,熄灭屏幕");
                    break;
                case Intent.ACTION_SCREEN_ON:
                    LogManager.appendUserOperationLog("手机被用户点亮屏幕");
                    break;
                case Intent.ACTION_USER_PRESENT:
                    LogManager.appendUserOperationLog("app  从后台切换到前台,并且屏幕被用户解锁了");
                    autoSyncSleepData();
                    break;
                default:
                    break;
            }
        }
    };

    public static DeviceFragment newInstance() {
        return new DeviceFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_main_device;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mDeviceGuideStepView = root.findViewById(R.id.device_guide_step_view);
        mDeviceStatusView = root.findViewById(R.id.device_status_view);

        mDeviceGuideStepView.setFragment(this);
        mDeviceGuideStepView.setOnDeviceGuideCallback(this);
        mDeviceStatusView.setOnDeviceStatusCallback(this);
        mDeviceStatusView.setFragment(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        DevicePresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();

        AppManager.getDeviceModel().registerOnSyncSleepDataProgressChangeListener(mDeviceStatusView);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PairOnDeviceDialog.ACTION_BIND);
        filter.addAction(DeviceBottomSheet.ACTION_TURN_MONITORING_MODE);
        filter.addAction(DeviceBottomSheet.ACTION_UNBIND);
        filter.addAction(JobTask.ACTION_SYNC);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case PairOnDeviceDialog.ACTION_BIND:
                        BlueDevice blueDevice = (BlueDevice) intent.getSerializableExtra(PairOnDeviceDialog.EXTRA_DEVICE);
                        mPresenter.doConnect(blueDevice);
                        break;
                    default:
                        break;
                }
            }
        }, filter);

        if (isEnable()) {
            checkDeviceCacheAndScan2Connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
                break;
            case REQUEST_LOCATION_AND_WRITE_PERMISSIONS:
                mDeviceGuideStepView.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                mDeviceStatusView.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_LOCATION_SERVICE_FOR_SCAN_AND_CONNECT) {
            if (LocationManagerUtil.isLocationProviderEnable(getContext())) {
                scanAndConnectWithPermissionCheck(mMonitor);
            }
        } else if (requestCode == REQUEST_OPEN_LOCATION_SERVICE_FOR_SCAN_BIND_MONITOR) {
            if (LocationManagerUtil.isLocationProviderEnable(getContext())) {
                doBindMonitor();
            }
        } else {
            mDeviceGuideStepView.onActivityResultDelegate(requestCode, resultCode, data);
        }
    }

    @Override
    public void onEnter(String data) {
        LogManager.appendUserOperationLog("点击进入 '设备' 界面");
        if (isResumed()) {
            autoSyncSleepData();
        }
        AppManager.getJobScheduler().checkJobScheduler();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppManager.getDeviceModel().registerOnSyncSleepDataProgressChangeListener(mDeviceStatusView);

        IntentFilter globalFilter = new IntentFilter();
        globalFilter.addAction(Intent.ACTION_USER_FOREGROUND);
        globalFilter.addAction(Intent.ACTION_USER_BACKGROUND);
        globalFilter.addAction(Intent.ACTION_USER_PRESENT);
        getContext().registerReceiver(mGlobalReceiver, globalFilter);

        autoSyncSleepData();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mGlobalReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        SyncPatternManager.Companion.syncPatternInPossible(getActivity());
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        AppManager.getDeviceModel().unregisterOnSyncSleepDataProgressChangeListener(mDeviceStatusView);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void doBindMonitor() {
        boolean isLocationServiceEnable = checkLocationService(REQUEST_OPEN_LOCATION_SERVICE_FOR_SCAN_BIND_MONITOR);
        if (!isLocationServiceEnable) {
            return;
        }
        PairOnDeviceDialog pairOnDeviceDialog = new PairOnDeviceDialog(getContext());
        pairOnDeviceDialog.show();
    }

    @Override
    public void setPresenter(DeviceContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onMonitorCallback(BlueDevice monitor) {
        mDeviceStatusView.invalidDevice(monitor);
        if (mPresenter.adapterIsEnable()) {
            mDeviceStatusView.show();
            mDeviceGuideStepView.hide();
        } else {
            mDeviceStatusView.hide();
            mDeviceGuideStepView.show();
        }
    }

    @Override
    public void onUnbindCallback(BlueDevice monitor) {
        mDeviceStatusView.hide();
        mDeviceGuideStepView.show();
    }

    @Override
    public void showStartingPaMode() {
        //if (mPaModeDialog == null) {
        mPaModeDialog = new PaModeDialog(getContext());
        //}
        mPaModeDialog.setType(0x01).show();
    }

    @Override
    public void showTurnOnPaModeSuccess() {
        if (mPaModeDialog != null) {
            mPaModeDialog.cancel();
        }
    }

    @Override
    public void showTurnOnPaModeFailed(int errorId) {
        if (mPaModeDialog != null) {
            mPaModeDialog.setType(0x02).setTvContent(errorId).showError();
        }
    }

    @Override
    public void syncDeviceSleepChaSuccess() {
        mDeviceStatusView.showSyncDeviceSleepChaSuccess();
    }

    @Override
    public void syncDeviceSleepChaFailed() {
        mDeviceStatusView.showSyncDeviceSleepChaFailed();
    }

    @Override
    public void onEnableAdapterCallback() {
        mDeviceStatusView.show();
        mDeviceGuideStepView.hide();
        checkDeviceCacheAndScan2Connect();
        AppManager.getJobScheduler().checkJobScheduler();
        LogManager.appendBluetoothLog("手机蓝牙被打开");
    }

    @Override
    public void onDisableAdapterCallback() {
        mDeviceStatusView.hide();
        mDeviceGuideStepView.show();
        LogManager.appendBluetoothLog("手机蓝牙被关闭");
    }

    @Override
    public void doMoreAction(View view) {
        showPopup(view);
    }

    private void showPopup(View view) {
        // popup 显示的时候，点击按钮，popup会先自动dismiss，然后触发view的onclick事件，两者时间间隔在30-80ms之间，
        // 所以此时要过滤点击事件
        if (System.currentTimeMillis() - mPopupDismissTime < POPUP_WINDOW_DISMISS_TIME) {
            return;
        }
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_device_operation_popup, null);
        TextView tvTurnMonitor = inflate.findViewById(R.id.tv_turn_monitor);
        TextView tvUnbind = inflate.findViewById(R.id.tv_unbind);
        TextView tvSynchronize = inflate.findViewById(R.id.tv_synchronize);
        View dividerOne = inflate.findViewById(R.id.v_divider_1);
        View dividerTwo = inflate.findViewById(R.id.v_divider_2);
        BlueDevice monitor = mPresenter.getCurrentMonitor();
        int tvTurnMonitorAndSynchronizeVisibility = (monitor.isConnected()) ? View.VISIBLE : View.GONE;
        tvTurnMonitor.setVisibility(tvTurnMonitorAndSynchronizeVisibility);
        tvSynchronize.setVisibility(tvTurnMonitorAndSynchronizeVisibility);
        dividerOne.setVisibility(tvTurnMonitorAndSynchronizeVisibility);
        dividerTwo.setVisibility(tvTurnMonitorAndSynchronizeVisibility);
        tvTurnMonitor.setText(monitor.isMonitoring ? R.string.turn_off_snoop_mode : R.string.turn_on_snoop_mode);
        tvTurnMonitor.setTextColor(ColorCompatUtil.Companion.getColor(getActivity(), monitor.isMonitoring ? R.color.dot_red_color : R.color.bt_hole_color));
        tvTurnMonitor.setOnClickListener(v -> {
            int monitoringMode = monitor.isMonitoring ? BlueDevice.MONITORING_CMD_CLOSE : BlueDevice.MONITORING_CMD_OPEN;
            new SumianAlertDialog(getContext())
                    .setTheme(new BlackTheme())
                    .hideTopIcon(true)
                    .setTitle(monitor.isMonitoring ? R.string.turn_off_monitoring_mode_title : R.string.turn_on_monitoring_mode_title)
                    .setMessage(monitor.isMonitoring ? R.string.turn_off_monitoring_mode_message : R.string.turn_on_monitoring_mode_message)
                    .setLeftBtn(R.string.cancel, null)
                    .setRightBtn(R.string.sure, v1 -> mPresenter.turnOnMonitoringMode(monitoringMode))
                    .show();
            mPopupWindow.dismiss();
        });
        tvUnbind.setOnClickListener(v -> {
            mPresenter.doUnbind();
            mPopupWindow.dismiss();
        });
        tvSynchronize.setOnClickListener(v -> {
            mDeviceStatusView.syncSleepDataWithPermissionCheck();
            mPopupWindow.dismiss();
        });

        mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAsDropDown(view);
        mPopupWindow.setOnDismissListener(() -> mPopupDismissTime = System.currentTimeMillis());
    }

    @Override
    public void doTurnOnSleep() {
        mPresenter.turnOnSleep();
    }

    @Override
    public void doSyncSleepCha() {
        mPresenter.doSyncSleepData();
    }

    @Override
    public void doConnect(BlueDevice monitor) {
        scanAndConnectWithPermissionCheck(monitor);
    }

    @SuppressWarnings("unused")
    @AfterPermissionGranted(DeviceFragment.REQUEST_LOCATION_PERMISSION)
    public void onLocationPermissionGranted() {
        scanAndConnectWithPermissionCheck(mMonitor);
    }

    public void scanAndConnectWithPermissionCheck(BlueDevice monitor) {
        mMonitor = monitor;
        if (!checkLocationService(REQUEST_OPEN_LOCATION_SERVICE_FOR_SCAN_AND_CONNECT)) {
            return;
        }
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            mPresenter.doScan2Connect(monitor);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.request_permission_hint), DeviceFragment.REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    /**
     * Check whether is location service enable.
     * If not enable, show a dialog to guid user open location service.
     *
     * @return whether is location service enable
     */
    private boolean checkLocationService(int requestCode) {
        boolean locationProviderEnable = LocationManagerUtil.isLocationProviderEnable(getContext());
        if (locationProviderEnable) {
            return true;
        } else {
            SumianDialog.create(getContext())
                    .setTitleText(R.string.open_location_service_for_blue_scan_hint)
                    .setLeftText(R.string.cancel, null)
                    .setRightText(R.string.confirm, v -> LocationManagerUtil.startLocationSettingActivityForResult(DeviceFragment.this, requestCode))
                    .show();
            return false;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(R.string.require_permission)
                    .setRationale(R.string.require_location_permission_for_bluetooth_connection_rationale)
                    .build()
                    .show();
        }
    }

    private boolean isEnable() {
        return mPresenter.adapterIsEnable();
    }

    private void checkDeviceCacheAndScan2Connect() {
        BlueDevice monitor = mPresenter.checkCache();
        if (monitor != null) {
            mDeviceStatusView.show();
            mDeviceGuideStepView.hide();
            BlueDevice speedSleeper = new BlueDevice();
            speedSleeper.name = App.Companion.getAppContext().getString(R.string.speed_sleeper);
            monitor.speedSleeper = speedSleeper;
            mDeviceStatusView.invalidDevice(monitor);
            scanAndConnectWithPermissionCheck(monitor);
        } else {
            mDeviceStatusView.hide();
            mDeviceGuideStepView.show();
        }
    }

    private void autoSyncSleepData() {
        long autoUploadTime = SpUtil.initSp("upload_sleep_cha_time").getLong("time", 0);

        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);

        if (mPresenter.isConnected() && powerManager.isInteractive() && (autoUploadTime == 0 || System.currentTimeMillis() - autoUploadTime > 5)) {
            mDeviceStatusView.syncSleepDataWithPermissionCheck();
            LogManager.appendPhoneLog("app  主动同步睡眠数据,原因是上一次同步的时间超过1小时");
        }
    }
}
