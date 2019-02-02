package com.sumian.sd.buz.coupon.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sd.buz.coupon.bean.Coupon

interface CouponListContract {

    interface View : BaseShowLoadingView {

        fun onGetCouponListSuccess(couponList: List<Coupon>)

        fun onGetCouponListCouponFailed(error: String)

        fun onRefreshCouponListSuccess(couponList: List<Coupon>)

        fun onGetNextCouponListSuccess(couponList: List<Coupon>)

    }


}