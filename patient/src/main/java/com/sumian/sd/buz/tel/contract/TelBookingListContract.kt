package com.sumian.sd.buz.tel.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sd.buz.tel.bean.TelBooking

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc:
 *
 */
interface TelBookingListContract {

    interface View : BaseShowLoadingView {

        fun onGetTelBookingListSuccess(telBookingList: List<TelBooking>)

        fun onGetTelBookingListFailed(error: String)

        fun onGetNextTelBookingListSuccess(telBookingList: List<TelBooking>)

        fun onRefreshTelBookingListSuccess(telBookingList: List<TelBooking>)

    }


}