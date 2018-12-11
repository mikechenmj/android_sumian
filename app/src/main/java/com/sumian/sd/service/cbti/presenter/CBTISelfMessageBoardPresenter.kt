package com.sumian.sd.service.cbti.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.bean.MessageBoard
import com.sumian.sd.service.cbti.contract.CBTISelfMessageBoardContract

/**
 * Created by sm
 *
 * on 2018/12/7
 *
 * desc:管理自己的留言  留言，删除，获取留言
 *
 */
class CBTISelfMessageBoardPresenter private constructor(view: CBTISelfMessageBoardContract.View) : CBTISelfMessageBoardContract.Presenter {

    companion object {
        private const val DEFAULT_PAGES: Int = 15

        @JvmStatic
        fun init(view: CBTISelfMessageBoardContract.View): CBTISelfMessageBoardContract.Presenter = CBTISelfMessageBoardPresenter(view)
    }

    private var mView: CBTISelfMessageBoardContract.View? = null

    init {
        mView = view
    }

    private var mType: Int = 0

    private var mPageNumber: Int = 1
    private var mIsRefresh: Boolean = false
    private var mIsGetNext = false

    override fun publishMessage(message: String, type: Int, isAnonymous: Int) {
        mView?.showLoading()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["message"] = message
        map["anonymous"] = isAnonymous
        val call = AppManager.getSdHttpService().writeCBTIMessageBoard(map = map)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                mView?.onPublishMessageBoardSuccess("留言成功")
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

    override fun delSelfMsg(msgId: Int, position: Int) {
        mView?.showLoading()
        val call = AppManager.getSdHttpService().delSelfMessageKeyboard(msgId)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                mView?.onDelSuccess("删除成功", position)
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

    override fun refreshSelfMsgListMsg() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getSelfMsgListMsg(mType)
    }

    override fun getSelfMsgListMsg(type: Int) {
        this.mType = type
        mView?.showLoading()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["include"] = "commenter"
        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES
        map["commented_by"] = "me"

        val call = AppManager.getSdHttpService().getCBTIMessageBoardList(map)
        mCalls.add(call)
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

    override fun getNextSelfMsgListMsg() {
        mIsGetNext = true
        getSelfMsgListMsg(mType)
    }
}