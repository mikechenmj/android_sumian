package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.cbti.bean.LessonDetail
import com.sumian.sleepdoctor.cbti.bean.LessonLog

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc:
 */
interface CBTIWeekLessonDetailContract {

    interface View : BaseView<Presenter> {

        fun onGetCBTIDetailSuccess(lessonDetail: LessonDetail)

        fun onGetCBTIDetailFailed(error: String)

        fun onUploadLessonLogSuccess(lessonLog: LessonLog)

        fun onUploadLessonLogFailed(error: String)

    }


    interface Presenter : BasePresenter<Any> {

        fun getCBTIDetailInfo(id: Int)

        fun uploadCBTIVideoLog(id: Int, videoProgress: String, endpoint: Int)
    }
}