package com.sumian.sleepdoctor.improve.doctor.presenter

import com.sumian.sleepdoctor.improve.doctor.contract.DoctorContract

class DoctorPresenter private constructor(view: DoctorContract.View) : DoctorContract.Presenter {

    private var mView: DoctorContract.View? = view

    companion object {
        fun initPrensenter(view: DoctorContract.View) {
            DoctorPresenter(view)
        }
    }


}
