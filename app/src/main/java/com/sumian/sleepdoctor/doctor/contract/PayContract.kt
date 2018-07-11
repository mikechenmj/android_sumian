package com.sumian.sleepdoctor.doctor.contract

import android.app.Activity
import android.content.Intent

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView
import com.sumian.sleepdoctor.doctor.bean.PayOrder

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

interface PayContract {


    interface View : BaseView<Presenter> {

        fun onCreatePayOrderSuccess()

        fun onOrderPaySuccess(payMsg: String)

        fun onOrderPayFailed(payMsg: String)

        fun onOrderPayInvalid(payMsg: String)

        fun onOrderPayCancel(payMsg: String)

        fun onCheckOrderPayIsOk()

        fun onCheckOrderPayIsInvalid(invalidError: String)

    }

    interface Presenter : BasePresenter<Any> {

        fun createPayOrder(activity: Activity, payOrder: PayOrder)

        fun checkPayOrder()

        fun doPay(activity: Activity)

        fun clearPayAction()

        fun onPayActivityResultDelegate(requestCode: Int, resultCode: Int, data: Intent)
    }
}
