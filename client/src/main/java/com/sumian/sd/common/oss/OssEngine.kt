package com.sumian.sd.common.oss

import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.utils.SumianExecutor
import com.sumian.sd.BuildConfig
import com.sumian.sd.app.App
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
class OssEngine {
    init {
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog()
        }
    }

    companion object {

        fun uploadFile(ossResponse: OssResponse, localUploadFilePath: String, uploadCallback: UploadCallback): OSSAsyncTask<PutObjectResult> {
            return uploadFile(ossResponse, localUploadFilePath, uploadCallback, null)
        }

        fun uploadFile(ossResponse: OssResponse, localUploadFilePath: String, uploadCallback: UploadCallback, progressListener: UploadProgressListener?): OSSAsyncTask<PutObjectResult> {
            val credentialProvider = OSSStsTokenCredentialProvider(ossResponse.accessKeyId, ossResponse.accessKeySecret, ossResponse.securityToken)
            val ossClient = OSSClient(App.getAppContext(), ossResponse.endpoint, credentialProvider)
            val putObjectRequest = PutObjectRequest(ossResponse.bucket, ossResponse.objectX, localUploadFilePath)
            val callbackParam = HashMap<String, String>(2)
            callbackParam["callbackUrl"] = ossResponse.callbackUrl
            callbackParam["callbackBody"] = ossResponse.callbackBody
            putObjectRequest.callbackParam = callbackParam
            putObjectRequest.setProgressCallback { _, currentSize, totalSize ->
                LogUtils.d(currentSize, totalSize, (100 * currentSize / totalSize).toString() + "%")
                if (progressListener != null) {
                    SumianExecutor.runOnUiThread({ progressListener.onProgressChange(currentSize, totalSize) })
                }
            }
            return ossClient.asyncPutObject(putObjectRequest, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest, result: PutObjectResult?) {
                    LogUtils.d(result)
                    SumianExecutor.runOnUiThread({ uploadCallback.onSuccess(result?.serverCallbackReturnBody) })
                }

                override fun onFailure(request: PutObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                    LogUtils.d(clientException, serviceException)
                    val errorCode = (serviceException?.errorCode)
                    val message = (clientException?.message ?: serviceException?.message)
                    SumianExecutor.runOnUiThread({ uploadCallback.onFailure(errorCode, message) })
                }
            })
        }
    }

    interface UploadCallback {
        fun onSuccess(response: String?)
        fun onFailure(errorCode: String?, message: String?)
    }

    interface UploadProgressListener {
        fun onProgressChange(currentSize: Long, totalSize: Long)
    }
}