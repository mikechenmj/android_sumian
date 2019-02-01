package com.sumian.sddoctor.service.advisory.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.advisory.bean.Advisory
import com.sumian.sddoctor.service.advisory.bean.AdvisoryResponse
import com.sumian.sddoctor.service.advisory.contract.AdvisoryListContract

/**
 *
 *Created by sm
 * on 2018/6/4 16:01
 * desc:
 **/
class AdvisoryListPresenter private constructor(view: AdvisoryListContract.View) : AdvisoryListContract.Presenter {

    companion object {

        private const val DEFAULT_PAGES: Int = 10

        fun init(view: AdvisoryListContract.View): AdvisoryListContract.Presenter {
            return AdvisoryListPresenter(view)
        }
    }

    private var mView: AdvisoryListContract.View? = null

    private var mAdvisoryType = Advisory.ALL_TYPE

    private var mCurrentPageIndex = 1
    private var mIsHaveMore = false
    private var mIsRefresh = false

    init {
        this.mView = view
    }

    override fun refreshAdvisories() {
        mCurrentPageIndex = 1
        mIsHaveMore = false
        mIsRefresh = true
        getAdvisories(mAdvisoryType)
    }

    override fun getAdvisories(advisoryType: Int) {
        this.mAdvisoryType = advisoryType

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["page"] = mCurrentPageIndex
        map["per_page"] = DEFAULT_PAGES

        if (advisoryType != Advisory.ALL_TYPE) {
            map["status"] = advisoryType
        }

        map["include"] = "traceable.user"

        val call = AppManager.getHttpService().getDoctorAdvisories(map)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<AdvisoryResponse>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetAdvisoriesFailed(errorResponse.message)
            }

            override fun onSuccess(response: AdvisoryResponse?) {

                if (mIsRefresh) {
                    mIsRefresh = false
                    mView?.onRefreshAdvisoriesSuccess(response?.data!!)
                } else {
                    mIsRefresh = false
                    mView?.onGetAdvisoriesSuccess(response?.data!!)
                }

                mIsHaveMore = response!!.meta.pagination.totalPages > mCurrentPageIndex
                mView?.onHaveMore(isHaveMore = mIsHaveMore)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }

        })
    }

    override fun getNextAdvisories() {
        if (!mIsHaveMore) return
        mCurrentPageIndex++
        getAdvisories(mAdvisoryType)
    }

}
