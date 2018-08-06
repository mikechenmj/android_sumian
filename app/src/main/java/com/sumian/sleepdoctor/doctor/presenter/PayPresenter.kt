package com.sumian.sleepdoctor.doctor.presenter

import android.app.Activity
import android.content.Intent
import android.support.annotation.StringRes
import com.google.gson.Gson
import com.pingplusplus.android.Pingpp
import com.sumian.sleepdoctor.BuildConfig
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BasePresenter.mCalls
import com.sumian.sleepdoctor.doctor.bean.PayOrder
import com.sumian.sleepdoctor.doctor.contract.PayContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse
import com.sumian.sleepdoctor.order.OrderDetail
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

class PayPresenter private constructor(view: PayContract.View) : PayContract.Presenter {

    private val TAG: String = PayPresenter::class.java.simpleName

    private var mView: PayContract.View? = null

    private var mOrder: String? = null

    private var mOrderNo: String? = null

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {


        fun init(view: PayContract.View) {
            PayPresenter(view)
        }
    }

    override fun createPayOrder(activity: Activity, payOrder: PayOrder) {

        mView?.onBegin()

        val call: Call<Any> = AppManager.getHttpService().createOrder(payOrder)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<Any>(), Callback<Any> {

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

            override fun onFailure(code: Int, message: String) {
                mView?.onFailure(message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })

    }

    override fun checkPayOrder() {

        mView?.onBegin()

        val call = AppManager.getHttpService().getOrderDetail(mOrderNo!!)
        addCall(call)
        call.enqueue(object : BaseResponseCallback<OrderDetail>() {
            override fun onSuccess(response: OrderDetail?) {
                mView?.onCheckOrderPayIsOk()
            }

            override fun onFailure(code: Int, message: String) {
                mView?.onCheckOrderPayIsInvalid(message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }

    override fun doPay(activity: Activity) {
        Pingpp.DEBUG = BuildConfig.DEBUG
        Pingpp.enableDebugLog(BuildConfig.DEBUG)
        Pingpp.createPayment(activity, mOrder)
    }

    override fun clearPayAction() {
        this.mOrder = null
        this.mOrderNo = null
    }

    override fun onPayActivityResultDelegate(requestCode: Int, resultCode: Int, data: Intent) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {

            val result = data.extras!!.getString("pay_result")

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
