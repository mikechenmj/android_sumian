package com.sumian.sd.buz.cbti.contract

import com.sumian.common.base.BaseViewModel
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

    interface View : SdBaseView<BaseViewModel> {

        fun onGetCBTIWeekPracticeSuccess(exercises: List<Exercise>)

        fun onGetCBTIWeekPracticeFailed(error: String)
    }


    interface Presenter {

        fun getCBTIWeekExercises(id: Int = 1)
    }

}