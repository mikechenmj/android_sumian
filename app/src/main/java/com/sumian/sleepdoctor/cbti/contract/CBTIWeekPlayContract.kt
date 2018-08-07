package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseView
import com.sumian.sleepdoctor.cbti.bean.CoursePlayAuth
import com.sumian.sleepdoctor.cbti.bean.CoursePlayLog

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc:
 */
interface CBTIWeekPlayContract {

    interface View : SdBaseView<Presenter> {

        fun onGetCBTIPlayAuthSuccess(coursePlayAuth: CoursePlayAuth)

        fun onGetCBTIPlayAuthFailed(error: String)

        fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog)

        fun onUploadLessonLogFailed(error: String)

        fun onGetCBTINextPlayAuthSuccess(coursePlayAuth: CoursePlayAuth)

        fun onGetCBTINextPlayAuthFailed(error: String)

    }


    interface Presenter : SdBasePresenter<Any> {

        fun getCBTIPlayAuthInfo(courseId: Int)

        fun uploadCBTIVideoLog(courseId: Int, videoProgress: String, endpoint: Int)

        fun calculatePlayFrame(currentCourseId: Int, currentFrame: Long, oldFrame: Long, totalFrame: Long)

        fun playNextCBTIVideo(courseId: Int)
    }
}