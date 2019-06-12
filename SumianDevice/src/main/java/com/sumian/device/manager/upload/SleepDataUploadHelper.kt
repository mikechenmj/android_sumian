package com.sumian.device.manager.upload

import android.content.Context
import android.text.TextUtils
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.sumian.device.manager.upload.UploadFileCallback.Companion.ERROR_CODE_DUPLICATE_UPLOAD
import com.sumian.device.manager.upload.UploadFileCallback.Companion.ERROR_CODE_NETWORK_ERROR
import com.sumian.device.manager.upload.UploadFileCallback.Companion.ERROR_CODE_OSS_ERROR
import com.sumian.device.manager.upload.UploadFileCallback.Companion.ERROR_CODE_UNKNOWN
import com.sumian.device.manager.upload.bean.UploadSleepDataTask
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.LogManager
import com.sumian.sd.common.oss.OssResponse
import com.sumian.sd.common.oss.OssUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 17:01
 * desc   : 上传数据
 * version: 1.0
 */
class SleepFileUploadUtil {

    companion object {
        fun uploadSleepFile(
                context: Context,
                task: UploadSleepDataTask,
                callback: UploadFileCallback
        ) {
            val params = task.uploadSleepDataParams
            val map = HashMap<String, Any>(0)
            map["filename"] = params.fileName
            if (!TextUtils.isEmpty(params.sn)) {
                map["sn"] = params.sn!!
            }
            if (!TextUtils.isEmpty(params.sleeper_sn)) {
                map["sleeper_sn"] = params.sleeper_sn!!
            }
            map["type"] = params.type
            map["app_receive_started_at"] = params.app_receive_ended_at
            map["app_receive_ended_at"] = params.app_receive_ended_at

            LogManager.uploadSleepDatatLog("upload $task")
            NetworkManager.getApi().uploadTransData(map)
                    .enqueue(object : Callback<OssResponse> {
                        override fun onFailure(call: Call<OssResponse>, t: Throwable) {
                            LogManager.uploadSleepDatatLog("uploadTransData onFailure: ${t.message}")
                            callback.onFail(ERROR_CODE_NETWORK_ERROR, t.message)
                        }

                        override fun onResponse(
                                call: Call<OssResponse>,
                                response: Response<OssResponse>
                        ) {
                            LogManager.uploadSleepDatatLog("uploadTransData onResponse: $response")
                            if (response.isSuccessful) {
                                val ossResponse = response.body()
                                if (ossResponse == null) {
                                    callback.onFail(ERROR_CODE_UNKNOWN, "ossResponse null")
                                } else {
                                    uploadFileToOss(context, task.filePath, ossResponse, callback)
                                }
                            } else {
                                val code = response.code()
                                if (code == 403) {
                                    callback.onFail(ERROR_CODE_DUPLICATE_UPLOAD, response.message())
                                } else {
                                    callback.onFail(code, response.message())
                                }
                            }
                        }
                    })
        }

        private fun uploadFileToOss(
                context: Context,
                filePath: String,
                ossResponse: OssResponse,
                callback: UploadFileCallback
        ) {
            OssUtil.uploadFile(context, filePath, ossResponse, object : OssUtil.UploadCallback {
                override fun onStart(task: OSSAsyncTask<PutObjectResult>) {
                    LogManager.uploadSleepDatatLog("OssUtil.uploadFile onStart: $task")
                }

                override fun onSuccess(response: String?) {
                    LogManager.uploadSleepDatatLog("OssUtil.uploadFile onSuccess: $response")
                    callback.onSuccess(response)
                }

                override fun onFailure(errorCode: String?, message: String?) {
                    LogManager.uploadSleepDatatLog("OssUtil.uploadFile onFailure: $message")
                    callback.onFail(ERROR_CODE_OSS_ERROR, message)
                }
            })
        }
    }
}

interface UploadFileCallback {
    companion object {
        const val ERROR_CODE_NETWORK_ERROR = 1
        const val ERROR_CODE_OSS_ERROR = 2
        const val ERROR_CODE_DUPLICATE_UPLOAD = 3
        const val ERROR_CODE_UNKNOWN = 4

    }

    fun onSuccess(result: String?)
    fun onFail(code: Int, msg: String?)
}