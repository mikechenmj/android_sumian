package com.sumian.sd.buz.setting.version

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.VersionUtil
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.setting.version.bean.Version
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.UiUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/2/28 14:00
 * desc   :
 * version: 1.0
 */
object VersionManager {
    val mHasUpgradeLiveData by lazy {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = false
        liveData
    }

    init {
        queryVersion()
    }

    fun queryVersion() {
        var currentVersion = UiUtils.getPackageInfo(App.getAppContext()).versionName
        if (currentVersion.indexOf("-") != -1) {
            currentVersion = currentVersion.subSequence(0, currentVersion.indexOf("-")).toString()
        }
        val call = AppManager.getSdHttpService().getAppVersion(currentVersion = currentVersion)
        call.enqueue(object : BaseSdResponseCallback<Version>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse.message)
            }

            override fun onSuccess(response: Version?) {
                response?.let { it ->
                    it.version?.let {
                        val currentVersionCodes = currentVersion.split(".")
                        val onlineVersionCodes = it.split(".")
                        val isHaveUpgrade = VersionUtil.hasNewVersion(onlineVersionCodes, currentVersionCodes)
                        mHasUpgradeLiveData.value = isHaveUpgrade
                    }
                }
            }
        })
    }
}