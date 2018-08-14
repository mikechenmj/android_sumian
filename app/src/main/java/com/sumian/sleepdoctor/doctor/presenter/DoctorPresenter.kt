package com.sumian.sleepdoctor.doctor.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.SdBasePresenter.mCalls
import com.sumian.sleepdoctor.doctor.bean.Doctor
import com.sumian.sleepdoctor.doctor.contract.DoctorContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback

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

        val doctorInfoCall = AppManager.getHttpService().getBindDoctorInfo()

        mCalls?.add(doctorInfoCall)

        doctorInfoCall.enqueue(object : BaseResponseCallback<Doctor>() {
            override fun onFailure(code: Int, message: String) {
                if (code == 1) {
                    AppManager.getAccountViewModel().updateBoundDoctor(null)
                    AppManager.getDoctorViewModel().notifyDoctor(null)
                    mView?.onNotBindDoctor()
                } else {
                    mView?.onGetDoctorInfoFailed(message)
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

        val doctorInfoCall = AppManager.getHttpService().getDoctorInfo(doctorId)

        mCalls?.add(doctorInfoCall)

        doctorInfoCall.enqueue(object : BaseResponseCallback<Doctor>() {
            override fun onFailure(code: Int, message: String) {
                mView?.onGetDoctorInfoFailed(message)
            }

            override fun onSuccess(response: Doctor?) {
                AppManager.getAccountViewModel().updateBoundDoctor(response)
                AppManager.getDoctorViewModel().notifyDoctor(response)
                mView?.onGetDoctorInfoSuccess(response)
            }
        })

    }
}