package com.sumian.sd.buz.advisory.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.contract.RecordContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

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
        val call = AppManager.getSdHttpService().getDoctorAdvisoryDetails(advisoryId, map)
        //addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Advisory>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetAdvisoryDetailFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Advisory?) {
                mView?.onGetAdvisoryDetailSuccess(response!!)
            }

            override fun onFinish() {
                mView?.onFinish()
            }

        })
    }
}