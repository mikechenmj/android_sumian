package com.sumian.device.manager.upload.bean

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 16:37
 * desc   :
 * version: 1.0
 */
data class UploadSleepDataTask(
        val filePath: String,
        val uploadSleepDataParams: UploadSleepDataParams
) {

    override fun equals(other: Any?): Boolean {
        return if (other != null && other is UploadSleepDataTask) {
            other.filePath == filePath
        } else {
            false
        }
    }
}