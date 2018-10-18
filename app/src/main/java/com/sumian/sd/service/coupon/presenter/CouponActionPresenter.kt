package com.sumian.sd.service.coupon.presenter

import android.text.TextUtils
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.coupon.contract.CouponActionContract

class CouponActionPresenter private constructor(view: CouponActionContract.View) : CouponActionContract.Presenter {

    private var mView: CouponActionContract.View? = null

    init {
        this.mView = view
    }

    companion object {
        @JvmStatic
        fun init(view: CouponActionContract.View): CouponActionContract.Presenter {
            return CouponActionPresenter(view)
        }
    }

    override fun checkCoupon(coupon: String) {
        if (TextUtils.isEmpty(coupon)) {
            mView?.onInputCouponFailed("请输入您的兑换码")
            return
        }
        couponCode(coupon)
    }

    private fun couponCode(coupon: String) {
        mView?.showLoading()

        val call = AppManager.getSdHttpService().couponAction(coupon)
        mCalls.add(call)
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