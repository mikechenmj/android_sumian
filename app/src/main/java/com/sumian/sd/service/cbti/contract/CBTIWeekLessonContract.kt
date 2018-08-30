package com.sumian.sd.service.cbti.contract

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.service.cbti.bean.CBTIMeta
import com.sumian.sd.service.cbti.bean.Course

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
interface CBTIWeekLessonContract {

    interface View : SdBaseView<Presenter> {

        fun onGetCBTIWeekLessonSuccess(courses: List<Course>)

        fun onGetCBTIMetaSuccess(cbtiMeta: CBTIMeta)

        fun onGetCBTIWeekLessonFailed(error: String)

    }


    interface Presenter : SdBasePresenter<Any> {

        fun getCBTIWeekLesson(id: Int = 1)

    }

}