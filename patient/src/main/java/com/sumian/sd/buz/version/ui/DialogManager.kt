package com.sumian.sd.buz.version.ui

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/6/12 10:55
 * desc   :
 * version: 1.0
 */
object DialogManager {
    const val DIALOG_TYPE_APP = 1
    const val DIALOG_TYPE_DEVICE = 2

    var isAppUpgradeDialogShowing = false
    var isAppForceUpgrade = false
    var isDeviceUpgradeDialogShowing = false
    var isDeviceForceUpgrade = false

    /**
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
                    !isDeviceUpgradeDialogShowing
                }
            }
            DIALOG_TYPE_DEVICE -> {
                if (force) {
                    !(isAppUpgradeDialogShowing && isAppForceUpgrade)
                            || !(isDeviceUpgradeDialogShowing && isDeviceForceUpgrade)
                } else {
                    !isAppUpgradeDialogShowing
                }
            }
            else -> true
        }
    }
}