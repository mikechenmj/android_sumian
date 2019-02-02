package com.sumian.sd.buz.cbti.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.cbti.bean.MessageBoard
import com.sumian.sd.buz.cbti.presenter.CBTIMsgBoardPresenter

interface CBTIMessageBoardContract {

    interface View : SdBaseView<CBTIMsgBoardPresenter> {
        fun onGetMessageBoardListSuccess(msgBoardList: List<MessageBoard>)
        fun onRefreshMessageBoardListSuccess(msgBoardList: List<MessageBoard>)
        fun onGetNextMessageBoardListSuccess(msgBoardList: List<MessageBoard>)
        fun onGetMessageBoardListFailed(error: String)
    }

}