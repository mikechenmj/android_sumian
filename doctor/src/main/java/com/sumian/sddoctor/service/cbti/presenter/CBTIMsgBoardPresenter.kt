package com.sumian.sddoctor.service.cbti.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.bean.PaginationResponseV2
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.cbti.bean.MessageBoard
import com.sumian.sddoctor.service.cbti.contract.CBTIMessageBoardContract

class CBTIMsgBoardPresenter private constructor(view: CBTIMessageBoardContract.View) : CBTIMessageBoardContract.Presenter {

    companion object {
        private const val DEFAULT_PAGES: Int = 15
        @JvmStatic
        fun init(view: CBTIMessageBoardContract.View): CBTIMessageBoardContract.Presenter = CBTIMsgBoardPresenter(view)
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

    override fun setType(type: Int) {
        this.mType = type
        refreshMessageBoardList()
    }

    override fun getMessageBoardList(type: Int) {
        this.mType = type
        mView?.onBegin()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["include"] = "commenter"
        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES

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

    override fun refreshMessageBoardList() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getMessageBoardList(mType)
    }

    override fun getNextMessageBoardList() {
        if (!mIsCanLoadMore) {
            return
        }
        mPageNumber++
        mIsGetNext = true
        getMessageBoardList(mType)
    }
}