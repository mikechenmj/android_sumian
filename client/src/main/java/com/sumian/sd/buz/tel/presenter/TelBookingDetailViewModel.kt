package com.sumian.sd.buz.tel.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.tel.bean.TelBooking
import com.sumian.sd.buz.tel.contract.TelBookingDetailContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
class TelBookingDetailViewModel private constructor(view: TelBookingDetailContract.View) : BaseViewModel() {

    private var mView: TelBookingDetailContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: TelBookingDetailContract.View): TelBookingDetailViewModel {
            return TelBookingDetailViewModel(view)
        }

    }

    fun getTelBookingDetail(telBookingId: Int) {

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