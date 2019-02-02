package com.sumian.sddoctor.service.cbti.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroup

interface CBTIProgressGroupContract {


    interface View : BaseShowLoadingView {

        fun onGetCBTIProgressGroupsSuccess(groups: MutableList<CBTIProgressGroup>)

        fun onGetCBTIProgressGroupsFailed(error: String)

    }

}