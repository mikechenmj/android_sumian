package com.sumian.sd.service.coupon.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.service.coupon.bean.Coupon

interface CouponListContract {

    interface View : BaseShowLoadingView {

        fun onGetCouponListSuccess(couponList: List<Coupon>)

        fun onGetCouponListCouponFailed(error: String)

        fun onRefreshCouponListSuccess(couponList: List<Coupon>)

        fun onGetNextCouponListSuccess(couponList: List<Coupon>)

    }


    interface Presenter : IPresenter {

        fun getCouponList()

        fun refreshCouponList()

        fun getNextCouponList()
    }
}