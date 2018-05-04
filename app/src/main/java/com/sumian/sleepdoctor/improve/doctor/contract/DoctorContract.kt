package com.sumian.sleepdoctor.improve.doctor.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView

interface DoctorContract {

    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter<Any>{

    }
}
