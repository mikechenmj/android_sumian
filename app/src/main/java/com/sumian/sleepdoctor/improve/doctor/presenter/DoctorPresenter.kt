package com.sumian.sleepdoctor.improve.doctor.presenter

import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.contract.DoctorContract
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeResult

class DoctorPresenter private constructor(view: DoctorContract.View) : DoctorContract.Presenter {

    private var mView: DoctorContract.View = view

    companion object {

        fun initPresenter(view: DoctorContract.View) {
            DoctorPresenter(view)
        }

    }

    override fun checkBindDoctorState(sBridgeResult: SBridgeResult<Doctor>) {
        if (sBridgeResult.code == 0) {
            if (AppManager.getAccountViewModel().userProfile.isBindDoctor) {
                val doctor = sBridgeResult.result
                if (AppManager.getAccountViewModel().userProfile.isSameDoctor(doctor.id)) {
                    mView.onIsSameDoctorCallback(sBridgeResult.message)
                } else {
                    mView.onBindDoctorFailed(App.getAppContext().getString(R.string.bind_other_doctor_state))
                }
            } else {
                AppManager.getAccountViewModel().updateBindDoctor(sBridgeResult.result)
                mView.onBindDoctorSuccess(sBridgeResult.message)
            }
        } else {
            mView.onBindDoctorFailed(sBridgeResult.message)
        }
    }

}
