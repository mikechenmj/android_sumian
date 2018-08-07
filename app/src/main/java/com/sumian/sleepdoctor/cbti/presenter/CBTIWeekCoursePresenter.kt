package com.sumian.sleepdoctor.cbti.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.SdBasePresenter.mCalls
import com.sumian.sleepdoctor.cbti.bean.CBTIDataResponse
import com.sumian.sleepdoctor.cbti.bean.Course
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback

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

        call.enqueue(object : BaseResponseCallback<CBTIDataResponse<Course>>() {
            override fun onSuccess(response: CBTIDataResponse<Course>?) {
                response?.let {
                    mView.onGetCBTIWeekLessonSuccess(response.data)
                    mView.onGetCBTIMetaSuccess(response.meta)
                }
            }

            override fun onFailure(code: Int, message: String) {
                mView.onGetCBTIWeekLessonFailed(error = message)
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

        mCalls.add(call)
    }

}