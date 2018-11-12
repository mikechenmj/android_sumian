package com.sumian.sd.setting.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.setting.contract.FeedbackContract
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.setting.bean.Feedback
import com.sumian.sd.setting.contract.FeedbackContract

class FeedbackPresenter private constructor(view: FeedbackContract.View) : FeedbackContract.Presenter {

    companion object {

        @JvmStatic
        fun init(view: FeedbackContract.View): FeedbackContract.Presenter {
            return FeedbackPresenter(view)
        }
    }

    private var mView: FeedbackContract.View? = null

    init {
        this.mView = view
    }

    override fun feedback(feedback: String) {
        mView?.showLoading()

        val call = AppManager.getSdHttpService().feedback(feedback)
        mCalls.add(call)

        call.enqueue(object : BaseSdResponseCallback<Feedback>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onFeedbackFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Feedback?) {
                mView?.onFeedbackSuccess(App.getAppContext().getString(R.string.feedback_success))
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })

    }
}