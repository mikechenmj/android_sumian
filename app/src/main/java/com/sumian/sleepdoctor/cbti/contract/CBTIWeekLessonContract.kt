package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.cbti.bean.Courses

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
interface CBTIWeekLessonContract {

    interface View : BaseView<Presenter> {

        fun onGetCBTIWeekLessonSuccess(courses: Courses)

        fun onGetCBTIWeekLessonFailed(error: String)

    }


    interface Presenter : BasePresenter<Any> {

        fun getCBTIWeekLesson(id: Int = 1)

    }

}