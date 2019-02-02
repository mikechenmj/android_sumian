package com.sumian.sd.buz.cbti.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.cbti.presenter.CBTIMessageBoardActionPresenter

interface CBTIMessageBoardActionContract {

    interface View : SdBaseView<CBTIMessageBoardActionPresenter> {
        fun onPublishMessageBoardSuccess(success: String)
        fun onPublishMessageBoardFailed(error: String)
    }

}