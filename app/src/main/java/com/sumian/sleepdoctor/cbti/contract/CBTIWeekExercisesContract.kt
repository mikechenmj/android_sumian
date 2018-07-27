package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.cbti.bean.Exercise

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
interface CBTIWeekExercisesContract {

    interface View : BaseView<Presenter> {

        fun onGetCBTIWeekPracticeSuccess(exercises: List<Exercise>)

        fun onGetCBTIWeekPracticeFailed(error: String)
    }


    interface Presenter : BasePresenter<Any> {

        fun getCBTIWeekExercises(id: Int = 1)
    }

}