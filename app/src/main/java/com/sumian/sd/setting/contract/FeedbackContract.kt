package com.sumian.sd.setting.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

interface FeedbackContract {

    interface View : BaseShowLoadingView {
        fun onFeedbackSuccess(success: String)
        fun onFeedbackFailed(error: String)
    }

    interface Presenter : IPresenter {
        fun feedback(feedback: String)
    }
}