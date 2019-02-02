package com.sumian.sddoctor.service.cbti.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.cbti.bean.CBTIDataResponse
import com.sumian.sddoctor.service.cbti.bean.Course
import com.sumian.sddoctor.service.cbti.contract.CBTIWeekLessonContract

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
class CBTIWeekCoursePresenter constructor(view: CBTIWeekLessonContract.View) : CBTIWeekLessonContract.Presenter {

    private val mView: CBTIWeekLessonContract.View

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: CBTIWeekLessonContract.View): CBTIWeekLessonContract.Presenter {
            return CBTIWeekCoursePresenter(view)
        }
    }

    override fun getCBTIWeekLesson(id: Int) {

        mView.onBegin()

        val call = AppManager.getHttpService().getCBTICourseWeekPart(id)

        call.enqueue(object : BaseSdResponseCallback<CBTIDataResponse<Course>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView.onGetCBTIWeekLessonFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: CBTIDataResponse<Course>?) {
                response?.let {
                    mView.onGetCBTIWeekLessonSuccess(response.data)
                    mView.onGetCBTIMetaSuccess(response.meta)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

        addCall(call)
    }

}