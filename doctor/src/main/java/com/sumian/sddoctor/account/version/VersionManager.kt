package com.sumian.sddoctor.account.version

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.VersionUtil
import com.sumian.sddoctor.account.bean.Version
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.UiUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/2/28 14:00
 * desc   :
 * version: 1.0
 */
object VersionManager {
    const val UPGRADE_MODE_NO_UPGRADE = -1
    const val UPGRADE_MODE_NORMAL = 0
    const val UPGRADE_MODE_FORCE = 1
    const val UPGRADE_MODE_MUTE = 2

    val mUpgradeMode by lazy {
        val liveData = MutableLiveData<Int>()
        liveData.value = UPGRADE_MODE_NO_UPGRADE
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
        val call = AppManager.getHttpService().getAppVersion(currentVersion = currentVersion)
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
                        if (isHaveUpgrade) {
                            mUpgradeMode.value = response.show_update_mode
                        } else {
                            mUpgradeMode.value = UPGRADE_MODE_NO_UPGRADE
                        }
                    }
                }
            }
        })
    }
}