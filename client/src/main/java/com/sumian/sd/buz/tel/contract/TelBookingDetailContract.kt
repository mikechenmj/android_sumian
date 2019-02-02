package com.sumian.sd.buz.tel.contract

import com.sumian.common.base.BaseViewModel
import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.sd.buz.tel.bean.TelBooking

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
interface TelBookingDetailContract {

    interface View : BaseShowLoadingView {

        fun onGetTelBookingDetailSuccess(telBooking: TelBooking)

        fun onGetTelBookingDetailFailed(error: String)

    }


}

abstract class TelBookingDetail : BaseViewModel() {

    abstract fun getTelBookingDetail(telBookingId: Int)

}
