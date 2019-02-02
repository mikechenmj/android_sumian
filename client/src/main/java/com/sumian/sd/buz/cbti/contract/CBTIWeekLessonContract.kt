package com.sumian.sd.buz.cbti.contract

import com.sumian.sd.buz.cbti.bean.CBTIMeta
import com.sumian.sd.buz.cbti.bean.Course
import com.sumian.sd.buz.cbti.presenter.CBTIWeekCoursePresenter

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
interface CBTIWeekLessonContract {

    interface View {

        fun setPresenter(presenter: CBTIWeekCoursePresenter) {

        }

        fun onFailure(error: String) {

        }

        fun onBegin() {

        }

        fun onFinish() {

        }

        fun onGetCBTIWeekLessonSuccess(courses: List<Course>)

        fun onGetCBTIMetaSuccess(cbtiMeta: CBTIMeta)

        fun onGetCBTIWeekLessonFailed(error: String)

    }



}