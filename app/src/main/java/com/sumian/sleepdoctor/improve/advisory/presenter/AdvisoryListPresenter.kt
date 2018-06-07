package com.sumian.sleepdoctor.improve.advisory.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.AdvisoryListContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse
import com.sumian.sleepdoctor.network.response.PaginationResponse
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
    private var mAdvisoryType = Advisory.UNUSED_TYPE
    private var mAdvisoryId = 0
    private var mIsRefresh: Boolean = false

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
        getAdvisories(mAdvisoryType, mAdvisoryId)
    }

    override fun getAdvisories(advisoryType: Int, advisoryId: Int) {
        this.mAdvisoryType = advisoryType
        this.mAdvisoryId = advisoryId

        mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = if (advisoryType == Advisory.USED_TYPE) "user,doctor,records" else ""
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
                    mView?.onGetAdvisoriesSuccess(data!!)
                }
                if (data != null && !data.isEmpty()) {
                    mPageNumber++
                }
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
                mIsRefresh = false
                mView?.onGetAdvisoriesFailed(errorResponse?.message!!)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }

    override fun getNextAdvisories() {
        getAdvisories(mAdvisoryType, mAdvisoryId)
    }

}
