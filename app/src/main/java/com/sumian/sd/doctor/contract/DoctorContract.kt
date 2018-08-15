package com.sumian.sd.doctor.contract

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.doctor.bean.Doctor

/**
 *
 *Created by sm
 * on 2018/5/30 16:25
 * desc:
 **/
class DoctorContract {

    interface View : SdBaseView<Presenter> {

        fun onGetDoctorInfoSuccess(doctor: Doctor?)

        fun onGetDoctorInfoFailed(error: String)

        fun onNotBindDoctor()

    }

    interface Presenter : SdBasePresenter<Any> {

        fun getBindDoctorInfo()

        fun getDoctorServiceInfo(doctorId: Int)
    }
}