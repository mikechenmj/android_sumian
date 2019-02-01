package com.sumian.sddoctor.service.cbti.contract

import com.sumian.sddoctor.service.advisory.onlinereport.SdBasePresenter
import com.sumian.sddoctor.service.advisory.onlinereport.SdBaseView
import com.sumian.sddoctor.service.cbti.bean.CBTIMeta
import com.sumian.sddoctor.service.cbti.bean.Course

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