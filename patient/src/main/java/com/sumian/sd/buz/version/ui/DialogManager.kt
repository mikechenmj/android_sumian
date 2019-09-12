package com.sumian.sd.buz.version.ui

import com.sumian.sd.app.AppManager

/**
 * @author : Zhan Xuzhao
 * @changer : chenmj
 * e-mail : 448900450@qq.com
 * time   : 2019/9/02 11:12
 * desc   :修正不同弹窗之间是否允许弹出的逻辑
 * version: 2.0
 */
object DialogManager {
    const val DIALOG_TYPE_APP = 1
    const val DIALOG_TYPE_DEVICE = 2

    var isAppUpgradeDialogShowing = false
    var isAppForceUpgrade = false
    var isDeviceUpgradeDialogShowing = false
    var isDeviceForceUpgrade = false

    var isInScanUpgradeUi = false

    /**
     * 应用不在前台时不弹框
     * 更新弹窗（强制更新APP  >  强制更新固件 > 推荐更新APP = 推荐更新固件）> 使用设备相关业务的弹窗 > 其他业务弹窗（低优先级）
     * 高优先级显示的时候，低优先级发生了，无须继续弹窗，待下次符合条件时再弹；
     * 某优先级显示的时候，同优先级发生了，无须继续弹窗，待下次符合条件时再弹；
     * 低优先级显示的时候，高优先级发生了，需要弹出高优先级那个弹窗，结束后，继续展示低优先级弹窗；
     */
    fun canShow(type: Int, force: Boolean): Boolean {
        return when (type) {
            DIALOG_TYPE_APP -> {
                if (force) {
                    true
                } else {
                    var isDialogShowing = isAppUpgradeDialogShowing || isDeviceUpgradeDialogShowing
                    !isDialogShowing
                }
            }
            DIALOG_TYPE_DEVICE -> {
                if (isInScanUpgradeUi) {
                    return false
                }
                if (!AppManager.isAppForeground()) {
                    return false
                }
                if (force) {
                    var isForceDialogShowing = (isAppUpgradeDialogShowing && isAppForceUpgrade)
                            || (isDeviceUpgradeDialogShowing && isDeviceForceUpgrade)
                    !isForceDialogShowing
                } else {
                    var isDialogShowing = isAppUpgradeDialogShowing || isDeviceUpgradeDialogShowing
                    !isDialogShowing

                }
            }
            else -> true
        }
    }
}