package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseView
import com.sumian.sleepdoctor.cbti.bean.Exercise

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
interface CBTIWeekExercisesContract {

    interface View : SdBaseView<Presenter> {

        fun onGetCBTIWeekPracticeSuccess(exercises: List<Exercise>)

        fun onGetCBTIWeekPracticeFailed(error: String)
    }


    interface Presenter : SdBasePresenter<Any> {

        fun getCBTIWeekExercises(id: Int = 1)
    }

}