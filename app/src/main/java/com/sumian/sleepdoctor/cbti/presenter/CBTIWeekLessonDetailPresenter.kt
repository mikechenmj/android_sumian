package com.sumian.sleepdoctor.cbti.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.cbti.bean.LessonDetail
import com.sumian.sleepdoctor.cbti.bean.LessonLog
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonDetailContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc:
 */
class CBTIWeekLessonDetailPresenter(view: CBTIWeekLessonDetailContract.View) : CBTIWeekLessonDetailContract.Presenter {

    private var mView: CBTIWeekLessonDetailContract.View? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: CBTIWeekLessonDetailContract.View): CBTIWeekLessonDetailContract.Presenter {
            return CBTIWeekLessonDetailPresenter(view)
        }
    }

    override fun getCBTIDetailInfo(id: Int) {

        mView?.onBegin()

        val call = AppManager.getHttpService().getCBTILessonDetail(id = id)
        call.enqueue(object : BaseResponseCallback<LessonDetail>() {

            override fun onSuccess(response: LessonDetail?) {
                response?.let {
                    mView?.onGetCBTIDetailSuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetCBTIDetailFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
        mCalls.add(call)
    }

    override fun uploadCBTIVideoLog(id: Int, videoProgress: String, endpoint: Int) {

        mView?.onBegin()

        val call = AppManager.getHttpService().uploadCBTICourseLogs(id, videoProgress.toUpperCase(),endpoint )
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<LessonLog>() {

            override fun onSuccess(response: LessonLog?) {
                response?.let {
                    mView?.onUploadLessonLogSuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onUploadLessonLogFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })

    }
}