package com.sumian.hw.improve.device.presenter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.sumian.sleepdoctor.app.HwApp;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.HwAppManager;
import com.sumian.hw.command.BlueCmd;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.common.util.BlueByteUtil;
import com.sumian.hw.common.util.SpUtil;
import com.sumian.hw.common.util.StreamUtil;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.improve.device.bean.BlueDevice;
import com.sumian.hw.improve.device.contract.DeviceContract;
import com.sumian.hw.improve.device.model.DeviceModel;
import com.sumian.hw.improve.device.wrapper.BlueDeviceWrapper;
import com.sumian.hw.log.LogManager;
import com.sumian.blue.callback.BlueAdapterCallback;
import com.sumian.blue.callback.BluePeripheralCallback;
import com.sumian.blue.callback.BluePeripheralDataCallback;
import com.sumian.blue.constant.BlueConstant;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.blue.model.bean.BlueUuidConfig;
import com.sumian.sleepdoctor.utils.StorageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sm
 * on 2018/3/24.
 * <p>
 * desc: 蓝牙逻辑流程参考 com/sumian/app/improve/device/presenter/sync_data_sequence_v1.puml
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DevicePresenter implements DeviceContract.Presenter, BlueAdapterCallback, BluePeripheralDataCallback, BluePeripheralCallback {

    @SuppressWarnings("unused")
    private static final String TAG = DevicePresenter.class.getSimpleName();
    private static final String CACHE_FILE_NAME = "blue_device_cache.txt";
    private DeviceContract.View mView;
    private int mCurrentIndex = -1;
    private ArrayList<String> m8fTransData = new ArrayList<>(0);
    private BlueDevice mMonitor;
    private boolean mIsMonitoring;
    private boolean mIsUnbinding;
    private int mTranType;
    private String mBeginCmd;
    private long mReceiveStartedTime;
    private BlueDeviceWrapper mBlueDeviceWrapper;
    private int mTotalDataCount;
    private int mPackageNumber; // 透传数据 包的index

    private DevicePresenter(DeviceContract.View view) {
        mMonitor = new BlueDevice();
        mMonitor.name = HwApp.getAppContext().getString(R.string.monitor);
        BlueDevice speedSleeper = new BlueDevice();
        speedSleeper.name = HwApp.getAppContext().getString(R.string.speed_sleeper);
        mMonitor.speedSleeper = speedSleeper;

        view.setPresenter(this);
        this.mView = view;

        this.mBlueDeviceWrapper = new BlueDeviceWrapper();
        HwAppManager.getBlueManager().addBlueAdapterCallback(this);
    }

    public static void init(DeviceContract.View view) {
        new DevicePresenter(view);
    }

    @Override
    public boolean adapterIsEnable() {
        return HwAppManager.getBlueManager().isEnable();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public BlueDevice checkCache() {
        File cacheFile = new File(HwApp.getAppContext().getCacheDir(), CACHE_FILE_NAME);
        BlueDevice cacheBlueDevice = null;
        ObjectInputStream ois = null;
        if (cacheFile.exists()) {
            try {
                if (cacheFile.length() > 0) {
                    ois = new ObjectInputStream(new FileInputStream(cacheFile));
                    Object object = ois.readObject();
                    if (object != null && object instanceof BlueDevice) {
                        cacheBlueDevice = (BlueDevice) object;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                StreamUtil.close(ois);
            }
        } else {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cacheBlueDevice;
    }

    @Override
    public void doConnect(BlueDevice monitor) {

        HwAppManager.getBlueManager().clearBluePeripheral();

        BluetoothDevice remoteDevice = HwAppManager.getBlueManager().getBluetoothDeviceFromMac(monitor.mac);
        if (remoteDevice != null) {

            BlueUuidConfig blueUuidConfig = new BlueUuidConfig();

            blueUuidConfig.serviceUuid = BlueConstant.SERVICE_UUID;
            blueUuidConfig.notifyUuid = BlueConstant.NOTIFY_UUID;
            blueUuidConfig.writeUuid = BlueConstant.WRITE_UUID;
            blueUuidConfig.descUuid = BlueConstant.DESCRIPTORS_UUID;

            BluePeripheral bluePeripheral = new BluePeripheral.PeripheralBlueBuilder()
                .setContext(HwApp.getAppContext())
                .setBlueUuidConfig(blueUuidConfig)
                .setName(remoteDevice.getName())
                .setRemoteDevice(remoteDevice)
                .bindWorkThread(HwAppManager.getBlueManager().getWorkThread())
                .build();

            bluePeripheral.addPeripheralDataCallback(this);
            bluePeripheral.addPeripheralCallback(this);

            bluePeripheral.connect();

            LogManager.appendMonitorLog("主动连接监测仪  connect to   name=" + remoteDevice.getName() + "  address=" + remoteDevice.getAddress());
        } else {
            LogManager.appendMonitorLog("主动连接监测仪  connect to  is invalid   because  init bluetoothDevice is null");
        }
    }

    @Override
    public void doScan2Connect(BlueDevice monitor) {
        this.mMonitor = monitor;
        this.mMonitor.status = 0x01;
        notifyDeviceDataChanged();
        this.mBlueDeviceWrapper.scan2Connect(this.mMonitor, () -> doConnect(mMonitor), () -> {
            notifyDeviceDataChanged();
            ToastHelper.show("蓝牙连接失败多次,可尝试关闭手机蓝牙待5s后重试...");
        });
    }

    @Override
    public void turnOnSleep() {
        BluePeripheral bluePeripheral = getCurrentBluePeripheral();
        if (bluePeripheral == null) return;
        mView.showStartingPaMode();
        bluePeripheral.writeDelay(BlueCmd.cDoSleepyPaMode(), 500);

        LogManager.appendSpeedSleeperLog("主动 turn on  助眠仪 pa 模式");
    }

    @Override
    public void doSyncSleepData() {
        BluePeripheral bluePeripheral = getCurrentBluePeripheral();
        if (bluePeripheral == null) return;
        bluePeripheral.write(BlueCmd.cSleepData());
        mPackageNumber = 0;
        LogManager.appendTransparentLog("主动同步睡眠数据");
    }

    @Override
    public void doUnbind() {
        this.mIsUnbinding = true;
        BluePeripheral currentBluePeripheral = getCurrentBluePeripheral();
        if (currentBluePeripheral != null) {
            currentBluePeripheral.close();
        } else {
            mView.onUnbindCallback(mMonitor);
            clearCacheDevice();
        }

        HwAppManager.getBlueManager().clearBluePeripheral();
        mBlueDeviceWrapper.release();
        LogManager.appendMonitorLog("主动解绑监测仪");
    }

    @Override
    public void turnOnMonitoringMode(int monitoringMode) {
        BluePeripheral bluePeripheral = getCurrentBluePeripheral();
        if (bluePeripheral == null) return;

        bluePeripheral.write(BlueCmd.cDoMonitorMonitoringMode(monitoringMode));
    }

    @Override
    public BlueDevice getCurrentMonitor() {
        return mMonitor;
    }

    @Override
    public boolean isConnected() {
        return mMonitor != null && mMonitor.status > 0x01;
    }

    @Override
    public void onAdapterEnable() {
        mMonitor.status = 0x00;
        mMonitor.battery = 0;
        mMonitor.speedSleeper.status = 0x00;
        mMonitor.speedSleeper.battery = 0;
        notifyDeviceDataChanged();

        mView.onEnableAdapterCallback();
        LogManager.appendBluetoothLog("蓝牙 turn on");
    }

    @Override
    public void onAdapterDisable() {
        mMonitor.status = 0x00;
        mMonitor.battery = 0;

        mMonitor.speedSleeper.status = 0x00;
        mMonitor.speedSleeper.battery = 0;

        notifyDeviceDataChanged();

        mView.onDisableAdapterCallback();
        HwAppManager.getBlueManager().clearBluePeripheral();

        mBlueDeviceWrapper.release();

        LogManager.appendBluetoothLog("蓝牙 turn off");
    }

    @Override
    public void onSendSuccess(BluePeripheral bluePeripheral, byte[] data) {
        String cmd = BlueCmd.bytes2HexString(data);
        LogManager.appendBluetoothLog("蓝牙发送成功的指令  cmd=" + cmd);
        switch (cmd) {
            case "aa4f0101":
                LogManager.appendMonitorLog("0x4f 主动同步睡眠特征数据指令成功");
//                mMonitor.status = 0x03;
//                notifyDeviceDataChanged();
                break;
            case "aa570101":
                LogManager.appendMonitorLog("0x57 主动 turn on 监测仪的监测模式发送指令成功");
                break;
            case "aa570100":
                LogManager.appendMonitorLog("0x57 主动 turn off 监测仪的监测模式发送指令成功");
                break;
            case "aa580101":
                LogManager.appendSpeedSleeperLog("0x58 turn on 速眠仪的 pa 模式发送指令成功");
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceiveSuccess(BluePeripheral peripheral, byte[] data) {
        //     (Looper.myLooper() == Looper.getMainLooper()));
        String cmd = BlueCmd.bytes2HexString(data);
        if (TextUtils.isEmpty(cmd) || cmd.length() <= 2 || !"55".equals(cmd.substring(0, 2))) {
            //设备命令出问题
            //不是设备命令,有可能发生粘包,分包,拆包现象. 需要重新发送该命令,再次请求消息
            return;
        }
        //554F 05 01 1411 1E 15 02 0A 1411 1E 16 14 04 03
        //554F 05 05 1411 08 1F 07 0A 1411 08 1F 07 32 01
        //558f 0c 02d0 0001 faaf8bb8a55a3ee3

        //558E 15DC 01 21A56150 00AAC2E7
        //558E 15DC 0F 21A56150
        //558f 1033 08ffffffffffffffff
        String cmdIndex = BlueCmd.formatCmdIndex(cmd);
        switch (cmdIndex) {
            case "40"://校正时区
                receiveSyncTimeSuccessCmd();
                break;
            case "44"://获取监测仪电量
                receiveMonitorBatteryInfo(cmd);
                break;
            case "45"://获取速眠仪电量
                receiveSleeperBatteryInfo(cmd);
                break;
            case "4b":
                receiveSetUserInfoResult(cmd);
                break;
            case "4e"://获取速眠仪的连接状态
                receiveSleeperConnectionStatus(cmd);
                break;
            case "4f"://主动获取睡眠特征数据
                receiveRequestSleepDataResponse(peripheral, cmd);
                break;
            case "50"://获取监测仪固件版本信息
                receiveMonitorVersionInfo(cmd);
                break;
            case "59"://使速眠仪进入 dfu 模式开启成功
                receiveSleeperEnterDfuSuccessResponse(cmd);
                break;
            case "51"://监测仪自己固件 dfu 模式开启成功
                receiveMonitorEnterDfuSuccessResponse(cmd);
                break;
            case "52":
                LogManager.appendBluetoothLog("正在绑定速眠仪中," + cmd);
                break;
            case "53"://获取监测仪的 sn 号
                receiveMonitorSnInfo(data, cmd);
                break;
            case "54"://获取速眠仪的固件版本信息
                receiveSleeperVersionInfo(cmd);
                break;
            case "55"://获取监测仪绑定的并且连接着的速眠仪的 sn 号
                receiveSleeperSnInfo(data, cmd);
                break;
            case "56"://获取监测仪绑定的速眠仪的 mac 地址
                receiveSleeperMacInfo(cmd);
                break;
            case "57"://开启/关闭监测仪的监测模式  0x01 开启  0x00 关闭
                receiveTurnOnOffMonitoringModeResponse(cmd);
                break;
            case "58"://使速眠仪进入 pa 模式之后的反馈
                receiveSleeperEnterPaModeResponse(cmd);
                break;
            case "61"://同步到的监测仪的所有状态,以及与之绑定的速眠仪的所有状态
                receiveAllMonitorAndSleeperStatus(peripheral, data, cmd);
                break;
            case "d0"://临床原始数据采集时间点
                long unixTime = Long.parseLong(cmd.substring(4, 12), 16);
                FileHelper.updateFileDate(unixTime);
                break;
            case "d1"://采集临床肌电数据   不回响应包
                String emg = BlueByteUtil.formatData(data);
                FileHelper.appendEmgContent(emg);
                break;
            case "d2"://采集临床脉率数据   不回响应包
                String pulse = BlueByteUtil.formatData(data);
                FileHelper.appendPulseContent(pulse);
                break;
            case "d3"://采集临床加速度数据  不回响应包
                String speed = BlueByteUtil.formatData(data);
                FileHelper.appendSpeedContent(speed);
                break;
            case "8e": // 开始/结束 透传数据
                receiveStartOrFinishTransportCmd(peripheral, data, cmd);
                break;
            case "8f": // 透传数据
                receiveSleepData(peripheral, data, cmd);
                break;
            default:
                peripheral.write(BlueCmd.cResponseOk(data[1]));
                break;
        }
    }

    private void receiveSleepData(BluePeripheral peripheral, byte[] data, String cmd) {
        int indexOne = (Integer.parseInt(cmd.substring(4, 6), 16) & 0x0f) << 8;
        int indexTwo = Integer.parseInt(cmd.substring(6, 8), 16);
        int index = indexOne + indexTwo;

        if (mCurrentIndex == -1) {
            mCurrentIndex = index;
            peripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8f, 0x03, data[2], data[3], (byte) 0x88});
            m8fTransData.add(cmd);
        } else {
            if (index == mCurrentIndex + 1) {
                mCurrentIndex = index;
                peripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8f, 0x03, data[2], data[3], (byte) 0x88});
                m8fTransData.add(cmd);
            } else if (index > mCurrentIndex + 1) {
                peripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8f, 0x03, data[2], data[3], (byte) 0xff});
                LogManager.appendTransparentLog("index=" + index + "  realCount=" + mCurrentIndex + "  该index 出错,要求重传 cmd=" + cmd);
            }
            //else if (index <= mCurrentIndex) {
            // Log.e(TAG, "onReceiveSuccess: --------不处理该情况------>")
            // return;
            //}
        }
        //558f 1033 08ffffffffffffffff
        //   2byte  前四位为类型  后16位数据报文长度
        //(（a &0xF）<< 8) + b
        onSyncDataProgressChange(index, mTotalDataCount);
        //LogManager.appendTransparentLog("0x8f index=" + index + "  indexCount=" + mCurrentIndex + "  cmd=" + cmd);
    }

    private void receiveStartOrFinishTransportCmd(BluePeripheral peripheral, byte[] data, String cmd) {
        int typeAndCount = Integer.parseInt(cmd.substring(4, 8), 16);
        //16 bit 包括4bit 类型 12bit 长度 向右移12位,得到高4位的透传数据类型
        int tranType = typeAndCount >> 12;
        int dataCount;
        LogManager.appendFormatPhoneLog("receiveStartOrFinishTransportCmd: %d", data[4]);
        switch (data[4]) {
            case 0x01: //开始透传
                mCurrentIndex = -1;
                mMonitor.status = BlueDevice.STATUS_SYNCHRONIZING;
                if (!m8fTransData.isEmpty()) {
                    m8fTransData.clear();
                }
                mTranType = tranType;
                mBeginCmd = cmd;
                mReceiveStartedTime = getActionTime();
                //558e 1 2d0 01 5a1562f6 00ab1107
                mTotalDataCount = getDataCountFromCmd(cmd);
                dataCount = mTotalDataCount;
                if (isAvailableStorageEnough(dataCount)) {
                    writeResponse(peripheral, data, true);
                    mPackageNumber++;
                    LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,等待设备透传 " + dataCount + "包数据" + "  cmd=" + cmd);
                } else {
                    writeResponse(peripheral, data, false);
                    LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,磁盘空间不足 " + dataCount + "包数据" + "  cmd=" + cmd);
                }
                HwAppManager.getDeviceModel().notifyStartSyncSleepData();
                notifyDeviceDataChanged();
                break;
            case 0x0f:// 结束。透传8f 数据接收完成,保存文件,准备上传数据到后台
                HwAppManager.getDeviceModel().notifyFinishSyncSleepData();
                //  Log.e(TAG, "onReceiveSuccess: --------8e 0f--->" + cmd + "  count=" + mTransCount);
                ////558e 1 2d0 0f 5a15627e
                dataCount = getDataCountFromCmd(cmd);
                //if (dataCount == 0) {
                //   mMonitor.status = mIsMonitoring ? 0x05 : 0x02;
                //   mView.syncDeviceSleepChaFailed();
                //}
                if (dataCount == m8fTransData.size()) {
                    mMonitor.status = mIsMonitoring ? BlueDevice.STATUS_MONITORING : BlueDevice.STATUS_CONNECTED;
                    @SuppressWarnings("unchecked") ArrayList<String> sleepData = (ArrayList<String>) m8fTransData.clone();
                    m8fTransData.clear();
                    if (isAvailableStorageEnough(dataCount)) {
                        SpUtil.initEdit("upload_sleep_cha_time").putLong("time", System.currentTimeMillis()).apply();
                        LogManager.appendMonitorLog("0x8e0f 透传数据" + dataCount + "包接收成功,准备写入本地文件 cmd=" + cmd);
                        HwAppManager.getJobScheduler()
                            .saveSleepData(sleepData, mTranType, mBeginCmd, cmd,
                                HwAppManager.getDeviceModel().getMonitorSn(),
                                HwAppManager.getDeviceModel().getSleepySn(),
                                mReceiveStartedTime, getActionTime());
                        writeResponse(peripheral, data, true);
                    } else {
                        writeResponse(peripheral, data, false);
                        LogManager.appendMonitorLog("0x8e01 缓冲区初始化完毕,磁盘空间不足 " + dataCount + "包数据" + "  cmd=" + cmd);
                        break;
                    }
                } else {
                    LogManager.appendMonitorLog(
                        "0x8e0f 透传数据" + dataCount + "包接收失败,原因是包数量不一致 实际收到包数量 RealDataCount="
                            + mCurrentIndex + " 重新透传数据已准备,等待设备重新透传  cmd=" + cmd);
                    mCurrentIndex = -1;
                    this.m8fTransData.clear();
                    writeResponse(peripheral, data, false);
                }
                notifyDeviceDataChanged();
                break;
            default:
                break;
        }
    }

    private void receiveAllMonitorAndSleeperStatus(BluePeripheral peripheral, byte[] data, String cmd) {
        //byte1表示监测仪的监测模式状态
        //byte2表示速眠仪的 pa 模式状态
        //5561 xx 01 01
        //Log.e(TAG, "onReceive: -----61------sync monitor sleepy state---->" + cmd);
        int stateLen = Integer.parseInt(cmd.substring(4, 6), 16);
        String allState = cmd.substring(6);
        if (stateLen == (allState.length() / 2)) {//判断所有状态是否一致
            peripheral.write(BlueCmd.cResponseOk(data[1]));
            int monitorSnoopingModeState = Integer.parseInt(allState.substring(0, 2), 16);
            if (monitorSnoopingModeState == 0x01) {//独立监测模式
                mMonitor.status = 0x05;
                mIsMonitoring = true;
            } else {
                mMonitor.status = 0x02;
                mIsMonitoring = false;
            }
            LogManager.appendMonitorLog("0x61 收到监测仪的监测模式变化 监测模式=" + monitorSnoopingModeState + "  cmd=" + cmd);
            int sleepyPaModeState = Integer.parseInt(allState.substring(2, 4), 16);
            if (sleepyPaModeState > 0) {
                mMonitor.speedSleeper.status = 0x04;
            } else {
                if (mMonitor.speedSleeper.status > 0x01) {
                    mMonitor.speedSleeper.status = 0x02;
                }
            }
            notifyDeviceDataChanged();
            LogManager.appendSpeedSleeperLog("0x61 收到速眠仪的pa 模式变化  pa 模式=" + sleepyPaModeState + "  cmd=" + cmd);
            HwAppManager.getDeviceModel().setMonitorSnoopingModeState(monitorSnoopingModeState);//监测仪的监测模式状态
            HwAppManager.getDeviceModel().setSleepyPaModeState(sleepyPaModeState);//获取速眠仪的 pa 模式状态
        } else {//指令出错了,需重发所有状态
            peripheral.write(BlueCmd.cResponseFailed(data[1]));
            LogManager.appendMonitorLog("0x61  监测仪与速眠仪反馈模式变化的指令不正确  cmd=" + cmd);
        }
    }

    private void receiveSleeperEnterPaModeResponse(String cmd) {
        //aa58 01  默认 app 只能开启 pa 模式,不可以关闭
        //  Log.e(TAG, "onReceive: -----set   pa----->" + cmd);
        //5558 01 88/e0/e1/e2/e3/e4/e5/e6/e7/ff
        // 0x88 -- 设置成功
        // 0xE0 -- 监测仪未连接速眠仪
        // 0xE1 -- 监测仪未佩戴
        // 0xE2 -- 头部未在枕头上
        // 0xE3 -- 独立模式已开启,不能开启 pa 模式,提醒先关闭监测仪独立监测模式
        // 0xE4 -- 用户已经处于睡眠状态
        // 0xE5 -- 设置参数错误
        // 0xE6 -- 设置数据长度错误
        // 0xE7 -- 发送数据到速眠仪发生错误
        // 0xFF -- 未知错误
        if (cmd.length() == 8) {
            switch (cmd) {
                case "55580188"://开启pa成功
                    mMonitor.speedSleeper.status = 0x04;
                    notifyDeviceDataChanged();
                    mView.showTurnOnPaModeSuccess();
                    LogManager.appendSpeedSleeperLog("0x58 开启速眠仪的 pa 模式成功  cmd=" + cmd);
                    break;
                case "555801ff"://各种 pa 错误
                default:
                    @StringRes int errorTextId;
                    String errorCode = cmd.substring(6);
                    switch (errorCode) {
                        case "e0":
                            errorTextId = R.string.sleepy_not_connected_monitor;
                            break;
                        case "e1":
                            errorTextId = R.string.not_wear_monitor;
                            break;
                        case "e2":
                            errorTextId = R.string.head_not_at_pillow;
                            break;
                        case "e3":
                            errorTextId = R.string.not_turn_on_pa_mode;
                            break;
                        case "e4":
                            errorTextId = R.string.not_turn_on_pa_mode_in_sleep_time;
                            break;
                        case "e8":
                            errorTextId = R.string.monitor_is_charging;
                            break;
                        case "e5":
                        case "e6":
                        case "e7":
                        case "ff":
                        default:
                            errorTextId = R.string.turn_on_sleepy_pa_mode_error;
                            break;
                    }
                    mView.showTurnOnPaModeFailed(errorTextId);
                    LogManager.appendSpeedSleeperLog("0x58 开始速眠的 pa 模式失败,原因是" + HwApp.getAppContext().getResources().getString(errorTextId) + "  cmd=" + cmd);
                    break;
            }
        } else {
            mView.showTurnOnPaModeFailed(R.string.turn_on_sleepy_pa_mode_error);
            LogManager.appendSpeedSleeperLog("0x58 开启速眠仪的 pa 模式失败,返回的指令长度不为8  cmd=" + cmd);
        }
    }

    private void receiveTurnOnOffMonitoringModeResponse(String cmd) {
        //55 57 01  88
        //55 57 01  ff
        int monitorSnoopingState = HwAppManager.getDeviceModel().getMonitorSnoopingModeState();
        switch (cmd) {
            case "55570188"://操作成功
                if (mMonitor.status == BlueDevice.STATUS_MONITORING) {
                    mMonitor.status = BlueDevice.STATUS_CONNECTED;
                    mIsMonitoring = false;
                } else {
                    mMonitor.status = BlueDevice.STATUS_MONITORING;
                    mIsMonitoring = true;
                }
                if (monitorSnoopingState == DeviceModel.MONITOR_SNOOPING_STATE) {
                    monitorSnoopingState = DeviceModel.MONITOR_IDLE_SNOOPING_STATE;
                } else {
                    monitorSnoopingState = DeviceModel.MONITOR_SNOOPING_STATE;
                }
                HwAppManager.getDeviceModel().setMonitorSnoopingModeState(monitorSnoopingState);
                if (monitorSnoopingState == 0x01) {
                    LogManager.appendMonitorLog("0x57 开启监测仪的监测模式成功 cmd=" + cmd);
                } else {
                    LogManager.appendMonitorLog("0x57 关闭监测仪的监测模式成功 cmd=" + cmd);
                }
                break;
            case "555701ff"://操作失败
            default:
                mIsMonitoring = false;
                mMonitor.status = 0x02;
                LogManager.appendMonitorLog("0x57 操作(开启/关闭)监测仪的监测模式失败  cmd=" + cmd);
                break;
        }
        notifyDeviceDataChanged();
        //  Log.e(TAG, "onReceive: --57-----set  monitor  snooping mode---->" + cmd);
    }

    private void receiveSleeperMacInfo(String cmd) {
        String mac = cmd.substring(6);
        HwAppManager.getDeviceModel().setSleepyMac(mac);
        // String formatMac = BlueCmd.formatMac(data);
        long oldMac = Long.parseLong(mac, 16);
        long newMac = ((oldMac & 0xff) + 1) + ((oldMac >> 8) << 8);
        StringBuilder macSb = new StringBuilder();
        macSb.delete(0, macSb.length());
        String hexString = Long.toHexString(newMac);
        if (!TextUtils.isEmpty(hexString) && hexString.length() >= 2) {
            for (int i = 0, len = hexString.length(); i < len; i++) {
                if (i % 2 == 0) {
                    macSb.append(hexString.substring(i, i + 2));
                    if (i != len - 2) {
                        macSb.append(":");
                    }
                }
            }
            mMonitor.speedSleeper.mac = macSb.toString().toUpperCase(Locale.getDefault());
        }

        LogManager.appendSpeedSleeperLog("获取到监测仪绑定的速眠仪的 mac address=" + macSb.toString().toUpperCase(Locale.getDefault()) + "  cmd=" + cmd);
    }

    private void receiveSleeperSnInfo(byte[] data, String cmd) {
        String sleepySn = BlueCmd.formatSn(data);
        // Log.e(TAG, "onReceive: -----55---sleepy sn---->" + sleepySn);
        mMonitor.speedSleeper.sn = sleepySn;
        HwAppManager.getDeviceModel().setSleepySn(sleepySn);
        LogManager.appendSpeedSleeperLog("获取到监测仪绑定的速眠仪的 sn=" + sleepySn + "  cmd=" + cmd);
    }

    private void receiveSleeperVersionInfo(String cmd) {
        int sleepyFirmwareVersionOne = Integer.parseInt(cmd.substring(6, 8), 16);
        int sleepyFirmwareVersionTwo = Integer.parseInt(cmd.substring(8, 10), 16);
        int sleepyFirmwareVersionThree = Integer.parseInt(cmd.substring(10, 12), 16);
        String sleepyFirmwareVersion = sleepyFirmwareVersionOne + "." + sleepyFirmwareVersionTwo + "." +
            sleepyFirmwareVersionThree;
        // Log.e(TAG, "onReceive: ---sleepy  version---->" + sleepyFirmwareVersion);
        LogManager.appendSpeedSleeperLog("速眠仪的固件版本信息" + sleepyFirmwareVersion + "  cmd=" + cmd);
        HwAppManager.getDeviceModel().setSleepyVersion(sleepyFirmwareVersion);
    }

    private void receiveMonitorSnInfo(byte[] data, String cmd) {
        //55 53 0e 413031323334353637383930  valid sn
        //55 53 0e 410000000000000000000000  null
        // Log.e(TAG, "onReceive: -----53---->" + cmd);
        String monitorSn = BlueCmd.formatSn(data);
        //  Log.e(TAG, "onReceive: -----53---monitor sn---->" + monitorSn);
        mMonitor.sn = monitorSn;
        //notifyDeviceDataChanged();
        HwAppManager.getDeviceModel().setMonitorSn(monitorSn);
        LogManager.appendMonitorLog("获取到监测仪绑定的速眠仪的 sn=" + monitorSn + "  cmd=" + cmd);
    }

    private void receiveMonitorEnterDfuSuccessResponse(String cmd) {
        mMonitor.status = 0x00;
        mMonitor.battery = 0;
        mMonitor.speedSleeper.status = 0x00;
        mMonitor.speedSleeper.battery = 0;
        notifyDeviceDataChanged();
        LogManager.appendSpeedSleeperLog("0x51 速眠仪进入dfu 模式成功  cmd=" + cmd);
    }

    private void receiveSleeperEnterDfuSuccessResponse(String cmd) {
        mMonitor.status = 0x00;
        mMonitor.battery = 0;
        mMonitor.speedSleeper.status = 0x00;
        mMonitor.speedSleeper.battery = 0;
        notifyDeviceDataChanged();
        LogManager.appendMonitorLog("0x50 监测仪进入dfu 模式成功 cmd=" + cmd);
    }

    private void receiveMonitorVersionInfo(String cmd) {
        int monitorFirmwareVersionOne = Integer.parseInt(cmd.substring(6, 8), 16);
        int monitorFirmwareVersionTwo = Integer.parseInt(cmd.substring(8, 10), 16);
        int monitorFirmwareVersionThree = Integer.parseInt(cmd.substring(10, 12), 16);
        String monitorFirmwareVersion = monitorFirmwareVersionOne + "." + monitorFirmwareVersionTwo + "." + monitorFirmwareVersionThree;
        HwAppManager.getDeviceModel().setMonitorVersion(monitorFirmwareVersion);
        //  Log.e(TAG, "onReceive: ------monitor  version------>" + monitorFirmwareVersion);
        LogManager.appendSpeedSleeperLog("速眠仪的固件版本信息" + monitorFirmwareVersion + "  cmd=" + cmd);
    }

    private void receiveRequestSleepDataResponse(BluePeripheral peripheral, String cmd) {
        mMonitor.mac = peripheral.getMac();
        switch (cmd) {
            case "554f020188":
                mMonitor.status = BlueDevice.STATUS_SYNCHRONIZING;
                LogManager.appendTransparentLog("收到0x4f回复 发现设备有睡眠特征数据,准备同步中  cmd=" + cmd);
                break;
            case "554f020100":
                mMonitor.status = mIsMonitoring ? BlueDevice.STATUS_MONITORING : BlueDevice.STATUS_CONNECTED;
                mView.syncDeviceSleepChaSuccess();
                LogManager.appendTransparentLog("收到0x4f回复 设备没有睡眠特征数据  cmd=" + cmd);
                SpUtil.initEdit("upload_sleep_cha_time").putLong("time", System.currentTimeMillis()).apply();
                break;
            case "554f0201ff":
                mMonitor.status = mIsMonitoring ? BlueDevice.STATUS_MONITORING : BlueDevice.STATUS_CONNECTED;
                mView.syncDeviceSleepChaFailed();
                LogManager.appendTransparentLog("收到0x4f回复 设备4f 指令识别异常  cmd=" + cmd);
                break;
            default:
                break;
        }
        notifyDeviceDataChanged();
    }

    private void receiveSleeperConnectionStatus(String cmd) {
        // Log.e(TAG, "onReceive: ---sleepy  connected state------->" + cmd);
        int sleepyConnectState = Integer.parseInt(cmd.substring(cmd.length() - 2), 16);
        if (sleepyConnectState == 0x00) {
            mMonitor.speedSleeper.status = 0x00;
            mMonitor.speedSleeper.battery = 0x00;
        } else {
            if (mMonitor.speedSleeper.status <= 0x01) {
                mMonitor.speedSleeper.status = 0x02;
            }
        }
        notifyDeviceDataChanged();
        HwAppManager.getDeviceModel().setSleepyConnectState(sleepyConnectState);
        LogManager.appendSpeedSleeperLog("收到速眠仪的连接状态变化------>" + sleepyConnectState + "  cmd=" + cmd);
    }

    private void receiveSetUserInfoResult(String cmd) {
        if ("554b0188".equals(cmd)) {
            LogManager.appendMonitorLog("对设备设置  用户信息成功...." + cmd);
        } else {
            LogManager.appendMonitorLog("对设备设置 用户信息失败..." + cmd);
        }
    }

    private void receiveSleeperBatteryInfo(String cmd) {
        int sleepyBattery = Integer.parseInt(cmd.substring(cmd.length() - 2), 16);
        // Log.e(TAG, "onReceive: -----sleepy  battery---->" + sleepyBattery);
        mMonitor.speedSleeper.battery = sleepyBattery;
        notifyDeviceDataChanged();
        HwAppManager.getDeviceModel().setSleepyBattery(sleepyBattery);
        LogManager.appendMonitorLog("收到助眠仪的电量变化---->" + sleepyBattery + "  cmd=" + cmd);
    }

    private void receiveMonitorBatteryInfo(String cmd) {
        int monitorBattery = Integer.parseInt(cmd.substring(cmd.length() - 2), 16);
        mMonitor.battery = monitorBattery;
        mMonitor.status = 0x02;
        notifyDeviceDataChanged();
        HwAppManager.getDeviceModel().setMonitorBattery(monitorBattery);
        LogManager.appendMonitorLog("收到监测仪的电量变化---->" + monitorBattery + "  cmd=" + cmd);
    }

    private void receiveSyncTimeSuccessCmd() {
        // Log.e(TAG, "onReceive: ---40---->" + cmd);
        LogManager.appendMonitorLog("收到0x40 监测仪校正时区成功");
    }

    private void onSyncDataProgressChange(int current, int total) {
        HwAppManager.getDeviceModel().updateSyncSleepDataProgressAndNotifyListeners(mPackageNumber, current, total);
    }

    /**
     * @param peripheral       peripheral
     * @param data             data
     * @param readyForNextData true 准备接受, false 异常
     */
    private void writeResponse(BluePeripheral peripheral, byte[] data, boolean readyForNextData) {
        byte[] command = {(byte) 0xaa, (byte) 0x8e, data[2], data[3], data[4], data[5], data[6], data[7], data[8], readyForNextData ? (byte) 0x88 : (byte) 0xff};
        peripheral.write(command);
    }

    private boolean isAvailableStorageEnough(int dataCount) {
        // dataCount + 2 表示加上了startCmd和endCmd
        // 每行命令有26个字符，加上换行，共27字符，一个字符占1byte，考虑安全性，这里计算时按30byte/cmd计算
        // 实测10000个cmd，文件273kb；
        long dataBytes = (dataCount + 2) * 30L;
        long availableExternalStorageSize = StorageUtil.getAvailableExternalStorageSize();
        return dataBytes < availableExternalStorageSize;
    }

    /**
     * 获取该次透传数据总条数
     *
     * @return 透传数据总条数
     */
    private int getDataCountFromCmd(String cmd) {
        return Integer.parseInt(cmd.substring(5, 8), 16);
    }

    @Override
    public void onConnecting(BluePeripheral peripheral, int connectState) {
        mMonitor.mac = peripheral.getMac();
        mMonitor.status = 0x01;
        mMonitor.battery = 0;

        mMonitor.speedSleeper.status = 0x00;
        mMonitor.speedSleeper.battery = 0;

        notifyDeviceDataChanged();

        LogManager.appendMonitorLog("监测仪正在连接中 " + peripheral.getName());
    }

    @Override
    public void onConnectSuccess(BluePeripheral peripheral, int connectState) {
        mMonitor.mac = peripheral.getMac();
        mMonitor.status = 0x01;
        mMonitor.battery = 0;

        notifyDeviceDataChanged();

        HwAppManager.getBlueManager().saveBluePeripheral(peripheral);

        LogManager.appendMonitorLog("监测仪连接成功 " + peripheral.getName());
    }

    @Override
    public void onConnectFailed(BluePeripheral peripheral, int connectState) {
        mMonitor.mac = peripheral.getMac();
        mMonitor.status = 0x00;
        mMonitor.battery = 0;

        notifyDeviceDataChanged();

        HwAppManager.getBlueManager().refresh();

        LogManager.appendMonitorLog("监测仪连接失败 " + peripheral.getName());
    }

    @Override
    public void onDisconnecting(BluePeripheral peripheral, int connectState) {
        mMonitor.mac = peripheral.getMac();
        mMonitor.status = 0x00;
        mMonitor.battery = 0;

        mMonitor.speedSleeper.status = 0x00;
        mMonitor.speedSleeper.battery = 0;

        notifyDeviceDataChanged();

        HwAppManager.getBlueManager().refresh();

        LogManager.appendMonitorLog("监测仪正在断开连接 " + peripheral.getName());
    }

    @Override
    public void onDisconnectSuccess(BluePeripheral peripheral, int connectState) {
        if (mIsUnbinding) {
            mMonitor.mac = null;
            mMonitor.status = 0x00;
            mMonitor.battery = 0;

            mMonitor.speedSleeper.status = 0x00;
            mMonitor.speedSleeper.battery = 0;

            notifyDeviceDataChanged();

            clearCacheDevice();

            mView.onUnbindCallback(mMonitor);
            mIsUnbinding = false;

            LogManager.appendMonitorLog("解绑监测仪成功 " + peripheral.getName());

        } else {
            mIsUnbinding = false;
            mMonitor.mac = peripheral.getMac();
            mMonitor.status = 0x00;
            mMonitor.battery = 0;

            mMonitor.speedSleeper.status = 0x00;
            mMonitor.speedSleeper.battery = 0;

            notifyDeviceDataChanged();

            LogManager.appendMonitorLog("监测仪成功断开连接 " + peripheral.getName());
            saveCacheFile();
        }

        HwAppManager.getBlueManager().refresh();
        HwAppManager.getBlueManager().clearBluePeripheral();
    }

    @Override
    public void onTransportChannelReady(BluePeripheral peripheral) {
        saveCacheFile();
        peripheral.writeDelay(BlueCmd.cRTC(), 200);
        peripheral.writeDelay(BlueCmd.cMonitorBattery(), 400);
        peripheral.writeDelay(BlueCmd.cMonitorAndSleepyState(), 600);
        peripheral.writeDelay(BlueCmd.cSleepyConnectedState(), 800);
        peripheral.writeDelay(BlueCmd.cSleepyBattery(), 1000);
        peripheral.writeDelay(BlueCmd.cMonitorSnNumber(), 1200);
        peripheral.writeDelay(BlueCmd.cSleepySnNumber(), 1400);
        peripheral.writeDelay(BlueCmd.cSleepyMac(), 1600);
        peripheral.writeDelay(BlueCmd.cMonitorFirmwareVersion(), 1800);
        peripheral.writeDelay(BlueCmd.cSleepyFirmwareVersion(), 2000);
        peripheral.writeDelay(BlueCmd.cUserInfo(), 2200);
        peripheral.writeDelay(BlueCmd.cSleepData(), 2400);
        mPackageNumber = 0;
        LogManager.appendMonitorLog("连接成功,开始初始化同步监测仪与助眠仪相关状态数据 " + peripheral.getName());
    }

    private void notifyDeviceDataChanged() {
        mView.onMonitorCallback(mMonitor);
        HwAppManager.getDeviceModel().updateBlueDeviceAndNotifyListeners(mMonitor);
        HwAppManager.getReportModel().notifySyncStatus(mMonitor.status);
    }

    private long getActionTime() {
        return System.currentTimeMillis() / 1000L;
    }

    private void saveCacheFile() {
        File cacheFile = new File(HwApp.getAppContext().getCacheDir(), CACHE_FILE_NAME);
        ObjectOutputStream oos = null;
        try {
            if (cacheFile.exists()) {
                oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
                oos.writeObject(mMonitor);
                oos.flush();
            } else {
                boolean newFile = cacheFile.createNewFile();
                if (newFile) {
                    oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
                    oos.writeObject(mMonitor);
                    oos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(oos);
        }
    }

    @Nullable
    private BluePeripheral getCurrentBluePeripheral() {
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral == null || !bluePeripheral.isConnected()) {
            return null;
        }
        return bluePeripheral;
    }

    private void clearCacheDevice() {
        File cacheFile = new File(HwApp.getAppContext().getCacheDir(), CACHE_FILE_NAME);
        if (cacheFile.exists()) {
            if (cacheFile.delete()) {
                LogManager.appendUserOperationLog("设备被成功解绑,并清除掉缓存成功");
            }
        }
    }
}
