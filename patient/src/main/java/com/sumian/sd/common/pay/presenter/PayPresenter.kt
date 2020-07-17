package com.sumian.sd.common.pay.presenter

import android.app.Activity
import android.os.Handler
import android.util.Log
import com.google.gson.Gson
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
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

class PayPresenter private constructor(view: PaymentActivity, api: IWXAPI) : BaseViewModel() {

    private var mView: PaymentActivity? = null

    var WXApi: IWXAPI? = null

    private var mPayment: JSONObject? = null

    private var mOrderNo: String? = null

    private var mCheckOrderCount = 0

    private val mHandler: Handler by lazy {
        Handler()
    }

    init {
        view.setPresenter(this)
        mView = view
        WXApi = api
    }

    companion object {
        @JvmStatic
        fun init(view: PaymentActivity): PayPresenter {
            val msgApi = WXAPIFactory.createWXAPI(view, null)
            msgApi.registerApp(BuildConfig.WECHAT_APP_ID)
            return PayPresenter(view, msgApi)
        }
    }

    fun createPayOrder(activity: Activity, payOrder: PayOrder) {
        mView?.showLoading()
        val call: Call<Any> = AppManager.getSdHttpService().createOrder(payOrder)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>(), Callback<Any> {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onFailure(errorResponse.message)
            }

            override fun onSuccess(response: Any?) {
                val toJson = Gson().toJson(response)
                try {
                    val payment = JSONObject(toJson)
                    val order = payment.getJSONObject("order")
                    mOrderNo = order.getString("order_no")
                    mPayment = payment
                    mView?.onCreatePayOrderSuccess()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })

    }

    fun checkPayOrder() {
        mCheckOrderCount++
        mView?.showLoading()
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
                mView?.dismissLoading()
            }
        })
    }

    fun checkCouponCode(couponCode: String, packageId: Int) {
        val call = AppManager.getSdHttpService().checkCouponCode(couponCode, packageId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PayCouponCode>() {

            override fun onSuccess(response: PayCouponCode?) {
                mView?.onCheckCouponCodeSuccess(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onCheckCouponCodeFailed(error = errorResponse.message, code = errorResponse.code)
            }
        })
    }

    fun autoCheckOrderStatus() {
        if (mCheckOrderCount >= 10) {
            mCheckOrderCount = 0
            mView?.onCheckOrderPayFinialIsInvalid("该订单支付失败,请重新购买", "{\"result\":\"order_error\"}")
        } else {
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({ checkPayOrder() }, 1000L)
        }
    }

    fun doPay(activity: Activity) {
        val request = PayReq()
        request.appId = mPayment!!.getString("app_id")
        request.partnerId = mPayment!!.getString("partner_id")
        request.prepayId = mPayment!!.getString("prepay_id")
        request.packageValue = mPayment!!.getString("package")
        request.timeStamp = mPayment!!.getString("timestamp")
        request.sign = mPayment!!.getString("sign")
        request.nonceStr = mPayment!!.getString("nonce_str")
        WXApi!!.sendReq(request)
    }

    fun clearPayAction() {
        this.mPayment = null
        this.mOrderNo = null
    }

    fun onWXPayResult(resp: BaseResp?) {
        if (resp?.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            when (resp?.errCode) {
                0 -> {
                    mView?.onOrderPaySuccess(App.getAppContext().getString(R.string.pay_success))
                }
                -1 -> {
                    mView?.onOrderPayFailed("{\"result\":\"order_error\"}")
                }
                -2 -> {
                    mView?.onOrderPayCancel("{\"result\":\"cancel\"}")
                    clearPayAction()
                }
                else -> {
                    mView?.onOrderPayFailed("{\"result\":\"unknown_error\"}")
                }
            }
        }
    }
}
