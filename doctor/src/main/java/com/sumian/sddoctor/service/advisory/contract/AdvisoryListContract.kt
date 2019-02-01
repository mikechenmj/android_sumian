package com.sumian.sddoctor.service.advisory.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.advisory.bean.Advisory

/**
 *
 *Created by sm
 * on 2018/6/4 14:47
 * desc:
 **/
interface AdvisoryListContract {

    interface View : BaseShowLoadingView {

        fun onGetAdvisoriesSuccess(advisories: List<Advisory>)

        fun onGetAdvisoriesFailed(error: String)

        fun onRefreshAdvisoriesSuccess(advisories: List<Advisory>)

        fun onHaveMore(isHaveMore: Boolean = false)

    }


    interface Presenter : IPresenter {

        fun refreshAdvisories()

        fun getAdvisories(advisoryType: Int = Advisory.ALL_TYPE)

        fun getNextAdvisories()

    }
}