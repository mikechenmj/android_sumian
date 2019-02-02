package com.sumian.sddoctor.patient.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sddoctor.patient.bean.Patient

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
interface ModifyPatientTagContract {

    interface View : BaseShowLoadingView {

        fun onGetPatientSuccess(patient: Patient)

        fun onGetPatientFailed(error: String)

        fun onConsultedSuccess(patient: Patient)

        fun onConsultedFailed(error: String)

    }

}