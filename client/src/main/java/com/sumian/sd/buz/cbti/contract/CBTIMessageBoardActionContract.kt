package com.sumian.sd.buz.cbti.contract

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView

interface CBTIMessageBoardActionContract {

    interface View : SdBaseView<Presenter> {
        fun onPublishMessageBoardSuccess(success: String)
        fun onPublishMessageBoardFailed(error: String)
    }

    interface Presenter : SdBasePresenter<Any> {
        fun publishMessage(message: String, type: Int, isAnonymous: Int)
    }
}