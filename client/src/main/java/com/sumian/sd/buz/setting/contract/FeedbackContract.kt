package com.sumian.sd.buz.setting.contract

import com.sumian.common.base.BaseShowLoadingView

interface FeedbackContract {

    interface View : BaseShowLoadingView {
        fun onFeedbackSuccess(success: String)
        fun onFeedbackFailed(error: String)
    }

}