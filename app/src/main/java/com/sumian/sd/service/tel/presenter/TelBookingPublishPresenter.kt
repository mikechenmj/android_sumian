package com.sumian.sd.service.tel.presenter

import android.text.TextUtils
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.service.tel.contract.TelBookingPublishContract

/**
 * Created by sm
 *
 * on 2018/8/15
 *
 * desc:
 *
 */
class TelBookingPublishPresenter private constructor(view: TelBookingPublishContract.View) : TelBookingPublishContract.Presenter {

    companion object {

        fun init(view: TelBookingPublishContract.View): TelBookingPublishContract.Presenter {
            return TelBookingPublishPresenter(view)
        }
    }

    private var mView: TelBookingPublishContract.View? = null

    init {
        this.mView = view
    }


    override fun getLatestTelBookingOrder() {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["include"] = "package.servicePackage"

        val call = AppManager.getSdHttpService().getLatestTelBookingDetail(map = map)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<TelBooking>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetLatestTelBookingOrderFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: TelBooking?) {
                response?.let {
                    mView?.onGetLatestTelBookingOrderSuccess(it)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })
    }

    override fun publishTelBookingOrder(telBookingId: Int, planStartAt: Int, consultingQuestion: String, add: String, include: Boolean) {

        this.mView?.showLoading()

        val map = mutableMapOf<String, Any>()
        map["plan_start_at"] = planStartAt
        map["consulting_question"] = consultingQuestion
        map["add"] = add
        if (include) {
            map["include"] = "package.servicePackage"
        }

        val call = AppManager.getSdHttpService().publishTelBooking(telBookingId, map = map)

        call.enqueue(object : BaseSdResponseCallback<TelBooking>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onPublishTelBookingOrderFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: TelBooking?) {
                response?.let {
                    mView?.onPublishTelBookingOrderSuccess(it)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })
    }

    override fun checkInputContent(consultingQuestion: String, add: String) {
        if (checkContent(consultingQuestion, 21, "请用一句话描述你想要咨询的问题", "问题已超过21个字")) {
            if (checkContent(add, 401, "请详细描述你希望解决的问题", "补充说明已超过401个字")) {
                mView?.onCheckInputContentSuccess(consultingQuestion, add)
            }
        }
    }


    private fun checkContent(content: String, condition: Int, errorTips: String, lengthError: String): Boolean {
        return if (TextUtils.isEmpty(content)) {
            mView?.onCheckInputContentFailed(errorTips)
            false
        } else {
            if (content.length >= condition) {
                mView?.onCheckInputContentFailed(lengthError)
                false
            } else {
                true
            }
        }
    }
}