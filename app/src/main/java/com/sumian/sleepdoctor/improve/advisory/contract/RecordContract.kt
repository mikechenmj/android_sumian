package com.sumian.sleepdoctor.improve.advisory.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory

/**
 *
 *Created by sm
 * on 2018/6/6 12:03
 * desc:
 **/
interface RecordContract {


    interface View : BaseView<Presenter> {

        fun onGetAdvisoryDetailSuccess(advisory: Advisory)

        fun onGetAdvisoryDetailFailed(error:String)

    }

    interface Presenter : BasePresenter<Any> {

        fun getAdvisoryDetail(advisoryId: Int = 0)
    }


}