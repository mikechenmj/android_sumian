package com.sumian.sddoctor.service.cbti.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroup

interface CBTIProgressGroupContract {


    interface View : BaseShowLoadingView {

        fun onGetCBTIProgressGroupsSuccess(groups: MutableList<CBTIProgressGroup>)

        fun onGetCBTIProgressGroupsFailed(error: String)

    }

    interface Presenter : IPresenter {

        fun  initDefaultCBTIProgressGroups()

        fun getCBTIProgressGroups(groups: String? = null, isHavePatient: Boolean = false)
    }
}