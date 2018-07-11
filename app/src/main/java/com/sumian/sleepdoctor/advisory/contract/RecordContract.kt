package com.sumian.sleepdoctor.advisory.contract

import com.sumian.sleepdoctor.advisory.bean.Advisory
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView

/**
 *
 *Created by sm
 * on 2018/6/6 12:03
 * desc:
 **/
interface RecordContract {


    interface View : BaseView<Presenter> {

        fun onGetAdvisoryDetailSuccess(advisory: Advisory)

        fun onGetAdvisoryDetailFailed(error: String)

    }

    interface Presenter : BasePresenter<Any> {

        fun getAdvisoryDetail(advisoryId: Int = 0)
    }


}