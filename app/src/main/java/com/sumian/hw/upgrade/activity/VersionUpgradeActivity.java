package com.sumian.hw.upgrade.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.common.util.UiUtil;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.upgrade.contract.VersionUpgradeContract;
import com.sumian.hw.upgrade.dialog.VersionDialog;
import com.sumian.hw.upgrade.presenter.VersionUpgradePresenter;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.Locale;

import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by jzz
 * on 2017/10/27.
 * <p>
 * desc:固件升级提醒模块
 */

@SuppressWarnings("ConstantConditions")
public class VersionUpgradeActivity extends HwBaseActivity implements View.OnClickListener, TitleBar.OnBackListener
        , VersionUpgradeContract.View, DfuProgressListener {

    private static final String TAG = VersionUpgradeActivity.class.getSimpleName();

    private static final String EXTRA_VERSION_TYPE = "extra_version_type";
    private static final String EXTRA_VERSION_IS_LATEST = "extra_version_latest";

    private static final long DISMISS_DIALOG_DELAY = 1200L;

    public static final int VERSION_TYPE_APP = 0x01;
    public static final int VERSION_TYPE_MONITOR = 0x02;
    public static final int VERSION_TYPE_SLEEPY = 0x03;

    TitleBar mTitleBar;
    ImageView mIvUpgrade;
    TextView mTvVersionLatest;
    TextView mTvVersionCurrent;
    Button mBtDownload;

    private VersionUpgradeContract.Presenter mPresenter;

    private VersionDialog mVersionDialog;

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
        Intent intent = new Intent(context, VersionUpgradeActivity.class);
        intent.putExtra(EXTRA_VERSION_TYPE, versionType);
        intent.putExtra(EXTRA_VERSION_IS_LATEST, haveLatestVersion);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mVersionType = bundle.getInt(EXTRA_VERSION_TYPE);
        mIsLatestVersion = bundle.getBoolean(EXTRA_VERSION_IS_LATEST);
        VersionUpgradePresenter.init(this);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_version_upgrade;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mIvUpgrade = findViewById(R.id.iv_upgrade);
        mTvVersionLatest = findViewById(R.id.tv_version_latest);
        mTvVersionCurrent = findViewById(R.id.tv_version_current);
        mBtDownload = findViewById(R.id.bt_download);
        findViewById(R.id.bt_download).setOnClickListener(this);

        this.mTitleBar.addOnBackListener(this);
        if (mVersionType != VERSION_TYPE_APP) {
            mPresenter.showDfuProgressNotification(this);
            DfuServiceListenerHelper.registerProgressListener(this, this);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        String newVersion = null;
        String currentVersion = null;
        switch (mVersionType) {
            case VERSION_TYPE_APP:
                newVersion = AppManager.getVersionModel().getAppUpgradeInfo().version;
                currentVersion = UiUtil.getPackageInfo(App.Companion.getAppContext()).versionName;
                break;
            case VERSION_TYPE_MONITOR:
                newVersion = AppManager.getVersionModel().getMonitorVersion().getVersion();
                currentVersion = AppManager.getDeviceModel().getMonitorVersion();
                break;
            case VERSION_TYPE_SLEEPY:
                newVersion = AppManager.getVersionModel().getSleepyVersion().getVersion();
                currentVersion = AppManager.getDeviceModel().getSleepyVersion();
                break;
            default:
                break;
        }

        mIvUpgrade.setImageResource(mIsLatestVersion ? R.mipmap.ic_firmware_upgrade_icon_download : R.mipmap.ic_upgrade_icon_newest);
        mTvVersionLatest.setText(mIsLatestVersion ? String.format(Locale.getDefault(), getString(R.string.latest_version), newVersion) : getString(R.string.firmware_note_hint));
        mTvVersionCurrent.setText(String.format(Locale.getDefault(), getString(R.string.current_version_hint), currentVersion));
        mBtDownload.setText(mIsLatestVersion ? R.string.firmware_download_hint : R.string.firmware_upgrade_hint);
        mBtDownload.setVisibility(mIsLatestVersion ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_download) {
            if (mBtDownload.getText().equals(getString(R.string.firmware_upgrade_hint))) {
                if (mVersionType != VERSION_TYPE_APP) {
                    if (this.mobileBatteryLow()) {
                        ToastHelper.show("手机电量不足50%，请连接电源再升级");
                        LogManager.appendPhoneLog("手机电量不足50%,无法进行 dfu 升级");
                        return;
                    }
                    if (mVersionType == VERSION_TYPE_MONITOR && this.monitorBatteryLow()) {
                        ToastHelper.show("监测仪电量不足50%，请确保电量充足再升级");
                        LogManager.appendMonitorLog("监测仪电量不足50%,无法进行 dfu 升级");
                        return;
                    }
                    if (mVersionType == VERSION_TYPE_SLEEPY && this.sleepyBatterLow()) {
                        ToastHelper.show("速眠仪电量不足50%，请确保电量充足再升级");
                        LogManager.appendSpeedSleeperLog("速眠仪电量不足50%,无法进行 dfu 升级");
                        return;
                    }

                    ToastHelper.show(R.string.firmware_upgrade_ing_hint);
                    initDialog(0x01);
                }
                mDfuCount++;
                mPresenter.upgrade(mVersionType);
            } else {
                initDialog(0x00);
                if (mVersionType == VERSION_TYPE_APP) {
                    UiUtil.openAppInMarket(v.getContext());
                    return;
                } else {
                    ToastHelper.show(R.string.firmware_downloading_hint);
                    mPresenter.downloadFile(mVersionType, mVersionType == VERSION_TYPE_MONITOR ? AppManager.getVersionModel().getMonitorVersion() : AppManager.getVersionModel().getSleepyVersion());
                }
            }

        } else {
        }
    }

    @Override
    protected void onRelease() {
        DfuServiceListenerHelper.unregisterProgressListener(this, this);
        super.onRelease();
        mPresenter.release();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onDownloadStartCallback() {
        // runUiThread(() -> showFirmwareDialog(0x00));
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
            mIvUpgrade.setImageResource(R.mipmap.ic_firmware_upgrade_icon_upgrade);
            mBtDownload.setText(R.string.firmware_upgrade_hint);
            mVersionDialog.cancel();
            if (mVersionType == VERSION_TYPE_APP) {
                ToastHelper.show(R.string.app_download_success_hint);
            } else {
                ToastHelper.show(R.string.firmware_download_success_hint);
            }
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
        onError(deviceAddress, 0, DfuBaseService.ERROR_TYPE_OTHER, "未扫描到 对应 mac 地址的dfu 模式的并且有广播的设备");
    }

    @Override
    public void onDeviceConnecting(String deviceAddress) {
        mTvVersionCurrent.removeCallbacks(mDismissDialogRunnable);
        mTvVersionCurrent.postDelayed(mDismissDialogRunnable, DISMISS_DIALOG_DELAY);
        // Log.e(TAG, "onDeviceConnecting: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,设备正在连接中  mac=" + deviceAddress);
    }

    @Override
    public void onDeviceConnected(String deviceAddress) {
        // Log.e(TAG, "onDeviceConnected: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,设备已连接上  mac=" + deviceAddress);
    }

    @Override
    public void onDfuProcessStarting(String deviceAddress) {
        //  Log.e(TAG, "onDfuProcessStarting: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,设备正在准备进度回调  mac=" + deviceAddress);
    }

    @Override
    public void onDfuProcessStarted(String deviceAddress) {
        // Log.e(TAG, "onDfuProcessStarted: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,设备进度回调已开始  mac=" + deviceAddress);
    }

    @Override
    public void onEnablingDfuMode(String deviceAddress) {
        //   Log.e(TAG, "onEnablingDfuMode: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,正在进入 dfu模式  mac=" + deviceAddress);
    }

    @Override
    public void onProgressChanged(String deviceAddress, int percent, float speed,
                                  float avgSpeed, int currentPart, int partsTotal) {
        //Log.e(TAG, "onProgressChanged: ------>" + deviceAddress + "  percent=" + percent + "  speed=" + speed);
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
        // Log.e(TAG, "onFirmwareValidating: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,正在验证固件文件中  mac=" + deviceAddress);

    }

    @Override
    public void onDeviceDisconnecting(String deviceAddress) {
        // Log.e(TAG, "onDeviceDisconnecting: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,正在断开连接中  mac=" + deviceAddress);
    }

    @Override
    public void onDeviceDisconnected(String deviceAddress) {
        // Log.e(TAG, "onDeviceDisconnected: ------>" + deviceAddress);
        LogManager.appendUserOperationLog("设备 dfu 固件升级,已断开连接  mac=" + deviceAddress);
    }

    @Override
    public void onDfuCompleted(String deviceAddress) {
        //   Log.e(TAG, "onDfuCompleted: ------>" + deviceAddress);
        cancelDialog();
        ToastHelper.show(R.string.firmware_upgrade_success_hint);
        LogManager.appendUserOperationLog("设备 dfu固件升级完成  mac=" + deviceAddress);
        AppManager.getVersionModel().notifyMonitorDot(false);
        AppManager.getVersionModel().notifySleepyDot(false);
        finish();
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

    @Override
    public void onDfuAborted(String deviceAddress) {
        // Log.e(TAG, "onDfuAborted: ------>" + deviceAddress);
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

    private void initDialog(int dialogType) {
        VersionDialog versionDialog = VersionDialog.newInstance(dialogType,
                mVersionType == VERSION_TYPE_APP ? getString(R.string.app_download_title_hint) : getString(R.string.firmware_download_title_hint));
        versionDialog.show(getSupportFragmentManager(), versionDialog.getClass().getSimpleName());
        this.mVersionDialog = versionDialog;
    }

    @Override
    public void setPresenter(VersionUpgradeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    // private methods
    private boolean monitorBatteryLow() {
        return AppManager.getDeviceModel().getMonitorBattery() < 50;
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
        return AppManager.getDeviceModel().getSleepyBattery() < 50;
    }


}
