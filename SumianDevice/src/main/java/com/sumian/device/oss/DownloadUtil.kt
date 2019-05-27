package com.sumian.device.oss

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/15 17:34
 * desc   :
 * version: 1.0
 */
class DownloadUtil {

    interface DownloadFileCallback {
        fun onStart()
        fun onFinish()
        fun onProgressChange(progress: Int, total: Int)
        fun onFail(code: Int, msg: String)
    }
}