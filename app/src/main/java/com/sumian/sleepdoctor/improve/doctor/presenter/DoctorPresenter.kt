package com.sumian.sleepdoctor.improve.doctor.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.contract.DoctorContract
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

    override fun getBindDoctorInfo(doctorId: Int) {

        val doctorInfoCall = AppManager.getHttpService().getDoctorInfo(doctorId, "services")

        mView?.onBegin()

        mCalls?.add(doctorInfoCall)

        doctorInfoCall.enqueue(object : BaseResponseCallback<Doctor>() {

            override fun onSuccess(response: Doctor?) {
                AppManager.getAccountViewModel().updateBindDoctor(response)
                mView?.onGetDoctorInfoSuccess(response)
            }

            override fun onFailure(error: String?) {
                mView?.onFailure(error)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }
}