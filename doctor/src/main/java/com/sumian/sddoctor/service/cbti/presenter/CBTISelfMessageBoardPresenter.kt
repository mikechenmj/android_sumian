package com.sumian.sddoctor.service.cbti.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.bean.PaginationResponseV2
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.cbti.bean.MessageBoard
import com.sumian.sddoctor.service.cbti.contract.CBTISelfMessageBoardContract

/**
 * Created by sm
 *
 * on 2018/12/7
 *
 * desc:管理自己的留言  留言，删除，获取留言
 *
 */
class CBTISelfMessageBoardPresenter private constructor(view: CBTISelfMessageBoardContract.View) : BaseViewModel() {

    companion object {
        private const val DEFAULT_PAGES: Int = 15

        @JvmStatic
        fun init(view: CBTISelfMessageBoardContract.View): CBTISelfMessageBoardPresenter = CBTISelfMessageBoardPresenter(view)
    }

    private var mView: CBTISelfMessageBoardContract.View? = null

    init {
        mView = view
    }

    private var mType: Int = 0

    private var mPageNumber: Int = 1
    private var mIsRefresh: Boolean = false
    private var mIsGetNext = false

     fun publishMessage(message: String, type: Int, isAnonymous: Int) {
        mView?.showLoading()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["message"] = message
        map["anonymous"] = isAnonymous
        val call = AppManager.getHttpService().writeCBTIMessageBoard(map = map)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                mView?.onPublishMessageBoardSuccess(App.getAppContext().getString(R.string.msg_board_send_success))
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onPublishMessageBoardFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })
    }

     fun delSelfMsg(msgId: Int, position: Int) {
        mView?.showLoading()
        val call = AppManager.getHttpService().delSelfMessageKeyboard(msgId)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                mView?.onDelSuccess(App.getAppContext().getString(R.string.msg_board_del_success), position)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onDelFailed(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })
    }

     fun refreshSelfMsgListMsg() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getSelfMsgListMsg(mType)
    }

     fun getSelfMsgListMsg(type: Int) {
        this.mType = type
        mView?.showLoading()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["include"] = "commenter"
        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES
        map["commented_by"] = "me"

        val call = AppManager.getHttpService().getCBTIMessageBoardList(map)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<MessageBoard>>() {
            override fun onSuccess(response: PaginationResponseV2<MessageBoard>?) {
                val data = response?.data
                if (mIsRefresh) {
                    mIsRefresh = false
                    mPageNumber = 1
                    mView?.onRefreshMessageBoardListSuccess(data!!)
                } else {
                    mIsRefresh = false
                    if (mIsGetNext) {
                        mIsGetNext = false
                        mView?.onGetNextMessageBoardListSuccess(data!!)
                    } else {
                        mView?.onGetSelfMsgListSuccess(data!!)
                    }
                }
                if (data != null && !data.isEmpty()) {
                    mPageNumber++
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetSelfMsgListFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }
        })
    }

     fun getNextSelfMsgListMsg() {
        mIsGetNext = true
        getSelfMsgListMsg(mType)
    }
}