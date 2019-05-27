package com.sumian.sd.common.oss

import android.content.Context
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.sumian.device.BuildConfig
import com.sumian.device.util.LogManager
import com.sumian.device.util.ThreadManager
import java.util.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/19 14:38
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class OssUtil {
    init {
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog()
        }
    }

    companion object {
        fun uploadFile(
                context: Context,
                localUploadFilePath: String,
                ossResponse: OssResponse,
                uploadCallback: UploadCallback?
        ) {
            ThreadManager.runOnWorkThread {
                val credentialProvider = OSSStsTokenCredentialProvider(
                        ossResponse.accessKeyId,
                        ossResponse.accessKeySecret,
                        ossResponse.securityToken
                )
                val ossClient = OSSClient(context, ossResponse.endpoint, credentialProvider)
                val putObjectRequest =
                        PutObjectRequest(ossResponse.bucket, ossResponse.objectX, localUploadFilePath)
                val callbackParam = HashMap<String, String>(2)
                callbackParam["callbackUrl"] = ossResponse.callbackUrl
                callbackParam["callbackBody"] = ossResponse.callbackBody
                putObjectRequest.callbackParam = callbackParam
                putObjectRequest.setProgressCallback { _, currentSize, totalSize ->
                    LogManager.log(
                            "$currentSize, $totalSize, ${(100 * currentSize / totalSize).toString()} + %"
                    )
                    ThreadManager.runOnUIThread {
                        uploadCallback?.onProgressChange(currentSize, totalSize)
                    }
                }

                val task = ossClient.asyncPutObject(putObjectRequest, object :
                        OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                    override fun onSuccess(request: PutObjectRequest, result: PutObjectResult?) {
                        LogManager.log(result.toString())
                        ThreadManager.runOnUIThread { uploadCallback?.onSuccess(result?.serverCallbackReturnBody) }
                    }

                    override fun onFailure(
                            request: PutObjectRequest?,
                            clientException: ClientException?,
                            serviceException: ServiceException?
                    ) {
                        LogManager.log("$clientException, $serviceException")
                        val errorCode = (serviceException?.errorCode)
                        val message = (clientException?.message ?: serviceException?.message)
                        ThreadManager.runOnUIThread {
                            uploadCallback?.onFailure(errorCode, message)
                        }
                    }
                })
                ThreadManager.runOnUIThread { uploadCallback?.onStart(task) }
            }
        }
    }

    interface UploadCallback {
        fun onStart(task: OSSAsyncTask<PutObjectResult>)
        fun onSuccess(response: String?)
        fun onFailure(errorCode: String?, message: String?)
        fun onProgressChange(currentSize: Long, totalSize: Long) {}
    }

}