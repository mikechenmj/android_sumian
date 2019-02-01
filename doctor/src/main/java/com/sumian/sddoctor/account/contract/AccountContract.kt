package com.sumian.sddoctor.account.contract

import com.sumian.sddoctor.base.BasePresenter
import com.sumian.sddoctor.base.BaseView
import com.sumian.sddoctor.login.login.bean.DoctorInfo

interface AccountContract {

    interface View : BaseView {

        fun onModifySuccess(doctorInfo: DoctorInfo)

        fun onModifyFailed(error: String)

    }


    interface Presenter : BasePresenter {

        fun modifyDoctorInfo(modifyType: Int, newContent: String)

        fun onRelease()
    }

}