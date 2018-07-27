package com.sumian.sleepdoctor.advisory.presenter

import com.sumian.sleepdoctor.advisory.bean.Advisory
import com.sumian.sleepdoctor.advisory.contract.RecordContract
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse

/**
 *
 *Created by sm
 * on 2018/6/6 12:06
 * desc:
 **/
class RecordPresenter private constructor(view: RecordContract.View) : RecordContract.Presenter {

    private var mView: RecordContract.View? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: RecordContract.View) {
            RecordPresenter(view)
        }
    }

    override fun getAdvisoryDetail(advisoryId: Int) {

        this.mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = "user,doctor,records"
        val call = AppManager.getHttpService().getDoctorAdvisoryDetails(advisoryId, map)
        mCalls.add(call)

        call.enqueue(object : BaseResponseCallback<Advisory>() {
            override fun onSuccess(response: Advisory?) {
                mView?.onGetAdvisoryDetailSuccess(response!!)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetAdvisoryDetailFailed(errorResponse.message)
            }

            override fun onFinish() {
                mView?.onFinish()
            }

        })
    }
}