@file:Suppress("UNUSED_EXPRESSION", "PrivatePropertyName")

package com.sumian.sd.service.advisory.presenter

import android.text.TextUtils
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
import com.sumian.sd.BuildConfig
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.bean.PictureOssSts
import com.sumian.sd.service.advisory.contract.PublishAdvisoryRecordContact
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.body.AdvisoryRecordBody
import com.sumian.sd.network.callback.BaseResponseCallback
import com.sumian.sd.utils.JsonUtil
import org.json.JSONObject
import java.util.*

/**
 *
 *Created by sm
 * on 2018/6/8 11:11
 * desc:
 **/
class PublishAdvisoryRecordPresenter private constructor(view: PublishAdvisoryRecordContact.View) : PublishAdvisoryRecordContact.Presenter {

    private val TAG = PublishAdvisoryRecordPresenter::class.java.simpleName

    private var mView: PublishAdvisoryRecordContact.View? = null

    private var mPublishIndex: Int = 0

    private var mPictureOssSts: PictureOssSts? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {
        fun init(view: PublishAdvisoryRecordContact.View) {
            PublishAdvisoryRecordPresenter(view)
        }
    }

    override fun publishAdvisoryRecord(advisoryId: Int, content: String, onlineReportIds: ArrayList<Int>?) {

        this.mView?.onBegin()

        val advisoryRecordBody = AdvisoryRecordBody()
        advisoryRecordBody.include = "records"
        advisoryRecordBody.advisory_id = advisoryId
        advisoryRecordBody.content = content

        if (onlineReportIds != null) {
            advisoryRecordBody.online_report_ids = onlineReportIds
        }

        val call = AppManager.getHttpService().publishAdvisoryRecord(advisoryRecordBody)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<Advisory>() {
            override fun onSuccess(response: Advisory?) {
                AppManager.getAdvisoryViewModel().notifyAdvisory(advisory = response!!)
                mView?.onPublishAdvisoryRecordSuccess(response)
            }

            override fun onFailure(code: Int, message: String) {
                Log.e(TAG, "上传失败")
                mView?.onPublishAdvisoryRecordFailed(error = message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }

    override fun publishPictureAdvisoryRecord(advisoryId: Int, content: String, onlineReportIds: ArrayList<Int>?, pictureCount: Int) {

        this.mView?.onBegin()

        val advisoryRecordBody = AdvisoryRecordBody()
        advisoryRecordBody.advisory_id = advisoryId
        advisoryRecordBody.content = content
        if (onlineReportIds != null) {
            advisoryRecordBody.online_report_ids = onlineReportIds
        }
        advisoryRecordBody.picture_count = pictureCount

        val call = AppManager.getHttpService().publishPicturesAdvisoryRecord(advisoryRecordBody)
        mCalls.add(call)

        call.enqueue(object : BaseResponseCallback<PictureOssSts>() {

            override fun onSuccess(response: PictureOssSts?) {
                mPictureOssSts = response
                mView?.onGetPublishUploadStsSuccess("准备开始上传图片,请稍后")
            }

            override fun onFailure(code: Int, message: String) {
                mView?.onGetPublishUploadStsFailed(error = message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }

    override fun getLastAdvisory() {

        this.mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = "user,doctor,records"

        val call = AppManager.getHttpService().getLastAdvisoryDetails(map)
        mCalls.add(call)

        call.enqueue(object : BaseResponseCallback<Advisory>() {

            override fun onSuccess(response: Advisory?) {
                AppManager.getAdvisoryViewModel().notifyAdvisory(response!!)
                mView?.onGetLastAdvisorySuccess(response)
            }

            override fun onFailure(code: Int, message: String) {
                mView?.onGetLastAdvisoryFailed(error = message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })

    }

    private lateinit var mLocalFilePaths: Array<String>

    override fun publishImages(localFilePaths: Array<String>, oSSProgressCallback: OSSProgressCallback<PutObjectRequest>) {
        mPublishIndex = 0
        mView?.onStartUploadImagesCallback()
        this.mLocalFilePaths = localFilePaths
        publishImage(sts = mPictureOssSts!!, imageIndex = mPublishIndex, localFilePath = localFilePaths[mPublishIndex], oSSProgressCallback = oSSProgressCallback)
    }


    private fun publishImage(sts: PictureOssSts, imageIndex: Int, localFilePath: String, oSSProgressCallback: OSSProgressCallback<PutObjectRequest>) {

        if (BuildConfig.DEBUG) {
            OSSLog.enableLog()
        }

        val credentialProvider = OSSStsTokenCredentialProvider(sts.access_key_id, sts.access_key_secret, sts.security_token)
        val ossClient = OSSClient(App.getAppContext(), sts.endpoint, credentialProvider)
        // 构造上传请求

        val putObjectRequest = PutObjectRequest(sts.bucket, sts.objects[imageIndex], localFilePath)

        val metadata = ObjectMetadata()
        metadata.addUserMetadata("Accept-Encoding", "")
        // metadata.contentType = "application/octet-stream"

        putObjectRequest.metadata = metadata

        // 异步上传时可以设置进度回调
        val callbackParam = HashMap<String, String>(0)

        if (mPublishIndex == sts.objects.size - 1) {
            callbackParam["callbackUrl"] = sts.callback_url
            //callbackParam.put("callbackHost", "oss-cn-hangzhou.aliyuncs.com");
            //callbackParam.put("callbackBodyType", "application/json");//如果加入该请求参数,会出现请求500的错误.直接
            callbackParam["callbackBody"] = sts.callback_body
        }

        putObjectRequest.callbackParam = callbackParam

        putObjectRequest.progressCallback = oSSProgressCallback

        ossClient.asyncPutObject(putObjectRequest, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                mPublishIndex++
                if (mPublishIndex < sts.objects.size) {
                    publishImage(sts, mPublishIndex, mLocalFilePaths[mPublishIndex], oSSProgressCallback)
                } else {

                    val returnBody = result?.serverCallbackReturnBody
                    if (!TextUtils.isEmpty(returnBody)) {
                        if (returnBody?.contains("error")!!) {
                            val jsonObject = JSONObject(returnBody)
                            val errorJson = jsonObject.getString("error")
                            if (!TextUtils.isEmpty(errorJson)) {
                                val error = JSONObject(errorJson)
                                mView?.onEndUploadImagesCallback()
                                val errorMsg = error.getString("user_message")
                                if (!TextUtils.isEmpty(errorMsg)) {
                                    mView?.onPublishAdvisoryRecordFailed(errorMsg)
                                    return
                                }
                            }
                        }
                    }

                    mView?.onEndUploadImagesCallback()
                    val serverCallbackReturnBody = result?.serverCallbackReturnBody
                    val advisory = JsonUtil.fromJson(serverCallbackReturnBody, Advisory::class.java)
                    mView?.onPublishAdvisoryRecordSuccess(advisory!!)
                }
            }

            override fun onFailure(request: PutObjectRequest?, clientException: ClientException?, serviceException: ServiceException?) {
                Log.e(TAG, "上传失败")
                mView?.onEndUploadImagesCallback()
                mView?.onPublishAdvisoryRecordFailed("图片上传失败,请重试")
            }

        })
    }

}