package com.sumian.hw.upgrade.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumian.common.helper.ToastHelper;
import com.sumian.common.widget.TitleBar;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.upgrade.contract.VersionUpgradeContract;
import com.sumian.hw.upgrade.dialog.Version2ConnectingDialog;
import com.sumian.hw.upgrade.dialog.VersionDialog;
import com.sumian.hw.upgrade.presenter.DeviceVersionUpgradePresenter;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.device.DeviceManager;
import com.sumian.sd.widget.dialog.SumianAlertDialog;

import java.util.List;
import java.util.Locale;

import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2017/10/27.
 * <p>
 * desc:固件升级提醒模块
 */

@SuppressWarnings("ConstantConditions")
public class DeviceVersionUpgradeActivity extends HwBaseActivity implements View.OnClickListener, TitleBar.OnBackClickListener
        , VersionUpgradeContract.View, EasyPermissions.PermissionCallbacks {
    private static final String EXTRA_VERSION_TYPE = "extra_version_type";
    private static final String EXTRA_VERSION_IS_LATEST = "extra_version_latest";
    private static final long DISMISS_DIALOG_DELAY = 1200L;
    public static final int VERSION_TYPE_MONITOR = 0x02;
    public static final int VERSION_TYPE_SLEEPY = 0x03;
    private static final int REQUEST_WRITE_PERMISSION = 0xff;
    private ImageView mIvUpgrade;
    private TextView mTvVersionLatest;
    private TextView mTvVersionCurrent;
    private Button mBtDownload;
    private VersionUpgradeContract.Presenter mPresenter;
    private VersionDialog mVersionDialog;
    private Version2ConnectingDialog mVersion2ConnectingDialog;
    private int mVersionType;
    private boolean mIsLatestVersion;
    private int mDfuCount;
    private Runnable mDismissDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (mVersionDialog != null) {
                ToastHelper.show(R.string.firmware_upgrade_failed_hint);
                mPresenter.abort();
                cancelDialog();
            }
        }
    };

    public static void show(Context context, int versionType, boolean haveLatestVersion) {
        Intent intent = new Intent(context, DeviceVersionUpgradeActivity.class);
        intent.putExtra(EXTRA_VERSION_TYPE, versionType);
        intent.putExtra(EXTRA_VERSION_IS_LATEST, haveLatestVersion);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mVersionType = bundle.getInt(EXTRA_VERSION_TYPE);
        mIsLatestVersion = bundle.getBoolean(EXTRA_VERSION_IS_LATEST);
        DeviceVersionUpgradePresenter.init(this);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_version_upgrade;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnBackClickListener(this);
        titleBar.setTitle(mVersionType == VERSION_TYPE_MONITOR ? "监测仪升级" : "速眠仪升级");
        mIvUpgrade = findViewById(R.id.iv_upgrade);
        mTvVersionLatest = findViewById(R.id.tv_version_latest);
        mTvVersionCurrent = findViewById(R.id.tv_version_current);
        mBtDownload = findViewById(R.id.bt_download);
        findViewById(R.id.bt_download).setOnClickListener(this);
        mPresenter.showDfuProgressNotification(this);
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void initData() {
        super.initData();
        String newVersion = null;
        String currentVersion = null;
        switch (mVersionType) {
            case VERSION_TYPE_MONITOR:
                newVersion = AppManager.getVersionModel().getMonitorVersion().getVersion();
                currentVersion = DeviceManager.INSTANCE.getMonitorVersion();
                break;
            case VERSION_TYPE_SLEEPY:
                newVersion = AppManager.getVersionModel().getSleepyVersion().getVersion();
                currentVersion = DeviceManager.INSTANCE.getSleeperVersion();
                break;
            default:
                break;
        }

        mIvUpgrade.setImageResource(mIsLatestVersion ? R.mipmap.set_icon_download : R.mipmap.set_icon_success);
        mTvVersionLatest.setText(mIsLatestVersion ? String.format(Locale.getDefault(), getString(R.string.latest_version), newVersion) : getString(R.string.firmware_note_hint));
        mTvVersionCurrent.setText(String.format(Locale.getDefault(), getString(R.string.current_version_hint), currentVersion));
        mBtDownload.setText(mIsLatestVersion ? R.string.firmware_download_hint : R.string.firmware_upgrade_hint);
        mBtDownload.setVisibility(mIsLatestVersion ? View.VISIBLE : View.GONE);
    }

    private DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            mTvVersionCurrent.removeCallbacks(mDismissDialogRunnable);
            mTvVersionCurrent.postDelayed(mDismissDialogRunnable, DISMISS_DIALOG_DELAY);
            LogManager.appendUserOperationLog("设备 dfu 固件升级,设备正在连接中  mac=" + deviceAddress);
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,设备已连接上  mac=" + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,设备正在准备进度回调  mac=" + deviceAddress);
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,设备进度回调已开始  mac=" + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,正在进入 dfu模式  mac=" + deviceAddress);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed,
                                      float avgSpeed, int currentPart, int partsTotal) {
            LogManager.appendUserOperationLog("固件更新进度反馈   deviceAddress=" + deviceAddress + "  percent=" + percent);
            if (mVersionDialog != null) {
                mVersionDialog.updateProgress(percent);
            }
            if (percent == 1) {
                mTvVersionCurrent.removeCallbacks(mDismissDialogRunnable);
            }
            if (!(percent < 100)) {
                if (mVersionType == VERSION_TYPE_MONITOR) {
                    AppManager.getVersionModel().notifyMonitorDot(false);
                } else if (mVersionType == VERSION_TYPE_SLEEPY) {
                    AppManager.getVersionModel().notifySleepyDot(false);
                } else {
                    AppManager.getVersionModel().notifyAppDot(false);
                }
                runUiThread(() -> ToastHelper.show(R.string.firmware_upgrade_success_hint));
            }
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,正在验证固件文件中  mac=" + deviceAddress);

        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,正在断开连接中  mac=" + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级,已断开连接  mac=" + deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            cancelDialog();
            @StringRes int stringId = R.string.firmware_upgrade_success_hint;
            switch (mVersionType) {
                case VERSION_TYPE_MONITOR:
                    stringId = R.string.firmware_upgrade_success_hint;
                    break;
                case VERSION_TYPE_SLEEPY:
                    stringId = R.string.sleeper_firmware_upgrade_success_hint;
                    break;
            }
            ToastHelper.show(DeviceVersionUpgradeActivity.this, getString(stringId), Gravity.CENTER);
            LogManager.appendUserOperationLog("设备 dfu固件升级完成  mac=" + deviceAddress);
            AppManager.getVersionModel().notifyMonitorDot(false);
            AppManager.getVersionModel().notifySleepyDot(false);
            finish();
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            LogManager.appendUserOperationLog("设备 dfu 固件升级被终止  mac=" + deviceAddress);
            cancelDialog();
            ToastHelper.show("固件升级已被取消");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            if (mDfuCount > 2) {
                runUiThread(() -> {
                    ToastHelper.show(R.string.firmware_upgrade_failed_hint);
                    cancelDialog();
                });
            } else {
                mDfuCount++;
                runUiThread(() -> mPresenter.upgrade(mVersionType), 1000);
            }
            if (error == 4096) {
                AppManager.getBlueManager().refresh();
            }
            LogManager.appendUserOperationLog("设备 dfu 固件升级失败  mac=" + deviceAddress + "  error=" + error + "  errorMessage=" + message);
        }
    };

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.gallery_save_file_not_have_external_storage_permission, Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_WRITE_PERMISSION)
    private void downloadVersionFile() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ToastHelper.show(R.string.firmware_downloading_hint);
            mPresenter.downloadFile(mVersionType, mVersionType == VERSION_TYPE_MONITOR ? AppManager.getVersionModel().getMonitorVersion() : AppManager.getVersionModel().getSleepyVersion());
        } else {
            EasyPermissions.requestPermissions(this, "没有权限,你需要去设置中开启文件读写权限.", REQUEST_WRITE_PERMISSION, perms);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_download) {
            if (mBtDownload.getText().equals(getString(R.string.firmware_upgrade_hint))) {
                if (this.mobileBatteryLow()) {
                    LogManager.appendPhoneLog("手机电量不足50%,无法进行 dfu 升级");
                    showErrorDialog(R.string.phone_bettery_low_title, R.string.phone_bettery_low_message);
                    return;
                }
                switch (mVersionType) {
                    case VERSION_TYPE_MONITOR:
                        if (monitorBatteryLow()) {
                            LogManager.appendMonitorLog("监测仪电量不足50%,无法进行 dfu 升级");
                            showErrorDialog(R.string.monitor_bettery_low_title, R.string.monitor_bettery_low_message);
                            return;
                        } else {
                            initDialog(0x01);
                        }
                        break;
                    case VERSION_TYPE_SLEEPY:
                        if (sleepyBatterLow()) {
                            LogManager.appendSpeedSleeperLog("速眠仪电量不足50%,无法进行 dfu 升级");
                            showErrorDialog(R.string.sleeper_bettery_low_title, R.string.sleeper_bettery_low_message);
                            return;
                        }
                        break;
                }
                ToastHelper.show(this, getString(R.string.firmware_upgrade_ing_hint), Gravity.CENTER);
                mDfuCount++;
                mPresenter.upgrade(mVersionType);
            } else {
                initDialog(0x00);
                downloadVersionFile();
            }

        }
    }

    private void showErrorDialog(int title, int message) {
        new SumianAlertDialog(this)
                .hideTopIcon(true)
                .setTitle(title)
                .setMessage(message)
                .setRightBtn(R.string.confirm, null)
                .show();
    }

    @Override
    protected void onRelease() {
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        super.onRelease();
        mPresenter.release();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onDownloadStartCallback() {
    }

    @Override
    public void onDownloadProgress(int progress) {
        // Log.e(TAG, "onDownloadProgress: ------>" + progress);
        //0x00 固件下载 type
        mVersionDialog.updateProgress(progress);
    }

    @Override
    public void onDownloadFirmwareSuccess() {
        runUiThread(() -> {
            mIvUpgrade.setImageResource(R.mipmap.set_icon_upgrade);
            mBtDownload.setText(R.string.firmware_upgrade_hint);
            mVersionDialog.cancel();
            ToastHelper.show(R.string.firmware_download_success_hint);
        });
    }

    @Override
    public void onDownloadFirmwareFailed(String error) {
        runUiThread(() -> {
            mBtDownload.setText(R.string.firmware_download_hint);
            mVersionDialog.cancel();
            ToastHelper.show(error);
        });
    }

    @Override
    public void onCheckBluetoothAddressFailed() {
        mVersionDialog.cancel();
        ToastHelper.show("蓝牙设备地址不正确,无法连接蓝牙设备.请待设备退出固件升级模式之后重试....");
    }

    @Override
    public void onScanFailed(String deviceAddress) {
        mTvVersionCurrent.removeCallbacks(mDismissDialogRunnable);
        mDfuProgressListener.onError(deviceAddress, 0, DfuBaseService.ERROR_TYPE_OTHER, "未扫描到 对应 mac 地址的dfu 模式的并且有广播的设备");
    }

    @Override
    public void showSleepConnectingDialog() {
        Version2ConnectingDialog version2ConnectingDialog = Version2ConnectingDialog.Companion.newInstance();
        version2ConnectingDialog.show(getSupportFragmentManager(), Version2ConnectingDialog.class.getSimpleName());
        this.mVersion2ConnectingDialog = version2ConnectingDialog;
    }

    @Override
    public void dismissSleepConnectingDialog() {
        if (mVersion2ConnectingDialog != null) {
            mVersion2ConnectingDialog.dismissAllowingStateLoss();
        }
        initDialog(0x01);
    }


    private void cancelDialog() {
        if (mDismissDialogRunnable != null) {
            mTvVersionCurrent.removeCallbacks(mDismissDialogRunnable);
        }
        if (mVersionDialog != null) {
            mVersionDialog.cancel();
        }
        mDfuCount = 0;
    }


    private void initDialog(int dialogType) {
        VersionDialog versionDialog = VersionDialog.newInstance(dialogType, getString(R.string.firmware_download_title_hint));
        versionDialog.show(getSupportFragmentManager(), versionDialog.getClass().getSimpleName());
        this.mVersionDialog = versionDialog;
    }

    @Override
    public void setPresenter(VersionUpgradeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private boolean monitorBatteryLow() {
        return DeviceManager.INSTANCE.getMonitorBattery() < 50;
    }

    private boolean mobileBatteryLow() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        int batteryQuantity = (int) (batteryPct * 100);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        // 电量小于50，而且不在充电状态
        return batteryQuantity < 50 && !isCharging;
    }

    private boolean sleepyBatterLow() {
        return DeviceManager.INSTANCE.getSleeperBattery() < 50;
    }
}
