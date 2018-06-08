package com.sumian.sleepdoctor.improve.advisory.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.bean.PictureOssSts
import com.sumian.sleepdoctor.improve.advisory.contract.PublishAdvisoryRecordContact
import com.sumian.sleepdoctor.network.body.AdvisoryRecordBody
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse

/**
 *
 *Created by sm
 * on 2018/6/8 11:11
 * desc:
 **/
class PublishAdvisoryRecordPresenter private constructor(view: PublishAdvisoryRecordContact.View) : PublishAdvisoryRecordContact.Presenter {

    private var mView: PublishAdvisoryRecordContact.View? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {
        fun init(view: PublishAdvisoryRecordContact.View) {
            PublishAdvisoryRecordPresenter(view)
        }
    }

    override fun publishAdvisoryRecord(advisoryId: Int, content: String, onlineReportIds: IntArray?) {

        this.mView?.onBegin()

        val advisoryRecordBody = AdvisoryRecordBody()
        advisoryRecordBody.advisory_id = advisoryId
        advisoryRecordBody.content = content
        advisoryRecordBody.online_report_ids = onlineReportIds

        val call = AppManager.getHttpService().publishAdvisoryRecord(advisoryRecordBody)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<Advisory>() {
            override fun onSuccess(response: Advisory?) {
                AppManager.getAdvisoryViewModel().notifyAdvisory(advisory = response!!)
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
                mView?.onPublishAdvisoryRecordFailed(error = errorResponse?.message!!)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }

    override fun publishPictureAdvisoryRecord(advisoryId: Int, content: String, onlineReportIds: IntArray?, pictureCount: Int) {

        this.mView?.onBegin()

        val advisoryRecordBody = AdvisoryRecordBody()
        advisoryRecordBody.advisory_id = advisoryId
        advisoryRecordBody.content = content
        advisoryRecordBody.online_report_ids = onlineReportIds
        advisoryRecordBody.picture_count = pictureCount

        val call = AppManager.getHttpService().publishPicturesAdvisoryRecord(advisoryRecordBody)
        mCalls.add(call)

        call.enqueue(object : BaseResponseCallback<PictureOssSts>() {

            override fun onSuccess(response: PictureOssSts?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun getLastAdvisory() {

        this.mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = "user,doctor,records"

        val call = AppManager.getHttpService().getLastAdvisoryDetails(map)
        mCalls.add(call)

        call.enqueue(object : BaseResponseCallback<Advisory>() {

            override fun onSuccess(response: Advisory?) {
                AppManager.getAdvisoryViewModel().notifyAdvisory(response!!)
                mView?.onGetLastAdvisorySuccess(response)
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
                mView?.onGetLastAdvisoryFailed(error = errorResponse?.message!!)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })

    }
}