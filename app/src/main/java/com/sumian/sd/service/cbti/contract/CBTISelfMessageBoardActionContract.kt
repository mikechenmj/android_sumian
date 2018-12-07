package com.sumian.sd.service.cbti.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.service.cbti.bean.MessageBoard

/**
 * Created by sm
 *
 * on 2018/12/7
 *
 * desc:
 *
 */
interface CBTISelfMessageBoardActionContract {

    interface View : BaseShowLoadingView {
        fun onPublishMessageBoardSuccess(success: String)
        fun onPublishMessageBoardFailed(error: String)
        fun onDelSuccess(success: String)
        fun onDelFailed(error: String)
        fun onGetSelfMsgListSuccess(selfMsgList: MutableList<MessageBoard>)
        fun onGetSelfMsgListFailed(error: String)
    }

    interface Presenter : IPresenter {
        fun publishMessage(message: String, type: Int, isAnonymous: Int)
        fun delSelfMsg()
        fun getSelfMsgListMsg(type: Int)
        fun refreshSelfMsgListMsg()
        fun getNextSelfMsgListMsg()
    }

}