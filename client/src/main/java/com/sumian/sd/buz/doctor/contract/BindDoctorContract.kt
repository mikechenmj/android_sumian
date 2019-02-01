package com.sumian.sd.buz.doctor.contract

import com.sumian.common.h5.bean.SBridgeResult
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.doctor.bean.Doctor

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
