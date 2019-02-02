package com.sumian.sd.buz.tel.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sd.buz.tel.bean.TelBooking

/**
 * Created by sm
 *
 * on 2018/8/15
 *
 * desc:
 *
 */
interface TelBookingPublishContract {

    interface View : BaseShowLoadingView {

        fun onGetLatestTelBookingOrderSuccess(latestTelBooking: TelBooking)

        fun onGetLatestTelBookingOrderFailed(error: String)

        fun onPublishTelBookingOrderSuccess(telBooking: TelBooking)

        fun onPublishTelBookingOrderFailed(error: String)

        fun onCheckInputContentSuccess(consultingQuestion: String, add: String)

        fun onCheckInputContentFailed(error: String)

    }


}