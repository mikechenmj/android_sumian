package com.sumian.sd.service.advisory.presenter

import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.contract.RecordContract
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.callback.BaseResponseCallback

/**
 *
 *Created by sm
 * on 2018/6/6 12:06
 * desc:
 **/
class RecordPresenter private constructor(view: RecordContract.View) : RecordContract.Presenter {

    private var mView: RecordContract.View? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: RecordContract.View) {
            RecordPresenter(view)
        }
    }

    override fun getAdvisoryDetail(advisoryId: Int) {

        this.mView?.onBegin()

        val map = mutableMapOf<String, Any>()
        map["include"] = "user,doctor,records"
        val call = AppManager.getHttpService().getDoctorAdvisoryDetails(advisoryId, map)
        mCalls.add(call)

        call.enqueue(object : BaseResponseCallback<Advisory>() {
            override fun onSuccess(response: Advisory?) {
                mView?.onGetAdvisoryDetailSuccess(response!!)
            }

            override fun onFailure(code: Int, message: String) {
                mView?.onGetAdvisoryDetailFailed(message)
            }

            override fun onFinish() {
                mView?.onFinish()
            }

        })
    }
}