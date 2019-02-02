package com.sumian.sddoctor.account.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.bean.Feedback
import com.sumian.sddoctor.account.contract.FeedbackContract
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback

class FeedbackPresenter private constructor(view: FeedbackContract.View) : BaseViewModel() {

    companion object {

        @JvmStatic
        fun init(view: FeedbackContract.View): FeedbackPresenter {
            return FeedbackPresenter(view)
        }
    }

    private var mView: FeedbackContract.View? = null

    init {
        this.mView = view
    }

    fun feedback(feedback: String) {
        mView?.showLoading()

        val call = AppManager.getHttpService().feedback(feedback)
        addCall(call)

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