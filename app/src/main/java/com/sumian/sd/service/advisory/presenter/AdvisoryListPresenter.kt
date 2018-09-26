package com.sumian.sd.service.advisory.presenter

import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.callback.BaseResponseCallback
import com.sumian.sd.network.response.PaginationResponse
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.contract.AdvisoryListContract
import retrofit2.Callback

/**
 *
 *Created by sm
 * on 2018/6/4 16:01
 * desc:
 **/
class AdvisoryListPresenter private constructor(view: AdvisoryListContract.View) : AdvisoryListContract.Presenter {

    private var mView: AdvisoryListContract.View? = null

    private var mPageNumber: Int = 1
    private var mAdvisoryType = Advisory.UNFINISHED_TYPE
    private var mIsRefresh: Boolean = false
    private var mIsGetNext = false

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        const val DEFAULT_PAGES: Int = 15

        fun init(view: AdvisoryListContract.View) {
            AdvisoryListPresenter(view)
        }
    }

    override fun refreshAdvisories() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getAdvisories(mAdvisoryType)
    }

    override fun getAdvisories(advisoryType: Int) {
        this.mAdvisoryType = advisoryType

        mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = if (advisoryType == Advisory.FINISHED_TYPE) "records" else ""
        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES
        map["type"] = advisoryType

        val call = AppManager.getHttpService().getDoctorAdvisories(map)
        mCalls?.add(call)
        call.enqueue(object : BaseResponseCallback<PaginationResponse<Advisory>>(), Callback<PaginationResponse<Advisory>> {
            override fun onSuccess(response: PaginationResponse<Advisory>?) {
                val data = response?.data
                if (mIsRefresh) {
                    mIsRefresh = false
                    mPageNumber = 1
                    mView?.onRefreshAdvisoriesSuccess(data!!)
                } else {
                    mIsRefresh = false
                    if (mIsGetNext) {
                        mIsGetNext = false
                        mView?.onGetNextAdvisoriesSuccess(data!!)
                    } else {
                        mView?.onGetAdvisoriesSuccess(data!!)
                    }
                }
                if (data != null && !data.isEmpty()) {
                    mPageNumber++
                }
            }

            override fun onFailure(code: Int, message: String) {
                mIsRefresh = false
                mView?.onGetAdvisoriesFailed(message)
            }

            override fun onFinish() {
                mView?.onFinish()
            }

        })
    }

    override fun getNextAdvisories() {
        mIsGetNext = true
        getAdvisories(mAdvisoryType)
    }

}
