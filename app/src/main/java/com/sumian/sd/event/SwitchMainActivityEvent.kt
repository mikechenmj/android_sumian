package com.sumian.sd.event

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 17:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class SwitchMainActivityEvent(val type: Int) {
    companion object {
        const val TYPE_HW_ACTIVITY = 0
        const val TYPE_SD_ACTIVITY = 1
    }
}