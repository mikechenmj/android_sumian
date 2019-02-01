package com.sumian.sd.buz.doctor.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.buz.doctor.bean.Doctor
import com.sumian.sd.buz.doctor.contract.DoctorContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

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

    override fun getBindDoctorInfo() {

        mView?.onBegin()

        val doctorInfoCall = AppManager.getSdHttpService().getBindDoctorInfo()

        mCalls?.add(doctorInfoCall)

        doctorInfoCall.enqueue(object : BaseSdResponseCallback<Doctor>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                if (errorResponse.code == 1) {
                    AppManager.getAccountViewModel().updateBoundDoctor(null)
                    AppManager.getDoctorViewModel().notifyDoctor(null)
                    mView?.onNotBindDoctor()
                } else {
                    mView?.onGetDoctorInfoFailed(error = errorResponse.message)
                }
            }

            override fun onSuccess(response: Doctor?) {
                AppManager.getAccountViewModel().updateBoundDoctor(response)
                AppManager.getDoctorViewModel().notifyDoctor(response)
                mView?.onGetDoctorInfoSuccess(response)
                getDoctorServiceInfo(response?.id!!)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }

    override fun getDoctorServiceInfo(doctorId: Int) {

        val doctorInfoCall = AppManager.getSdHttpService().getDoctorInfo(doctorId)

        mCalls?.add(doctorInfoCall)

        doctorInfoCall.enqueue(object : BaseSdResponseCallback<Doctor>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetDoctorInfoFailed(errorResponse.message)

            }

            override fun onSuccess(response: Doctor?) {
                AppManager.getAccountViewModel().updateBoundDoctor(response)
                AppManager.getDoctorViewModel().notifyDoctor(response)
                mView?.onGetDoctorInfoSuccess(response)
            }
        })

    }
}