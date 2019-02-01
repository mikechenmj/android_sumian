package com.sumian.sddoctor.service.publish.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.publish.bean.Publish
import com.sumian.sddoctor.service.publish.contract.PublishDocContract

/**
 * Created by dq
 *
 * on 2018/8/31
 *
 * desc:
 */
class PublishDocPresenter private constructor(view: PublishDocContract.View) : BaseViewModel() {

    private var mView: PublishDocContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: PublishDocContract.View): PublishDocPresenter {
            return PublishDocPresenter(view)
        }
    }

     fun publishDoc(publishType: Int, publishId: Int, content: String) {

        mView?.showLoading()

        val call = if (publishType == Publish.PUBLISH_ADVISORY_TYPE) {
            val map = mutableMapOf<String, Any>()
            map["content"] = content
            AppManager.getHttpService().replayDocAdvisory(publishId, map = map)
        } else {
            AppManager.getHttpService().publishDocDiaryEvaluation(publishId, content)
        }

         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onPublishFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Any?) {
                mView?.onPublishSuccess("回复成功")
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })

    }
}