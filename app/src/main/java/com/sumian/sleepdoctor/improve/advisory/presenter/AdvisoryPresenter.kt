package com.sumian.sleepdoctor.improve.advisory.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.AdvisoryContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse
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

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: AdvisoryContract.View) {
            AdvisoryPresenter(view)
        }
    }

    override fun refreshAdvisories() {
        this.mPageNumber = 1
        getAdvisories(mAdvisoryType, mAdvisoryId)
    }

    override fun getAdvisories(advisoryType: Int, advisoryId: Int) {
        this.mAdvisoryType = advisoryType
        this.mAdvisoryId = advisoryId

        mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = "user,doctor,records"
        map["page"] = mPageNumber
        //map["per_page"]=15
        map["type"] = advisoryType

        val call = AppManager.getHttpService().getDoctorAdvisories(map)
        mCalls?.add(call)
        call.enqueue(object : BaseResponseCallback<ArrayList<Advisory>>(), Callback<ArrayList<Advisory>> {
            override fun onSuccess(response: ArrayList<Advisory>?) {
                mView?.onGetAdvisoriesSuccess(response!!)
                mPageNumber++
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
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