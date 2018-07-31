package com.sumian.app.upgrade.presenter;

import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.sumian.app.R;
import com.sumian.app.app.App;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.command.BlueCmd;
import com.sumian.app.common.util.HashUtils;
import com.sumian.app.log.LogManager;
import com.sumian.app.upgrade.activity.VersionUpgradeActivity;
import com.sumian.app.upgrade.bean.VersionInfo;
import com.sumian.app.upgrade.contract.VersionUpgradeContract;
import com.sumian.app.upgrade.service.DfuService;
import com.sumian.app.upgrade.wrapper.DfuWrapper;
import com.sumian.blue.callback.BluePeripheralDataCallback;
import com.sumian.blue.model.BluePeripheral;

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

public class VersionUpgradePresenter implements VersionUpgradeContract.Presenter, BluePeripheralDataCallback {

    private static final String TAG = VersionUpgradePresenter.class.getSimpleName();

    private WeakReference<VersionUpgradeContract.View> mViewWeakReference;
    private long mTaskId;
    private DownloadManager mDownloadManager;

    private Uri mDownloadedFileUri;

    private int mVersionType;
    private VersionInfo mVersionInfo;

    private boolean mIsEnableDfu;

    private String mDfuMac;

    private DfuWrapper mDfuWrapper;

    private DfuServiceController mDfuServiceController;

    private VersionUpgradePresenter(VersionUpgradeContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        // FirmwareManager.setOnDfuModeCallback(this);
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.addPeripheralDataCallback(this);
        }

