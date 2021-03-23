package com.sumian.devicedemo.dfuDemo.updater

import android.util.Log
import com.sumian.devicedemo.dfuDemo.*
import com.sumian.devicedemo.dfuDemo.updater.bean.CheckSumResult
import com.sumian.devicedemo.dfuDemo.updater.bean.SelectCmdResult
import kotlin.math.ceil

class InitPackageUpdater(dfuPackage: ByteArray, deviceId: String) : DfuPackageUpdater(dfuPackage, deviceId) {

    override fun getType(): Int {
        return PARA_INIT
    }

    override fun getPRNNumber(): Int {
        return INIT_PRN
    }

}