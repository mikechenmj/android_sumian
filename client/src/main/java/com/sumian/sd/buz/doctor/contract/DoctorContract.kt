package com.sumian.sd.buz.doctor.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.doctor.bean.Doctor
import com.sumian.sd.buz.doctor.presenter.DoctorPresenter

/**
 *
 *Created by sm
 * on 2018/5/30 16:25
 * desc:
 **/
class DoctorContract {

    interface View : SdBaseView<DoctorPresenter> {

        fun onGetDoctorInfoSuccess(doctor: Doctor?)

        fun onGetDoctorInfoFailed(error: String)

        fun onNotBindDoctor()

    }

    interface Presenter {

        fun getBindDoctorInfo()

        fun getDoctorServiceInfo(doctorId: Int)
    }
}