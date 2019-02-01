package com.sumian.sddoctor.service.advisory.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.advisory.bean.Advisory
import com.sumian.sddoctor.service.advisory.contract.RecordContract

/**
 *
 *Created by sm
 * on 2018/6/6 12:06
 * desc:
 **/
class RecordPresenter private constructor(view: RecordContract.View) : BaseViewModel() {

    private var mView: RecordContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: RecordContract.View): RecordPresenter {
            return RecordPresenter(view)
        }
    }

     fun getAdvisoryDetail(advisoryId: Int) {

        this.mView?.showLoading()

        val call = AppManager.getHttpService().getDoctorAdvisoryDetails(advisoryId)
         addCall(call)

        call.enqueue(object : BaseSdResponseCallback<Advisory>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetAdvisoryDetailFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Advisory?) {
                mView?.onGetAdvisoryDetailSuccess(response!!)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }

        })
    }
}