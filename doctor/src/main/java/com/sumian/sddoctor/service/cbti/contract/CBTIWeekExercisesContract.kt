package com.sumian.sddoctor.service.cbti.contract

import com.sumian.sddoctor.service.advisory.onlinereport.SdBasePresenter
import com.sumian.sddoctor.service.advisory.onlinereport.SdBaseView
import com.sumian.sddoctor.service.cbti.bean.Exercise

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