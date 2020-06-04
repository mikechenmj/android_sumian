package com.sumian.sd.buz.doctor.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.h5.bean.SBridgeResult
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.doctor.bean.Doctor

class BindDoctorPresenter private constructor(view: DoctorWebContainerView) : BaseViewModel() {

    private var mView: DoctorWebContainerView? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        @JvmStatic
        fun init(view: DoctorWebContainerView): BindDoctorPresenter {
            return BindDoctorPresenter(view)
        }

    }

    fun checkBindDoctorState(sBridgeResult: SBridgeResult<Doctor>) {
        if (sBridgeResult.code == 0) {
            if (AppManager.getAccountViewModel().userInfo!!.isBindDoctor) {
                val doctor = sBridgeResult.result
                if (AppManager.getAccountViewModel().userInfo!!.isSameDoctor(doctor.id)) {
                    mView?.onIsSameDoctorCallback(sBridgeResult.message)
                } else {
                    mView?.onBindDoctorFailed(App.getAppContext().getString(R.string.bind_other_doctor_state))
                }
            } else {
                AppManager.getAccountViewModel().updateBoundDoctor(sBridgeResult.result)
                AppManager.getDoctorViewModel().notifyDoctor(sBridgeResult.result)
                mView?.onBindDoctorSuccess(sBridgeResult.message)
            }
        } else {
            mView?.onBindDoctorFailed(sBridgeResult.message)
        }
    }
}

interface DoctorWebContainerView {
    fun setPresenter(presenter: BindDoctorPresenter)
    fun onIsSameDoctorCallback(message: String)
    fun onBindDoctorFailed(message: String)
    fun onBindDoctorSuccess(message: String)
}
