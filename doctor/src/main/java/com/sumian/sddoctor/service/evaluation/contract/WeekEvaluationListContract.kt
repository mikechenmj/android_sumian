package com.sumian.sddoctor.service.evaluation.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.evaluation.bean.WeekEvaluation

/**
 *
 *Created by sm
 * on 2018/6/4 14:47
 * desc:
 **/
interface WeekEvaluationListContract {

    interface View : BaseShowLoadingView {

        fun onGetEvaluationListSuccess(advisories: List<WeekEvaluation>)

        fun onGetEvaluationListFailed(error: String)

        fun onRefreshEvaluationListSuccess(advisories: List<WeekEvaluation>)

        fun onHaveMore(isHaveMore: Boolean = false)

    }


    interface Presenter : IPresenter {

        fun refreshEvaluationList()

        fun getEvaluationList(WeekEvaluationType: String = WeekEvaluation.ALL_TYPE)

        fun getNextEvaluationList()

    }
}