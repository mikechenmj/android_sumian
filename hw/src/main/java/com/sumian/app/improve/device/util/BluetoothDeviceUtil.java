package com.sumian.app.improve.device.util;

import android.support.annotation.IntDef;

import com.sumian.app.command.BlueCmd;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/21 9:01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BluetoothDeviceUtil {
    @SuppressWarnings("WeakerAccess")
    public static final int BLUETOOTH_DEVICE_VERSION_OLD = 0;
    public static final int BLUETOOTH_DEVICE_VERSION_CLINICAL = 1;
    public static final int BLUETOOTH_DEVICE_VERSION_RELEASE = 2;

    @IntDef({
        BLUETOOTH_DEVICE_VERSION_OLD,
        BLUETOOTH_DEVICE_VERSION_CLINICAL,
        BLUETOOTH_DEVICE_VERSION_RELEASE,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface BluetoothDeviceVersion {
    }

    /**
     * 根据扫描蓝牙得到的Advertising data（scanResult）判断设备的版本
     * <p>
     * scanRecord example: 02010607ffefbe0c0005040d094d2d53554d49414e2d39433400000000000000000000000000000000000000000000000000000000000000000000000000
     * useful fragment: 0c000504
     * 0C 代表Clinical 的“C”,0E 代表 Release 的 “E”
     * 000504为版本号,对应0.5.4
     * <p>
     * 参考资料（邮件）：
     * 发件人： fujun.z@sumian.com
     * 发送时间： 2018-04-17 16:25
     * 主题： Re: 关于区分临床版本与正式版本固件区分的修改方案
     *
     * @param scanRecord Advertising data returned in onLeScanCallback()
     * @return device version
     */
    @BluetoothDeviceVersion
    public static int getBluetoothDeviceVersion(byte[] scanRecord) {
        int deviceVersion = BLUETOOTH_DEVICE_VERSION_OLD;
        String scanRecordStr = BlueCmd.bytes2HexString(scanRecord);
        if (scanRecordStr.length() >= 16) {
            String substring = scanRecordStr.substring(14, 16);
            if ("0c".equalsIgnoreCase(substring)) {
                deviceVersion = BLUETOOTH_DEVICE_VERSION_CLINICAL;
            } else if ("0e".equalsIgnoreCase(substring)) {
                deviceVersion = BLUETOOTH_DEVICE_VERSION_RELEASE;
            }
        }
        return deviceVersion;
    }
}
