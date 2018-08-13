package com.sumian.sleepdoctor.main

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/13 15:47
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MainActivityTabManager private constructor() {
    private var pendingTabData: String? = null
    private var pendingTabName: String? = null

    companion object {
        const val TAB_HW_DEVICE = "hw_device"
        const val TAB_HW_REPORT = "hw_report"
        const val TAB_HW_ME = "hw_me"
        const val TAB_SD_HOMEPAGE = "sd_homepage"
        const val TAB_SD_DOCTOR = "sd_doctor"
        const val TAB_SD_ME = "sd_me"

        private val INSTANCE by lazy {
            MainActivityTabManager()
        }

        fun putPendingTabData(name: String?, data: String? = null) {
            INSTANCE.pendingTabName = name
            INSTANCE.pendingTabData = data
        }

        fun getPendingTabName(): String? {
            return INSTANCE.pendingTabName
        }

        fun getPendingTabData(): String? {
            return INSTANCE.pendingTabData
        }
    }
}