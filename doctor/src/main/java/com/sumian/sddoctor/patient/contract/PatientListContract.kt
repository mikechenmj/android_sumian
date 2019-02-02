package com.sumian.sddoctor.patient.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sddoctor.patient.bean.Patient

interface PatientListContract {

    interface View : BaseShowLoadingView {

        fun getPatientsSuccess(patients: ArrayList<Patient>?)

        fun getPatientFailed(error: String)

        fun onHaveMore(isHaveMore: Boolean = false)

        fun onRefreshPatientsSuccess(patients: ArrayList<Patient>?)

    }

}