package com.sumian.sd.buz.upgrade.presenter;

import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.blue.callback.BluePeripheralDataCallback;
import com.sumian.blue.manager.BlueManager;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.base.BaseViewModel;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.device.DeviceManager;
import com.sumian.sd.buz.device.command.BlueCmd;
import com.sumian.sd.buz.device.util.HashUtils;
import com.sumian.sd.buz.upgrade.activity.DeviceVersionUpgradeActivity;
import com.sumian.sd.buz.upgrade.bean.VersionInfo;
import com.sumian.sd.buz.upgrade.service.DfuService;
import com.sumian.sd.common.log.LogManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;

/**
 * Created by jzz
 * on 2017/11/1.
 * <p>
 * desc:
 */

public class DeviceVersionUpgradePresenter extends BaseViewModel implements BluePeripheralDataCallback {

    private static final String TAG = DeviceVersionUpgradePresenter.class.getSimpleName();

    private WeakReference<DeviceVersionUpgradeActivity> mViewWeakReference;
    private long mTaskId;
    private DownloadManager mDownloadManager;
    private Uri mDownloadedFileUri;
    private int mVersionType;
    private VersionInfo mVersionInfo;
    private boolean mIsEnableDfu;
    private String mDfuMac;
    private DfuServiceController mDfuServiceController;

