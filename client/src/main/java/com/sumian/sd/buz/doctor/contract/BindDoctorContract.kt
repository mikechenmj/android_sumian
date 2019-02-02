package com.sumian.sd.buz.doctor.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.doctor.presenter.BindDoctorPresenter

interface BindDoctorContract {

    interface View : SdBaseView<BindDoctorPresenter> {

        fun onBindDoctorSuccess(message: String)

        fun onBindDoctorFailed(message: String)

        fun onIsSameDoctorCallback(message: String)

    }

}
