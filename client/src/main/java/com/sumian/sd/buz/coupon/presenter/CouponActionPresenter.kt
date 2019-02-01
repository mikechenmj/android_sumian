package com.sumian.sd.buz.coupon.presenter

import android.text.TextUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.coupon.contract.CouponActionContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

class CouponActionPresenter private constructor(view: CouponActionContract.View) : BaseViewModel() {

    private var mView: CouponActionContract.View? = null

    init {
        this.mView = view
    }

    companion object {
        @JvmStatic
        fun init(view: CouponActionContract.View): CouponActionPresenter {
            return CouponActionPresenter(view)
        }
    }

     fun checkCoupon(coupon: String) {
        if (TextUtils.isEmpty(coupon)) {
            mView?.onInputCouponFailed("请输入您的兑换码")
            return
        }
        couponCode(coupon)
    }

    private fun couponCode(coupon: String) {
        mView?.showLoading()

        val call = AppManager.getSdHttpService().couponAction(coupon)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                mView?.onInputCouponSuccess()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onInputCouponFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })
    }
}