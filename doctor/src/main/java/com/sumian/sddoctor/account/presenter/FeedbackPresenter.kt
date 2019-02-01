package com.sumian.sddoctor.account.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.bean.Feedback
import com.sumian.sddoctor.account.contract.FeedbackContract
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback

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

        val call = AppManager.getHttpService().feedback(feedback)
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