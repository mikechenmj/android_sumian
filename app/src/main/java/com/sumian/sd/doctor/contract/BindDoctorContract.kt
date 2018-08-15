package com.sumian.sd.doctor.contract

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.doctor.bean.Doctor
import com.sumian.sd.widget.webview.SBridgeResult

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
