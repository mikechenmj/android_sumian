package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
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

    interface View : BaseView<Presenter> {

        fun onGetCBTIWeekLessonSuccess(courses: List<Course>)

        fun onGetCBTIMetaSuccess(cbtiMeta: CBTIMeta)

        fun onGetCBTIWeekLessonFailed(error: String)

    }


    interface Presenter : BasePresenter<Any> {

        fun getCBTIWeekLesson(id: Int = 1)

    }

}