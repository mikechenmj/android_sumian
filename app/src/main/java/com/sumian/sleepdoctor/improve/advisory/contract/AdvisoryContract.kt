package com.sumian.sleepdoctor.improve.advisory.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory

/**
 *
 *Created by sm
 * on 2018/6/4 14:47
 * desc:
 **/
interface AdvisoryContract {

    interface View : BaseView<Presenter> {

        fun onGetAdvisoriesSuccess(advisories: ArrayList<Advisory>)

        fun onGetAdvisoriesFailed(error: String)

        fun onGetNextAdvisoriesSuccess(advisories: ArrayList<Advisory>)

        fun onGetAdvisoryDetailSuccess(advisory: Advisory)

        fun onGetAdvisoryDetailFailed(error: String)

    }


    interface Presenter : BasePresenter<Any> {

        fun  refreshAdvisories()

        fun getAdvisories(advisoryType: Int = Advisory.UNUSED_TYPE, advisoryId: Int)

        fun getNextAdvisories()

    }
}