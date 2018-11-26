package com.sumian.sd.pay.contract

import android.app.Activity
import android.content.Intent

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView
import com.sumian.sd.pay.bean.PayCouponCode
import com.sumian.sd.pay.bean.PayOrder

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

interface PayContract {


    interface View : SdBaseView<Presenter> {

        fun onCreatePayOrderSuccess()

        fun onOrderPaySuccess(payMsg: String)

        fun onOrderPayFailed(payMsg: String)

        fun onOrderPayInvalid(payMsg: String)

        fun onOrderPayCancel(payMsg: String)

        fun onCheckOrderPayIsOk()

        fun onCheckOrderPayIsInvalid(invalidError: String)

        fun onCheckCouponCodeSuccess(payCouponCode: PayCouponCode?, payCouponCodeText: String, is2Pay: Boolean)

        fun onCheckCouponCodeFailed(error: String)

        /**
         * 原因：ping++ 的服务器回调可能会比 客户端回调 晚，此时服务端还未生成电话预约，所以会返回上一次的数据
         *
         * 查询该订单10次都没检测到订单支付成功,则直接跳转到医生详情页
         */
        fun onCheckOrderPayFinialIsInvalid(invalidError: String)

    }

    interface Presenter : SdBasePresenter<Any> {

        fun createPayOrder(activity: Activity, payOrder: PayOrder)

        fun checkPayOrder()

        fun checkCouponCode(is2Pay: Boolean = false, couponCode: String, packageId: Int)

        fun doPay(activity: Activity)

        fun clearPayAction()

        fun onPayActivityResultDelegate(requestCode: Int, resultCode: Int, data: Intent)
    }
}
