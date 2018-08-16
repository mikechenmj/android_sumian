package com.sumian.sd.tel.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.tel.bean.TelBooking

/**
 * Created by sm
 *
 * on 2018/8/15
 *
 * desc:
 *
 */
interface PublishTelBookingContract {

    interface View : BaseShowLoadingView {

        fun onGetLatestTelBookingOrderSuccess(latestTelBooking: TelBooking)

        fun onGetLatestTelBookingOrderFailed(error: String)

        fun onPublishTelBookingOrderSuccess(telBooking: TelBooking)

        fun onPublishTelBookingOrderFailed(error: String)
    }


    interface Presenter : IPresenter {

        fun getLatestTelBookingOrder()

        fun publishTelBookingOrder(planStartAt: Int, consultingQuestion: String, add: String, include: Boolean = false)
    }
}