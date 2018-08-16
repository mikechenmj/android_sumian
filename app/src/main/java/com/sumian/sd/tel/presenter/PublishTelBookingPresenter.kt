package com.sumian.sd.tel.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.BaseResponseCallback
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.tel.bean.TelBooking
import com.sumian.sd.tel.contract.PublishTelBookingContract

/**
 * Created by sm
 *
 * on 2018/8/15
 *
 * desc:
 *
 */
class PublishTelBookingPresenter private constructor(view: PublishTelBookingContract.View) : PublishTelBookingContract.Presenter {

    private var mView: PublishTelBookingContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: PublishTelBookingContract.View): PublishTelBookingContract.Presenter {
            return PublishTelBookingPresenter(view)
        }
    }

    override fun getLatestTelBookingOrder() {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["include"] = "package.servicePackage"

        val call = AppManager.getHttpService().getLatestTelBookingDetail(map = map)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<TelBooking>() {

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

    override fun publishTelBookingOrder(planStartAt: Int, consultingQuestion: String, add: String, include: Boolean) {

        val map = mutableMapOf<String, Any>()

    }
}