package com.sumian.sleepdoctor.cbti.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.cbti.bean.Courses
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
class CBTIWeekLessonPresenter constructor(view: CBTIWeekLessonContract.View) : CBTIWeekLessonContract.Presenter {

    private val mView: CBTIWeekLessonContract.View

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: CBTIWeekLessonContract.View): CBTIWeekLessonContract.Presenter {
            return CBTIWeekLessonPresenter(view)
        }
    }

    override fun getCBTIWeekLesson(id: Int) {

        mView.onBegin()

        val call = AppManager.getHttpService().getCBTILessonWeekPart(id)

        call.enqueue(object : BaseResponseCallback<Courses>() {
            override fun onSuccess(response: Courses?) {
                response?.let {
                    mView.onGetCBTIWeekLessonSuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView.onGetCBTIWeekLessonFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

        mCalls.add(call)
    }

}