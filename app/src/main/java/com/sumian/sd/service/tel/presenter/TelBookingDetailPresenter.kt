package com.sumian.sd.service.tel.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.service.tel.contract.TelBookingDetailContract

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
class TelBookingDetailPresenter private constructor(view: TelBookingDetailContract.View) : TelBookingDetailContract.Presenter {

    private var mView: TelBookingDetailContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: TelBookingDetailContract.View): TelBookingDetailContract.Presenter {
            return TelBookingDetailPresenter(view)
        }

    }

    override fun getTelBookingDetail(telBookingId: Int) {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()
        map["include"] = "package.servicePackage"

        val call = AppManager.getSdHttpService().getTelBookingDetail(telBookingId = telBookingId, map = map)

        call.enqueue(object : BaseSdResponseCallback<TelBooking>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetTelBookingDetailFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: TelBooking?) {
                response?.let {
                    mView?.onGetTelBookingDetailSuccess(it)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })

    }
}