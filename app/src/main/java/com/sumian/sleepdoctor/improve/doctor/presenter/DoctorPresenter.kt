package com.sumian.sleepdoctor.improve.doctor.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.contract.DoctorContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse

/**
 *
 *Created by sm
 * on 2018/5/30 16:30
 * desc:
 **/
class DoctorPresenter private constructor(view: DoctorContract.View) : DoctorContract.Presenter {

    private var mView: DoctorContract.View? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: DoctorContract.View) {
            DoctorPresenter(view)
        }

    }

    override fun getBindDoctorInfo(doctorId: Int) {

        mView?.onBegin()

        val doctorInfoCall = AppManager.getHttpService().getDoctorInfo(doctorId, "services")

        mCalls?.add(doctorInfoCall)

        doctorInfoCall.enqueue(object : BaseResponseCallback<Doctor>() {
            override fun onFailure(errorResponse: ErrorResponse?) {
                mView?.onFailure(errorResponse?.message)
            }

            override fun onSuccess(response: Doctor?) {
                AppManager.getAccountViewModel().updateBindDoctor(response)
                AppManager.getDoctorViewModel().notifyDoctor(response)
                mView?.onGetDoctorInfoSuccess(response)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }
}