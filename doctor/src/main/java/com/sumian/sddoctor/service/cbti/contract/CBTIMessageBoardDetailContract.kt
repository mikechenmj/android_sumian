package com.sumian.sddoctor.service.cbti.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.cbti.bean.MessageBoard

interface CBTIMessageBoardDetailContract {

    interface View : BaseShowLoadingView {
        fun onGetMsgBoardDetailSuccess(messageBoard: MessageBoard)
        fun onGetMsgBoardDetailFailed(error: String)
        fun onShowErrorView()
        fun onHideErrorView()
    }


    interface Presenter : IPresenter {
        fun getMsgBoardDetail(msgInt: Int)
    }
}