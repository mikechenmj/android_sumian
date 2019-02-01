package com.sumian.sddoctor.service.cbti.contract

import com.sumian.sddoctor.service.advisory.onlinereport.SdBasePresenter
import com.sumian.sddoctor.service.advisory.onlinereport.SdBaseView
import com.sumian.sddoctor.service.cbti.bean.CoursePlayAuth
import com.sumian.sddoctor.service.cbti.bean.CoursePlayLog

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

        fun onUploadCBTIQuestionnairesSuccess(coursePlayAuth: CoursePlayAuth)

        fun onUploadCBTIQuestionnairesFailed(error: String)
    }


    interface Presenter : SdBasePresenter<Any> {

        fun getCBTIPlayAuthInfo(courseId: Int)

        fun uploadCBTIVideoLog(videoId: String, courseId: Int, videoProgress: String, endpoint: Int)

        fun calculatePlayFrame(videoId: String, currentCourseId: Int, currentFrame: Long, oldFrame: Long, totalFrame: Long)

        fun playNextCBTIVideo(courseId: Int)

        fun uploadCBTIQuestionnaires(courseId: Int, position: Int)

        fun uploadCBTICourseWatchLog(courseId: Int, videoId: String)
    }
}