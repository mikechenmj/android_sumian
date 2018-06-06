package com.sumian.sleepdoctor.improve.advisory.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.AdvisoryContract
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
class AdvisoryPresenter private constructor(view: AdvisoryContract.View) : AdvisoryContract.Presenter {

    private var mView: AdvisoryContract.View? = null

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

        fun init(view: AdvisoryContract.View) {
            AdvisoryPresenter(view)
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
        call.enqueue(object : BaseResponseCallback<PaginationResponse<ArrayList<Advisory>>>(), Callback<PaginationResponse<ArrayList<Advisory>>> {
            override fun onSuccess(response: PaginationResponse<ArrayList<Advisory>>?) {
                val data = response?.data
                if (mIsRefresh) {
                    mPageNumber = 1
                    mView?.onRefreshAdvisoriesSuccess(data!!)
                } else {
                    mView?.onGetAdvisoriesSuccess(data!!)
                }
                if (data != null && !data.isEmpty()) {
                    mPageNumber++
                }
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
                mView?.onGetAdvisoriesFailed(errorResponse?.message!!)
            }

            override fun onFinish() {
                super.onFinish()
                mIsRefresh = false
                mView?.onFinish()
            }

        })
    }

    override fun getNextAdvisories() {
        getAdvisories(mAdvisoryType, mAdvisoryId)
    }

}
