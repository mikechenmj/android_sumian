package com.sumian.sd.buz.cbti.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.cbti.bean.CBTIDataResponse
import com.sumian.sd.buz.cbti.bean.Course
import com.sumian.sd.buz.cbti.contract.CBTIWeekLessonContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
class CBTIWeekCoursePresenter constructor(view: CBTIWeekLessonContract.View) : BaseViewModel() {

    private val mView: CBTIWeekLessonContract.View

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: CBTIWeekLessonContract.View): CBTIWeekCoursePresenter {
            return CBTIWeekCoursePresenter(view)
        }
    }

    fun getCBTIWeekLesson(id: Int) {

        mView.onBegin()

        val call = AppManager.getSdHttpService().getCBTICourseWeekPart(id)

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