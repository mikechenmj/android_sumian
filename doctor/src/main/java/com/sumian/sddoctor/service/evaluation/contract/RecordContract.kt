package com.sumian.sddoctor.service.evaluation.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.sddoctor.service.advisory.bean.Advisory

/**
 *
 *Created by sm
 * on 2018/6/6 12:03
 * desc:
 **/
interface RecordContract {


    interface View : BaseShowLoadingView {

        fun onGetAdvisoryDetailSuccess(advisory: Advisory)

        fun onGetAdvisoryDetailFailed(error: String)

    }



}