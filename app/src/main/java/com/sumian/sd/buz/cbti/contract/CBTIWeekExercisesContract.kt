package com.sumian.sd.buz.cbti.contract

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.cbti.bean.Exercise

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