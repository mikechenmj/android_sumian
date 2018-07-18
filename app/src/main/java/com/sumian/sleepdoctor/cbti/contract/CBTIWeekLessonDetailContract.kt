package com.sumian.sleepdoctor.cbti.contract

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.cbti.bean.LessonDetail

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

    }


    interface Presenter : BasePresenter<Any> {

        fun getCBTIDetailInfo(id: Int)
    }
}