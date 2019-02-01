package com.sumian.sddoctor.patient.contract

import com.sumian.sddoctor.base.BasePresenter
import com.sumian.sddoctor.base.BaseView
import com.sumian.sddoctor.patient.bean.Group

interface PatientGroupContract {

    interface View : BaseView {

        fun onRefreshGroupsSuccess(groups: MutableList<Group>)

        fun onGetGroupsSuccess(groups: MutableList<Group>)

        fun onGetGroupsFailed(error: String)

        fun onHideEmptyView()

        fun onShowEmptyView()

    }


    interface Presenter : BasePresenter {

        fun getLevelGroupPatients(vararg patientLevels: Int)

        fun getFaceGroupPatients(vararg patientFaces: Int)

        fun refreshGroupPatients()

        fun setGroups(groups: MutableList<Group>)

    }
}