package com.sumian.sd.service.tel.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.service.tel.bean.TelBooking

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


    interface Presenter : IPresenter {

        fun getTelBookingList(telBookingListType: Int = TelBooking.UN_FINISHED_TYPE)

        fun refreshTelBookingList()

        fun getNextTelBookingList()

    }
}