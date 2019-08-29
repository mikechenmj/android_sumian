package com.sumian.device.dfu

interface UpgradeCallback {

    companion object {
        const val ERROR_CODE_ENTER_DFU_MODE_FAIL =0
        const val ERROR_CODE_DFU_ABORTED = 1
        const val ERROR_CODE_GET_MONITOR_MAC_FAIL = 2
        const val ERROR_CODE_GET_SLEEP_MASTER_MAC_FAIL = 3
        const val ERROR_CODE_DOWNLOAD_FILE_FAIL = 4
    }

    fun onStart()

    fun onProgressChange(progress: Int)
    fun onSuccess()
    fun onFail(code: Int, msg: String?)
}