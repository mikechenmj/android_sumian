@file:Suppress("UNUSED_EXPRESSION", "PrivatePropertyName")

package com.sumian.sddoctor.service.publish.presenter

import android.util.Log
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.model.ObjectMetadata
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.error.ErrorInfo499
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.oss.OssResponse
import com.sumian.sddoctor.service.publish.bean.Publish
import com.sumian.sddoctor.service.publish.contract.PublishVoiceContact
import java.util.*

/**
 *
 *Created by sm
 *
 * on 2018/6/8 11:11
 *
 * desc:医生图文咨询/周日记评估  语音回复
 *
 **/
class PublishVoicePresenter private constructor(view: PublishVoiceContact.View) : PublishVoiceContact.Presenter {

    companion object {

        private val TAG = PublishVoicePresenter::class.java.simpleName

        fun init(view: PublishVoiceContact.View): PublishVoiceContact.Presenter {
            return PublishVoicePresenter(view)
        }
    }

    private var mView: PublishVoiceContact.View? = null

    init {
        this.mView = view
    }


    override fun getPublishVoiceSts(publishType: Int, publishId: Int, voiceFilePath: String, duration: Int) {

        mView?.showLoading()

        val call = if (publishType == Publish.PUBLISH_ADVISORY_TYPE) {
            AppManager.getHttpService().getAdvisoryVoiceOssSts(publishId, duration)
        } else {
            AppManager.getHttpService().getDiaryEvaluationVoiceSts(publishId, duration)
        }

        mCalls.add(call)

        call.enqueue(object : BaseSdResponseCallback<OssResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetPublishVoiceStsFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: OssResponse?) {
                response?.let {
                    publishVoice(response, voiceFilePath)
                }
                mView?.onGetPublishVoiceStsSuccess()
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })
    }

    private fun publishVoice(sts: OssResponse, localFilePath: String) {

        mView?.showLoading()

        if (BuildConfig.DEBUG) {
            OSSLog.enableLog()
        }

        val credentialProvider = OSSStsTokenCredentialProvider(sts.accessKeyId, sts.accessKeySecret, sts.securityToken)
        val ossClient = OSSClient(App.getAppContext(), sts.endpoint, credentialProvider)
        // 构造上传请求

        val putObjectRequest = PutObjectRequest(sts.bucket, sts.objectX, localFilePath)

        val metadata = ObjectMetadata()
        metadata.addUserMetadata("Accept-Encoding", "")
        putObjectRequest.metadata = metadata
        metadata.contentType = "application/octet-stream"

        // 异步上传时可以设置进度回调
        val callbackParam = HashMap<String, String>(0)
        callbackParam["callbackUrl"] = sts.callbackUrl

        //callbackParam.put("callbackHost", "oss-cn-hangzhou.aliyuncs.com");
        //callbackParam.put("callbackBodyType", "application/json");//如果加入该请求参数,会出现请求500的错误.直接
        callbackParam["callbackBody"] = sts.callbackBody
        putObjectRequest.callbackParam = callbackParam

        putObjectRequest.progressCallback = OSSProgressCallback<PutObjectRequest> { _, currentSize, totalSize -> Log.e(TAG, "currentSize=$currentSize   totalSize=$totalSize") }

        ossClient.asyncPutObject(putObjectRequest, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {

                result?.let {
                    val returnBody = it.serverCallbackReturnBody
                    if (it.statusCode == 499 || it.statusCode == 299) {
                        val errorInfo499 = JsonUtil.fromJson(returnBody, ErrorInfo499::class.java)!!
                        mView?.onPublishVoiceFailed(error = errorInfo499.error.userMessage)
                    } else {
                        //val advisory = JsonUtil.fromJson(returnBody, Advisory::class.java)!!
                        mView?.onPublishVoiceSuccess()
                    }
                }

                mView?.dismissLoading()
            }

            override fun onFailure(request: PutObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                mView?.onPublishVoiceFailed("语音上传失败,请重试")
                mView?.dismissLoading()
            }

        })
    }
}