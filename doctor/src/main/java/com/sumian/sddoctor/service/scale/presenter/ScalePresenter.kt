package com.sumian.sddoctor.service.scale.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.scale.bean.Scale
import com.sumian.sddoctor.service.scale.contract.ScaleContract

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
class ScalePresenter private constructor(view: ScaleContract.View) : BaseViewModel(){

    companion object {

        fun init(view: ScaleContract.View): ScalePresenter {
            return ScalePresenter(view)
        }

    }

    private var mView: ScaleContract.View? = null

    init {
        this.mView = view
    }

    fun getScales() {

        mView?.showLoading()

        val call = AppManager.getHttpService().getScale()

        call.enqueue(object : BaseSdResponseCallback<List<Scale>>() {

            override fun onSuccess(response: List<Scale>?) {
                response?.let {
                    mView?.onGetScalesSuccess(it)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetScalesFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })

    }

    fun sendScale(patientId: Int, scaleIds: List<Int>) {

        if (scaleIds.isEmpty()) {
            mView?.onSendScaleFailed("请先选择量表")
            return
        }

        mView?.showLoading()

        val scaldIds = StringBuilder()

        scaleIds.forEachIndexed { _, id ->
            scaldIds.append("$id,")
        }

        scaldIds.delete(scaldIds.lastIndexOf(","), scaldIds.length)

        val map = mutableMapOf<String, Any>()

        map["scale_ids"] = scaldIds.toString()
        map["user_ids"] = patientId

        val call = AppManager.getHttpService().sendScale(map = map)

        call.enqueue(object : BaseSdResponseCallback<Void>() {

            override fun onSuccess(response: Void?) {
                mView?.onSendScaleSuccess("发送成功")
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onSendScaleFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })

    }
}