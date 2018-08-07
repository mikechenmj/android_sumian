package com.sumian.sleepdoctor.doctor.contract

import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseView
import com.sumian.sleepdoctor.doctor.bean.Doctor
import com.sumian.sleepdoctor.widget.webview.SBridgeResult

interface BindDoctorContract {

    interface View : SdBaseView<Presenter> {

        fun onBindDoctorSuccess(message: String)

        fun onBindDoctorFailed(message: String)

        fun onIsSameDoctorCallback(message: String)

    }

    interface Presenter : SdBasePresenter<Any> {

        fun checkBindDoctorState(sBridgeResult: SBridgeResult<Doctor>)

    }
}
