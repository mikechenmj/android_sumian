package com.sumian.devicedemo.dfuDemo.updater

import com.sumian.devicedemo.dfuDemo.IMAGE_PRN
import com.sumian.devicedemo.dfuDemo.PARA_IMAGE
import com.sumian.devicedemo.dfuDemo.updater.bean.CheckSumResult

class DataPackageUpdater(dfuPackage: ByteArray, deviceId: String) : DfuPackageUpdater(dfuPackage, deviceId) {
    override fun getType(): Int {
        return PARA_IMAGE
    }

    override fun getPRNNumber(): Int {
        return IMAGE_PRN
    }
}