    private DeviceVersionUpgradePresenter(DeviceVersionUpgradeActivity view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.addPeripheralDataCallback(this);
        }
    }

    public static DeviceVersionUpgradePresenter init(DeviceVersionUpgradeActivity view) {
        return new DeviceVersionUpgradePresenter(view);
    }

    @Override
    public void onCleared() {
        super.onCleared();
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.removePeripheralDataCallback(this);
        }
        AppManager.getBlueManager().stopScanForDevice();
    }

    //使用系统下载器下载
    private void downloadFile(String versionUrl, String versionName) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载
        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);
        LogManager.appendUserOperationLog("开始下载固件信息  url=" + versionUrl);
        //在通知栏中显示，默认就是显示的
        request.setTitle("download soft...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(App.Companion.getAppContext().getCacheDir().getAbsolutePath(), versionName + ".zip");
        request.allowScanningByMediaScanner();
        mDownloadManager = (DownloadManager) App.Companion.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        if (mDownloadManager != null) {
            mTaskId = mDownloadManager.enqueue(request);
        }
        WeakReference<DeviceVersionUpgradeActivity> viewWeakReference = this.mViewWeakReference;
        DeviceVersionUpgradeActivity view = viewWeakReference.get();
        if (view != null) {
            view.onDownloadStartCallback();
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(mTaskId);
            try (Cursor cursor = mDownloadManager.query(query)) {
                if (cursor != null && cursor.moveToNext()) {
                    //下载状态
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (downloadStatus) {
                        case DownloadManager.STATUS_PAUSED:
                            break;
                        case DownloadManager.STATUS_PENDING:
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            //已经下载文件大小
                            int downloadByteSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            //下载文件的总大小
                            int fileSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            if (view != null) {
                                view.onDownloadProgress((int) (downloadByteSize / (float) fileSize * 100));
                            }
                            // LogManager.appendUserOperationLog("固件下载进度  progress=" + (downloadByteSize / (float) fileSize * 100) + "  fileSize=" + fileSize);
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            //Log.e(TAG, ">>>下载完成");
                            //下载完成
                            if (view != null) {
                                view.onDownloadProgress(100);
                            }
                            this.mDownloadedFileUri = mDownloadManager.getUriForDownloadedFile(mTaskId);
                            String address = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                            // Log.e(TAG, "checkDownloadStatus: ------->" + address);
                            String fileMd5 = HashUtils.getMD5String(new File(address.replace("file://", "")));
                            boolean isMd5 = mVersionInfo.getMd5().equals(fileMd5);
                            if (isMd5) {//下载的包没有损坏
                                if (view != null) {
                                    view.onDownloadFirmwareSuccess();
                                }
                                LogManager.appendUserOperationLog("固件下载成功, MD5校验成功 fileMd5=" + fileMd5 + "  serverFileMd5=" + mVersionInfo.getMd5());
                            } else {//包已损坏需要重新下载
                                if (view != null) {
                                    view.onDownloadFirmwareFailed(App.Companion.getAppContext().getString(R.string.firmware_md5_invalid_hint));
                                }
                                LogManager.appendUserOperationLog("固件下载成功, MD5校验失败 fileMd5=" + fileMd5 + "  serverFileMd5=" + mVersionInfo.getMd5());
                            }
                            scheduledExecutorService.shutdown();
                            break;
                        case DownloadManager.STATUS_FAILED:
                            // Log.e(TAG, ">>>下载失败");
                            if (view != null) {
                                view.onDownloadFirmwareFailed(App.Companion.getAppContext().getString(R.string.firmware_download_failed_hint));
                            }
                            LogManager.appendUserOperationLog("固件下载失败 " + versionUrl);
                            break;
                    }
                }
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void downloadFile(int versionType, VersionInfo versionInfo) {
        mVersionType = versionType;
        this.mVersionInfo = versionInfo;
        downloadFile(versionInfo.getUrl(), versionInfo.getVersion());
    }

    public void upgrade(int versionType) {
        Context context = App.Companion.getAppContext();
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        this.mVersionType = versionType;
        switch (versionType) {
            case DeviceVersionUpgradeActivity.VERSION_TYPE_MONITOR:
                if (!this.mIsEnableDfu) {
                    if (bluePeripheral != null && bluePeripheral.isConnected()) {
                        this.mDfuMac = bluePeripheral.getDfuMac();
                        bluePeripheral.write(BlueCmd.cDoMonitorDfuMode());
                        LogManager.appendSpeedSleeperLog("0x51 使能监测仪进入 dfu 模式");
                    }
                } else {
                    LogManager.appendMonitorLog("开始进行监测仪固件更新....");
                    doDfu(context, mDfuMac);
                }
                break;
            case DeviceVersionUpgradeActivity.VERSION_TYPE_SLEEPY:
                if (!this.mIsEnableDfu) {
                    if (bluePeripheral != null && bluePeripheral.isConnected()) {
                        bluePeripheral.writeDelay(BlueCmd.cSleepyMac(), 200);
                        LogManager.appendSpeedSleeperLog("0x56 获取速眠仪的 mac 地址");
                    }
                } else {
                    LogManager.appendMonitorLog("开始进行速眠仪固件更新....");
                    doDfu(context, mDfuMac);
                }
                break;
            default:
                break;
        }
    }

    private void doDfu(Context context, String dfuMac) {
        //CD:9D:C4:08:D8:9D --> CD:9D:C4:08:D8:9E
        disconnect();
        // new Handler(Looper.getMainLooper()).postDelayed(() -> {

        boolean isBluetoothAddress = BluetoothAdapter.checkBluetoothAddress(dfuMac);
        if (isBluetoothAddress) {

            if (mVersionType == DeviceVersionUpgradeActivity.VERSION_TYPE_SLEEPY) {
                DeviceVersionUpgradeActivity view = mViewWeakReference.get();
                if (view != null) {
                    view.showSleepConnectingDialog();
                }
            }
            AppManager.getBlueManager().scanForDevice(mDfuMac, new BlueManager.ScanForDeviceListener() {
                @Override
                public void onDeviceFound(BluetoothDevice device) {
                    if (mVersionType == DeviceVersionUpgradeActivity.VERSION_TYPE_SLEEPY) {
                        DeviceVersionUpgradeActivity view = mViewWeakReference.get();
                        if (view != null) {
                            view.dismissSleepConnectingDialog();
                        }
                    }

                    mDfuServiceController = new DfuServiceInitiator(dfuMac)
                            //.setDeviceName(currentDevice.getName())
                            //.setKeepBond(true)
                            .setPacketsReceiptNotificationsEnabled(true)
                            .setPacketsReceiptNotificationsValue(5)
                            .setZip(mDownloadedFileUri)
                            .setDisableNotification(false)
                            .setForeground(true)
                            //.setForceDfu(true)
                            .start(context, DfuService.class);
                    LogManager.appendUserOperationLog("正在启动 dfu service......");
                }

                @Override
                public void onScanTimeout() {
                    DeviceVersionUpgradeActivity view = mViewWeakReference.get();
                    if (view != null) {
                        view.onScanFailed(mDfuMac);
                    }

                    if (mVersionType == DeviceVersionUpgradeActivity.VERSION_TYPE_SLEEPY) {
                        if (view != null) {
                            view.dismissSleepConnectingDialog();
                        }
                    }
                }
            });

        } else {
            DeviceVersionUpgradeActivity view = mViewWeakReference.get();
            if (view != null) {
                view.onCheckBluetoothAddressFailed();
            }
        }
        // }, 500);
    }

    public void showDfuProgressNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context);
        }
    }

    public void abort() {
        if (mDfuServiceController != null) {
            mDfuServiceController.abort();
        }
    }

    @Override
    public void onSendSuccess(BluePeripheral bluePeripheral, byte[] data) {

    }

    @Override
    public void onReceiveSuccess(BluePeripheral peripheral, byte[] data) {
        //55510188
        String cmd = BlueCmd.bytes2HexString(data);
        if (TextUtils.isEmpty(cmd) || cmd.length() < 2 || !"55".equals(cmd.substring(0, 2))) {
            //设备命令出问题
            //不是设备命令,有可能发生粘包,分包,拆包现象. 需要重新发送该命令,再次请求消息
            return;
        }
        //554F 05 01 1411 1E 15 02 0A 1411 1E 16 14 04 03
        //554F 05 05 1411 08 1F 07 0A 1411 08 1F 07 32 01
        //558f 0c 02d0 0001 faaf8bb8a55a3ee3
        //558E 15DC 01 21A56150 00AAC2E7
        //558E 15DC 0F 21A56150
        LogManager.appendMonitorLog("on receive upgrade cmd: " + cmd);
        String cmdIndex = BlueCmd.formatCmdIndex(cmd);
        switch (cmdIndex) {
            case "51"://监测仪自己固件 dfu 模式开启 相应
                onEnterMonitorDfuModeResponse(cmd);
                break;
            case "56"://获取监测仪绑定的速眠仪的 mac 地址
                String mac = cmd.substring(6);
                DeviceManager.INSTANCE.setSleeperMac(mac);
                this.mDfuMac = DeviceManager.INSTANCE.getSleeperDfuMac();
                peripheral.writeDelay(BlueCmd.cDoSleepyDfuMode(), 200);
                break;
            case "59"://使速眠仪进入 dfu 模式开启成功
                onEnterSleeperDfuResponse(cmd);
                break;
            default:
                break;
        }
    }

    private void onEnterMonitorDfuModeResponse(String cmd) {
        //55510188
        String respCode = cmd.substring(6, 8);
        // 成功：0x88
        // 失败：
        // 0xE1 -- 监测仪电量过低
        // 0xE2 -- 正在上传睡眠数据
        // 0xE3 -- 正在透传速眠仪 LOG 数据
        // 0xFF -- 其他
        boolean startDfuSuccess = false;
        int errorMessage = R.string.error_unknown;
        switch (respCode) {
            case "88":
                startDfuSuccess = true;
                break;
            case "e1"://监测仪电量过低
                errorMessage = R.string.monitor_energy_is_low_please_try_it_later;
                break;
            case "e2"://正在上传睡眠数据
                errorMessage = R.string.monitor_is_syncing_please_try_it_later;
                break;
            case "e3"://正在透传速眠仪 LOG 数据
                errorMessage = R.string.monitor_is_syncing_please_try_it_later;
                break;
            default:
                break;
        }
        if (startDfuSuccess) {
            LogManager.appendMonitorLog("0x51 监测仪dfu 模式开启成功...." + mDfuMac);
            this.mIsEnableDfu = true;
            doDfu(App.Companion.getAppContext(), mDfuMac);
            mViewWeakReference.get().showUpgradeDialog();
        } else {
            ToastUtils.showLong(errorMessage);
        }
    }

    private void onEnterSleeperDfuResponse(String cmd) {
        String respCode = cmd.substring(6, 8);
        boolean isEnterSuccess = false;
        int sleeperErrorMessage = R.string.error_unknown;
        switch (respCode) {
            case "88":
                isEnterSuccess = true;
                break;
            default:
                break;
        }
        if (isEnterSuccess) {
            LogManager.appendMonitorLog("0x59 速眠仪dfu 模式开启成功....mac=" + mDfuMac);
            this.mIsEnableDfu = true;
            doDfu(App.Companion.getAppContext(), mDfuMac);
            mViewWeakReference.get().showUpgradeDialog();
        } else {
            ToastUtils.showLong(sleeperErrorMessage);
        }
    }

    private void disconnect() {
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
    }
}
