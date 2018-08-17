package com.sumian.sd.tel.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.tel.bean.TelBooking

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


    interface Presenter : IPresenter {

        fun getTelBookingDetail(telBookingId: Int)

    }
}