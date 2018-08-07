package com.sumian.sleepdoctor.doctor.contract

import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseView
import com.sumian.sleepdoctor.doctor.bean.Doctor

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