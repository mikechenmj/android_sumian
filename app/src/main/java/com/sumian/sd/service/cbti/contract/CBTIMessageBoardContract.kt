package com.sumian.sd.service.cbti.contract

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.service.cbti.bean.MessageBoard

interface CBTIMessageBoardContract {

    interface View : SdBaseView<Presenter> {
        fun onGetMessageBoardListSuccess(msgBoardList: List<MessageBoard>)
        fun onRefreshMessageBoardListSuccess(msgBoardList: List<MessageBoard>)
        fun onGetNextMessageBoardListSuccess(msgBoardList: List<MessageBoard>)
        fun onGetMessageBoardListFailed(error: String)
    }

    interface Presenter : SdBasePresenter<Any> {
        fun setType(type: Int)
        fun getMessageBoardList(type: Int)
        fun refreshMessageBoardList()
        fun getNextMessageBoardList()
    }
}