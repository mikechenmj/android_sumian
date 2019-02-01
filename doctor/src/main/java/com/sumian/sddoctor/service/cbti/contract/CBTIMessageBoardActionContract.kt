package com.sumian.sddoctor.service.cbti.contract

import com.sumian.sddoctor.service.advisory.onlinereport.SdBasePresenter
import com.sumian.sddoctor.service.advisory.onlinereport.SdBaseView

interface CBTIMessageBoardActionContract {

    interface View : SdBaseView<Presenter> {
        fun onPublishMessageBoardSuccess(success: String)
        fun onPublishMessageBoardFailed(error: String)
    }

    interface Presenter : SdBasePresenter<Any> {
        fun publishMessage(message: String, type: Int, isAnonymous: Int)
    }
}