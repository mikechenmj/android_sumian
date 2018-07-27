package com.sumian.sleepdoctor.doctor.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.doctor.bean.Doctor
import com.sumian.sleepdoctor.widget.webview.SBridgeResult

interface BindDoctorContract {

    interface View : BaseView<Presenter> {

        fun onBindDoctorSuccess(message: String)

        fun onBindDoctorFailed(message: String)

        fun onIsSameDoctorCallback(message: String)

    }

    interface Presenter : BasePresenter<Any> {

        fun checkBindDoctorState(sBridgeResult: SBridgeResult<Doctor>)

    }
}
