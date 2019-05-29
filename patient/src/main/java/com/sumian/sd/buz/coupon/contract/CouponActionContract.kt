package com.sumian.sd.buz.coupon.contract

import com.sumian.common.base.BaseShowLoadingView

interface CouponActionContract {

    interface View : BaseShowLoadingView {

        fun onInputCouponSuccess()

        fun onInputCouponFailed(error: String)

        fun onCheckFailed(error: String)

    }


}