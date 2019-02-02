package com.sumian.sd.buz.cbti.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.cbti.bean.MessageBoard
import com.sumian.sd.buz.cbti.contract.CBTIMessageBoardContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

class CBTIMsgBoardPresenter private constructor(view: CBTIMessageBoardContract.View) : BaseViewModel() {

    companion object {
        private const val DEFAULT_PAGES: Int = 15
        @JvmStatic
        fun init(view: CBTIMessageBoardContract.View): CBTIMsgBoardPresenter = CBTIMsgBoardPresenter(view)
    }

    private var mType: Int = 0
    private var mView: CBTIMessageBoardContract.View? = null

    private var mPageNumber: Int = 1
    private var mIsRefresh: Boolean = false
    private var mIsGetNext = false
    private var mIsCanLoadMore = false

    init {
        view.setPresenter(this)
        mView = view
    }

    fun setType(type: Int) {
        this.mType = type
        refreshMessageBoardList()
    }

    fun getMessageBoardList(type: Int) {
        this.mType = type
        mView?.onBegin()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["include"] = "commenter"
        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES

        val call = AppManager.getSdHttpService().getCBTIMessageBoardList(map)
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
                        mView?.onGetMessageBoardListSuccess(data!!)
                    }
                }
                mIsCanLoadMore = !response?.meta?.pagination?.isLastPage()!!
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetMessageBoardListFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                mView?.onFinish()
            }
        })
    }

    fun refreshMessageBoardList() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getMessageBoardList(mType)
    }

    fun getNextMessageBoardList() {
        if (!mIsCanLoadMore) {
            return
        }
        mPageNumber++
        mIsGetNext = true
        getMessageBoardList(mType)
    }
}