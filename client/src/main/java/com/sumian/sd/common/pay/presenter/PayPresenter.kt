package com.sumian.sd.common.pay.presenter

import android.app.Activity
import android.content.Intent
import android.os.Handler
import androidx.annotation.StringRes
import com.google.gson.Gson
import com.pingplusplus.android.Pingpp
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.sumian.sd.common.pay.bean.OrderDetail
import com.sumian.sd.common.pay.bean.PayCouponCode
import com.sumian.sd.common.pay.bean.PayOrder
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

class PayPresenter private constructor(view: PaymentActivity) : BaseViewModel() {

    private var mView: PaymentActivity? = null

    private var mOrder: String? = null

    private var mOrderNo: String? = null

    private var mCheckOrderCount = 0

    private val mHandler: Handler by lazy {
        Handler()
    }

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {
        @JvmStatic
        fun init(view: PaymentActivity) {
            PayPresenter(view)
        }
    }

    fun createPayOrder(activity: Activity, payOrder: PayOrder) {
        mView?.onBegin()
        val call: Call<Any> = AppManager.getSdHttpService().createOrder(payOrder)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>(), Callback<Any> {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onFailure(errorResponse.message)
            }

            override fun onSuccess(response: Any?) {
                val toJson = Gson().toJson(response)
                mOrder = toJson
                try {
                    mOrderNo = JSONObject(toJson).get("order_no") as String?
                    mView?.onCreatePayOrderSuccess()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })

    }

    fun checkPayOrder() {
        mCheckOrderCount++
        mView?.onBegin()
        val call = AppManager.getSdHttpService().getOrderDetail(mOrderNo!!)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<OrderDetail>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                autoCheckOrderStatus()
            }

            override fun onSuccess(response: OrderDetail?) {
                response?.let {
                    when (response.status) {//0：待支付，1：支付成功，2：支付失败 3：退款中 4：退款成功 5：退款失败
                        0 -> {//待支付
                            autoCheckOrderStatus()
                        }
                        1 -> {//支付成功
                            mCheckOrderCount = 0
                            mView?.onCheckOrderPayIsOk()
                        }
                        2 -> {
                            mCheckOrderCount = 0
                            mView?.onCheckOrderPayIsInvalid("支付失败")
                        }
                        3 -> {
                            mCheckOrderCount = 0
                            mView?.onCheckOrderPayIsInvalid("退款中")
                        }
                        4 -> {
                            mCheckOrderCount = 0
                            mView?.onCheckOrderPayIsInvalid("退款成功")
                        }
                        5 -> {
                            mCheckOrderCount = 0
                            mView?.onCheckOrderPayIsInvalid("退款失败")
                        }
                        else -> {//2及其他都是支付失败

                        }
                    }
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }

    fun checkCouponCode(is2Pay: Boolean, couponCode: String, packageId: Int) {
        mView?.onBegin()
        val call = AppManager.getSdHttpService().checkCouponCode(couponCode, packageId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PayCouponCode>() {

            override fun onSuccess(response: PayCouponCode?) {
                mView?.onCheckCouponCodeSuccess(response, couponCode, is2Pay)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onCheckCouponCodeFailed(error = errorResponse.message, code = errorResponse.code, payCouponCodeText = null, is2Pay = is2Pay)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }

    fun autoCheckOrderStatus() {
        if (mCheckOrderCount >= 10) {
            mCheckOrderCount = 0
            mView?.onCheckOrderPayFinialIsInvalid("该订单支付失败,请重新购买")
        } else {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({ checkPayOrder() }, 1000L)
        }
    }

    fun doPay(activity: Activity) {
        Pingpp.DEBUG = BuildConfig.DEBUG
        Pingpp.enableDebugLog(BuildConfig.DEBUG)
        Pingpp.createPayment(activity, mOrder)
    }

    fun clearPayAction() {
        this.mOrder = null
        this.mOrderNo = null
    }

    fun onPayActivityResultDelegate(requestCode: Int, resultCode: Int, data: Intent?) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            val result = data?.extras!!.getString("pay_result")
            @StringRes val payResultMsg: Int

            when (result) {
                "success" -> {
                    payResultMsg = R.string.pay_success
                    mView?.onOrderPaySuccess(App.getAppContext().getString(payResultMsg))
                }
                "fail" -> {
                    payResultMsg = R.string.pay_failed

                    val errorMsg = data.extras!!.getString("error_msg") // 错误信息
                    // String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息

                    mView?.onOrderPayFailed(App.getAppContext().getString(payResultMsg) + "," + errorMsg)
                }
                "cancel" -> {
                    payResultMsg = R.string.pay_cancel
                    mView?.onOrderPayCancel(App.getAppContext().getString(payResultMsg))
                    clearPayAction()
                }
                "invalid" -> {
                    payResultMsg = R.string.pay_invalid
                    mView?.onOrderPayInvalid(App.getAppContext().getString(payResultMsg))
                }
                "unknown" -> {
                    payResultMsg = R.string.pay_unknown
                    mView?.onOrderPayFailed(App.getAppContext().getString(payResultMsg))
                }
                else -> {
                    payResultMsg = R.string.pay_unknown
                    mView?.onOrderPayFailed(App.getAppContext().getString(payResultMsg))
                }
            }

            /* 处理返回值
             * "success" - 支付成功
             * "fail"    - 支付失败
             * "cancel"  - 取消支付
             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
             * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
             */

            // PlayLog.e(TAG, "onActivityResult: -------------->result=" + result + " package name=" + HwApp.Companion.getAppContext().getPackageName());

        }
    }
}
