@file:Suppress("ObjectLiteralToLambda")

package com.sumian.sd.buz.cbti.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.h5.bean.H5ShowToastData
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.h5.widget.SWebViewLayout
import com.sumian.common.utils.JsonUtil
import com.sumian.common.widget.TitleBar
import com.sumian.sd.BuildConfig
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.doctor.bean.H5DoctorServiceShoppingResult
import com.sumian.sd.buz.kefu.KefuManager
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.tencent.smtt.sdk.WebView

/**
 * CBTI  介绍购买页 webView
 */
class CBTIIntroductionWebView : SWebViewLayout {

    companion object {
        private const val REQUEST_CODE_BUY_SERVICE = 1000
    }

    private val mSumianImageTextDialog: SumianImageTextDialog  by lazy {
        SumianImageTextDialog(context)
    }
    private var mTitleBar: TitleBar? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initEvent()
    }

    private fun initEvent() {
        registerBuyServiceHandler(sWebView)
        registerBaseHandler(sWebView)
        sWebView.registerHandler("goToPage") { data, function ->
            run {
                val page = JsonUtil.getJsonObject(data)?.get("page")?.asString
                        ?: return@registerHandler
                onGoToPage(page, data)
            }
        }
    }

    private fun registerBuyServiceHandler(sWebView: SWebView) {
        sWebView.registerHandler("buyService", object : SBridgeHandler() {
            override fun handler(data: String?) {
                super.handler(data)
                val type = object : TypeToken<H5BaseResponse<H5DoctorServiceShoppingResult>>() {}
                val response = JsonUtil.fromJson<H5BaseResponse<H5DoctorServiceShoppingResult>>(data, type.type)
                response?.let {
                    PaymentActivity.startForResult(ActivityUtils.getTopActivity(), it.result?.service!!, it.result!!.packageId, REQUEST_CODE_BUY_SERVICE)
                }
            }
        })
    }

    private fun registerBaseHandler(sWebView: SWebView) {
        sWebView.registerHandler("showToast", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                LogUtils.d(data)
                val toastData = H5ShowToastData.fromJson(data)
                mSumianImageTextDialog.dismiss()
                mSumianImageTextDialog.show(toastData)
            }
        })
        sWebView.registerHandler("hideToast", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                LogUtils.d(data)
                val toastData = H5ShowToastData.fromJson(data)
                mSumianImageTextDialog.dismiss(toastData.delay)
            }
        })
        sWebView.registerHandler("finish", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                ActivityUtils.getTopActivity().finish()
            }
        })
        sWebView.registerHandler("return", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                ActivityUtils.getTopActivity().onBackPressed()
            }
        })
        sWebView.registerHandler("updatePageUI", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                val map = JsonUtil.fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {

                }.type) ?: return
                for ((key, value) in map) {
                    when (key) {
                        "showNavigationBar" -> if (value is Boolean) {
                            mTitleBar?.visibility = if (value) View.VISIBLE else View.GONE
                        }
                        "showTitle" -> if (value is Boolean) {
                            mTitleBar?.showTitle(value)
                        }
                        "showBackArrow" -> if (value is Boolean) {
                            mTitleBar?.showBackArrow(value)
                        }
                        else -> {
                        }
                    }
                }
            }
        })
    }

    fun onGoToPage(page: String, rawData: String) {
        when (page) {
            "onlineConsult" -> {
                KefuManager.launchKefuActivity()
//                StatUtil.event(StatConstants.click_sleep_guide_page_sleep_steward)
            }
        }
    }

    fun setTitleBar(titleBar: TitleBar) {
        this.mTitleBar = titleBar
    }

    override fun onReceiveTitle(view: WebView?, title: String?) {
        super.onReceiveTitle(view, title)
        mTitleBar?.setTitle(title)
    }

    fun requestCBTIIntroductionUrl() {
        val urlContent = H5Uri.NATIVE_ROUTE.replace("{pageData}", H5PayloadData(H5Uri.CBTI, mapOf()).toJson())
                .replace("{token}", AppManager.getAccountViewModel().token.token)
        val requestUrl = BuildConfig.BASE_H5_URL + urlContent
        loadRequestUrl(requestUrl)
    }
}