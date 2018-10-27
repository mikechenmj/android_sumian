package com.sumian.sd.device.scan

import com.sumian.sd.device.util.BluetoothDeviceUtil
import com.sumian.hw.log.LogManager
import com.sumian.sd.BuildConfig

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/13 23:45
 * desc   :
 * version: 1.0
 */
object DeviceValidateUtil {
    fun isDeviceValid(scanRecord: ByteArray?, deviceName: String, deviceMac: String): Boolean {
        val deviceVersion = BluetoothDeviceUtil.getBluetoothDeviceVersion(scanRecord)
        return if (BuildConfig.IS_CLINICAL_VERSION) { // clinical version app
            LogManager.appendBluetoothLog("临床版本app 搜索到一台设备 name=$deviceName  mac=$deviceMac")
            true
        } else { // release version app
            when (deviceVersion) {
                BluetoothDeviceUtil.BLUETOOTH_DEVICE_VERSION_RELEASE -> LogManager.appendBluetoothLog("常规版本app 搜索到一台正式版本设备 name=$deviceName  mac=$deviceMac")
                BluetoothDeviceUtil.BLUETOOTH_DEVICE_VERSION_CLINICAL -> LogManager.appendBluetoothLog("常规版本app 搜索到一台临床版本设备 name=$deviceName  mac=$deviceMac")
                BluetoothDeviceUtil.BLUETOOTH_DEVICE_VERSION_OLD -> LogManager.appendBluetoothLog("常规版本app 搜索到一台老版本设备 name=$deviceName  mac=$deviceMac")
                else -> LogManager.appendBluetoothLog("常规版本app 搜索到一台未知版本设备 name=$deviceName  mac=$deviceMac")
            }
            deviceVersion == BluetoothDeviceUtil.BLUETOOTH_DEVICE_VERSION_RELEASE
        }
    }
}