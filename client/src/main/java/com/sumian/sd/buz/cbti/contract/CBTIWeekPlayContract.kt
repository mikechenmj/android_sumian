package com.sumian.sd.buz.cbti.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.cbti.bean.CoursePlayAuth
import com.sumian.sd.buz.cbti.bean.CoursePlayLog
import com.sumian.sd.buz.cbti.presenter.CBTICoursePlayAuthPresenter

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc:
 */
interface CBTIWeekPlayContract {

    interface View : SdBaseView<CBTICoursePlayAuthPresenter> {

        fun onGetCBTIPlayAuthSuccess(coursePlayAuth: CoursePlayAuth)

        fun onGetCBTIPlayAuthFailed(error: String)

        fun onUploadLessonLogSuccess(coursePlayLog: CoursePlayLog)

        fun onUploadLessonLogFailed(error: String)

        fun onGetCBTINextPlayAuthSuccess(coursePlayAuth: CoursePlayAuth)

        fun onGetCBTINextPlayAuthFailed(error: String)

        fun onUploadCBTIQuestionnairesSuccess(coursePlayAuth: CoursePlayAuth)

        fun onUploadCBTIQuestionnairesFailed(error: String)
    }


}