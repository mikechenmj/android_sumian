package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseView
import com.sumian.sleepdoctor.cbti.bean.CBTIMeta
import com.sumian.sleepdoctor.cbti.bean.Course

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