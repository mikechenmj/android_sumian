package com.sumian.sd.buz.tel.presenter

import android.text.TextUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.tel.bean.TelBooking
import com.sumian.sd.buz.tel.contract.TelBookingPublishContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by sm
 *
 * on 2018/8/15
 *
 * desc:
 *
 */
class TelBookingPublishPresenter private constructor(view: TelBookingPublishContract.View) : BaseViewModel(){

    companion object {

        fun init(view: TelBookingPublishContract.View): TelBookingPublishPresenter {
            return TelBookingPublishPresenter(view)
        }
    }

    private var mView: TelBookingPublishContract.View? = null

    init {
        this.mView = view
    }


     fun getLatestTelBookingOrder() {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["include"] = "package.servicePackage"

        val call = AppManager.getSdHttpService().getLatestTelBookingDetail(map = map)
         addCall(call)
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

     fun publishTelBookingOrder(telBookingId: Int, planStartAt: Int, consultingQuestion: String, add: String, include: Boolean) {

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

     fun checkInputContent(consultingQuestion: String, add: String) {
        if (checkContent(consultingQuestion, 20, "请用一句话描述你想要咨询的问题", "问题已超过20个字")) {
            if (checkContent(add, 400, "请详细描述你希望解决的问题", "补充说明已超过400个字")) {
                mView?.onCheckInputContentSuccess(consultingQuestion, add)
            }
        }
    }


    private fun checkContent(content: String, condition: Int, errorTips: String, lengthError: String): Boolean {
        return if (TextUtils.isEmpty(content)) {
            mView?.onCheckInputContentFailed(errorTips)
            false
        } else {
            if (content.length > condition) {
                mView?.onCheckInputContentFailed(lengthError)
                false
            } else {
                true
            }
        }
    }
}