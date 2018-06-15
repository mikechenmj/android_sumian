package com.sumian.sleepdoctor.improve.doctor.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor

/**
 *
 *Created by sm
 * on 2018/5/30 16:25
 * desc:
 **/
class DoctorContract {

    interface View : BaseView<Presenter> {

        fun onGetDoctorInfoSuccess(doctor: Doctor?)

        fun onGetDoctorInfoFailed(error: String)

    }

    interface Presenter : BasePresenter<Any> {

        fun getBindDoctorInfo()

        fun getDoctorServiceInfo(doctorId: Int)
    }
}