package com.sumian.sd.advisory.contract

import com.sumian.sd.advisory.bean.Advisory
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView

/**
 *
 *Created by sm
 * on 2018/6/4 14:47
 * desc:
 **/
interface AdvisoryListContract {

    interface View : SdBaseView<Presenter> {

        fun onGetAdvisoriesSuccess(advisories: List<Advisory>)

        fun onGetAdvisoriesFailed(error: String)

        fun onGetNextAdvisoriesSuccess(advisories: List<Advisory>)

        fun onRefreshAdvisoriesSuccess(advisories: List<Advisory>)

    }


    interface Presenter : SdBasePresenter<Any> {

        fun refreshAdvisories()

        fun getAdvisories(advisoryType: Int = Advisory.UNUSED_TYPE)

        fun getNextAdvisories()

    }
}