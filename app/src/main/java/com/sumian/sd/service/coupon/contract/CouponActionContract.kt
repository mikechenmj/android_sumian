package com.sumian.sd.service.coupon.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter

interface CouponActionContract {

    interface View : BaseShowLoadingView {

        fun onInputCouponSuccess()

        fun onInputCouponFailed(error: String)

        fun onCheckFailed(error: String)

    }


    interface Presenter : IPresenter {

        fun checkCoupon(coupon: String)
    }
}