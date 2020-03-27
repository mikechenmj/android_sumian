package com.sumian.sd.buz.devicemanager.util;

import com.sumian.device.util.ScanRecord;
import com.sumian.sd.buz.devicemanager.command.BlueCmd;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

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
    public static final String BLUETOOTH_DEVICE_VERSION_CLINICAL_FLAG = "0c";
    public static final String BLUETOOTH_DEVICE_VERSION_RELEASE_FLAG = "0e";

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
     * @param scanRecord Advertising data returned in onLeScan()
     * @return device version
     */
    @BluetoothDeviceVersion
    public static int getBluetoothDeviceVersion(byte[] scanRecord) {
        int deviceVersion = BLUETOOTH_DEVICE_VERSION_OLD;
        ScanRecord scanRecordData = ScanRecord.parseFromBytes(scanRecord);
        byte[] manufacturerSpecificData = scanRecordData.getManufacturerSpecificData();
        if (manufacturerSpecificData == null) {
            return deviceVersion;
        }
        String manufacturerSpecificDataStr = BlueCmd.bytes2HexString(manufacturerSpecificData);
        if (manufacturerSpecificDataStr.length() < 6) {
            return deviceVersion;
        }
        String version = manufacturerSpecificDataStr.substring(4,6);
        if (BLUETOOTH_DEVICE_VERSION_CLINICAL_FLAG.equalsIgnoreCase(version)) {
            deviceVersion = BLUETOOTH_DEVICE_VERSION_CLINICAL;
        } else if (BLUETOOTH_DEVICE_VERSION_RELEASE_FLAG.equalsIgnoreCase(version)) {
            deviceVersion = BLUETOOTH_DEVICE_VERSION_RELEASE;
        }
        return deviceVersion;
    }
}