        this.mDfuWrapper = new DfuWrapper();
    }

    public static VersionUpgradePresenter init(VersionUpgradeContract.View view) {
        return new VersionUpgradePresenter(view);
    }

    @Override
    public void release() {
        if (mVersionType == VersionUpgradeActivity.VERSION_TYPE_APP) {
            // Beta.unregisterDownloadListener();
        } else {
            BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral != null) {
                bluePeripheral.removePeripheralDataCallback(this);
            }
        }

        if (mDfuWrapper != null) {
            mDfuWrapper.release();
        }
    }

    //使用系统下载器下载
    private void downloadFile(String versionUrl, String versionName) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);

        LogManager.appendUserOperationLog("开始下载固件信息  url=" + versionUrl);

        //在通知栏中显示，默认就是显示的
        request.setTitle("download soft...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        //request.setDestinationInExternalPublicDir("/downloadFile/", versionName + ".zip");
        request.setDestinationInExternalPublicDir(App.getAppContext().getCacheDir().getAbsolutePath(), versionName + ".zip");
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径
        request.allowScanningByMediaScanner();

        //将下载请求加入下载队列
        mDownloadManager = (DownloadManager) App.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        if (mDownloadManager != null) {
            mTaskId = mDownloadManager.enqueue(request);
        }

        WeakReference<VersionUpgradeContract.View> viewWeakReference = this.mViewWeakReference;
        VersionUpgradeContract.View view = viewWeakReference.get();
        if (view != null) {
            view.onDownloadStartCallback();
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            DownloadManager.Query query = new DownloadManager.Query().setFilterById(mTaskId);
            Cursor cursor = null;
            try {
                cursor = mDownloadManager.query(query);
                if (cursor != null && cursor.moveToNext()) {

                    //下载状态
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    switch (downloadStatus) {
                        case DownloadManager.STATUS_PAUSED:
                            //Log.e(TAG, ">>>下载暂停");
                            break;
                        case DownloadManager.STATUS_PENDING:
                            //Log.e(TAG, ">>>下载延迟");
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            //Log.e(TAG, ">>>正在下载");
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
                                    view.onDownloadFirmwareFailed(App.getAppContext().getString(R.string.firmware_md5_invalid_hint));
                                }
                                LogManager.appendUserOperationLog("固件下载成功, MD5校验失败 fileMd5=" + fileMd5 + "  serverFileMd5=" + mVersionInfo.getMd5());
                            }

                            scheduledExecutorService.shutdown();
                            break;
                        case DownloadManager.STATUS_FAILED:
                            // Log.e(TAG, ">>>下载失败");
                            if (view != null) {
                                view.onDownloadFirmwareFailed(App.getAppContext().getString(R.string.firmware_download_failed_hint));
                            }

                            LogManager.appendUserOperationLog("固件下载失败 " + versionUrl);
                            break;
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void downloadFile(int versionType, VersionInfo versionInfo) {
        mVersionType = versionType;
        this.mVersionInfo = versionInfo;
        if (mVersionType == VersionUpgradeActivity.VERSION_TYPE_APP) {
//            Beta.registerDownloadListener(this);
//            DownloadTask downloadTask = Beta.startDownload();
//            File saveFile = downloadTask.getSaveFile();
//            if (saveFile != null && saveFile.isFile() && saveFile.exists()) {
//                VersionUpgradeContract.View view = mViewWeakReference.get();
//                if (view != null) {
//                    view.onDownloadFirmwareSuccess();
//                }
//            }
        } else {
            downloadFile(versionInfo.getUrl(), versionInfo.getVersion());
        }
    }

    @Override
    public void upgrade(int versionType) {
        Context context = App.getAppContext();
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        this.mVersionType = versionType;
        switch (versionType) {
            case VersionUpgradeActivity.VERSION_TYPE_APP:
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                //判断是否是AndroidN以及更高的版本
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig
//                        .APPLICATION_ID + ".fileProvider", Beta.getStrategyTask().getSaveFile());
//                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//                } else {
//                    intent.setDataAndType(Uri.fromFile(Beta.getStrategyTask().getSaveFile()), "application/vnd.android.package-archive");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                App.getAppContext().startActivity(intent);
                // Beta.installApk(Beta.getStrategyTask().getSaveFile());
                break;
            case VersionUpgradeActivity.VERSION_TYPE_MONITOR:
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
            case VersionUpgradeActivity.VERSION_TYPE_SLEEPY:
                if (!this.mIsEnableDfu) {
                    if (bluePeripheral != null && bluePeripheral.isConnected()) {
                        bluePeripheral.writeDelay(BlueCmd.cSleepyMac(), 200);
                        LogManager.appendSpeedSleeperLog("0x56 获取助眠仪的 mac 地址");
                    }
                } else {
                    LogManager.appendMonitorLog("开始进行助眠仪固件更新....");
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
            mDfuWrapper.scan2Connect(mDfuMac, () -> {
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
            }, () -> {
                VersionUpgradeContract.View view = mViewWeakReference.get();
                if (view != null) {
                    view.onScanFailed(mDfuMac);
                }
            });

        } else {
            VersionUpgradeContract.View view = mViewWeakReference.get();
            if (view != null) {
                view.onCheckBluetoothAddressFailed();
            }
        }
        // }, 500);
    }

    @Override
    public void showDfuProgressNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context);
        }
    }

    @Override
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

        String cmdIndex = BlueCmd.formatCmdIndex(cmd);
        switch (cmdIndex) {
            case "51"://监测仪自己固件 dfu 模式开启成功
                //CD:9D:C4:08:D8:9D --> CD:9D:C4:08:D8:9E
                LogManager.appendMonitorLog("0x51 监测仪dfu 模式开启成功....");
                this.mIsEnableDfu = true;

                doDfu(App.getAppContext(), mDfuMac);

                break;
            case "56"://获取监测仪绑定的速眠仪的 mac 地址
                String mac = cmd.substring(6);
                HwAppManager.getDeviceModel().setSleepyMac(mac);
                this.mDfuMac = HwAppManager.getDeviceModel().getSleepyDfuMac();
                peripheral.writeDelay(BlueCmd.cDoSleepyDfuMode(), 200);
                break;
            case "59"://使速眠仪进入 dfu 模式开启成功
                LogManager.appendMonitorLog("0x59 速眠仪dfu 模式开启成功....");
                this.mIsEnableDfu = true;
                doDfu(App.getAppContext(), mDfuMac);
                break;
            default:
                break;
        }
    }

    private void disconnect() {
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
    }
}
