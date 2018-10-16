package com.sumian.common.h5

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import com.blankj.utilcode.util.LogUtils
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.R
import com.sumian.common.base.BaseActivity
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.common.h5.bean.H5ShowToastData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.utils.JsonUtil
import com.sumian.common.utils.ScreenUtil
import com.sumian.common.widget.TitleBar
import kotlinx.android.synthetic.main.common_activity_main_base_webview.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/10 10:53
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Suppress("ObjectLiteralToLambda", "MemberVisibilityCanBePrivate")
abstract class BaseWebViewActivity : BaseActivity(), SWebView.OnWebViewListener {

    protected var mSoftKeyBoardListener: SoftKeyBoardListener? = null
    protected var mSumianImageTextDialog: SumianImageTextDialog? = null

    override fun getLayoutId(): Int {
        return R.layout.common_activity_main_base_webview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitorKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSoftKeyBoardListener?.release()
    }

    override fun initWidget() {
        super.initWidget()
        title_bar!!.setOnBackClickListener { onBackPressed() }
    }

    override fun initData() {
        super.initData()
        val sWebView = sm_webview_container.sWebView
        sm_webview_container?.setWebListener(this)
        sm_webview_container?.loadRequestUrl(getCompleteUrl())
        registerHandler(sWebView)
        registerBaseHandler(sWebView)
    }

    private fun registerBaseHandler(sWebView: SWebView) {
        sWebView.registerHandler("showToast", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                LogUtils.d(data)
                val toastData = H5ShowToastData.fromJson(data)
                if (mSumianImageTextDialog != null) {
                    mSumianImageTextDialog!!.dismiss()
                } else {
                    mSumianImageTextDialog = SumianImageTextDialog(this@BaseWebViewActivity)
                }
                mSumianImageTextDialog!!.show(toastData)
            }
        })
        sWebView.registerHandler("hideToast", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                LogUtils.d(data)
                val toastData = H5ShowToastData.fromJson(data)
                if (mSumianImageTextDialog != null) {
                    mSumianImageTextDialog!!.dismiss(toastData.delay)
                }
            }
        })
        sWebView.registerHandler("finish", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                finish()
            }
        })
        sWebView.registerHandler("return", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                onBackPressed()
            }
        })
        sWebView.registerHandler("updatePageUI", object : BridgeHandler {
            override fun handler(data: String?, function: CallBackFunction) {
                val map = JsonUtil.fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {

                }.type) ?: return
                for ((key, value) in map) {
                    when (key) {
                        "showNavigationBar" -> if (value is Boolean) {
                            title_bar!!.visibility = if (value) View.VISIBLE else View.GONE
                        }
                        "showTitle" -> if (value is Boolean) {
                            title_bar!!.showTitle(value)
                        }
                        "showBackArrow" -> if (value is Boolean) {
                            title_bar!!.showBackArrow(value)
                        }
                        else -> {
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sm_webview_container!!.resumeWebView()
    }

    override fun onPause() {
        super.onPause()
        sm_webview_container!!.pauseWebView()
    }

    override fun onRelease() {
        super.onRelease()
        sm_webview_container!!.destroyWebView()
    }

    override fun onStop() {
        mSumianImageTextDialog?.release()
        super.onStop()
    }

    protected open fun h5HandlerName(): String? {
        return null
    }

    protected open fun initTitle(): String? {
        return null
    }

    protected open fun registerHandler(sWebView: SWebView) {}

    override fun onBackPressed() {
        if (!sm_webview_container!!.webViewCanGoBack()) {
            super.onBackPressed()
        }
    }

    protected open fun getCompleteUrl(): String {
        val stringBuilder = StringBuilder()
        val urlServerPart = getUrlServerPart()
        val urlContentPart = getUrlContentPart()
        val contentContainerParams = urlContentPart != null && urlContentPart.contains("?")
        stringBuilder.append(urlServerPart)
                .append(urlContentPart)
                .append(if (contentContainerParams) "&" else "?")
                .append(getUrlToken())
        val url = stringBuilder.toString()
        LogUtils.d("load url: %s", url)
        return url
    }

    protected fun getUrlServerPart(): String {
        return WebViewManger.getInstance().getBaseUrl() ?: ""
    }

    protected open fun getUrlContentPart(): String? {
        return null
    }

    private fun getUrlToken(): String {
        return "token=" + getToken()
    }

    protected fun getToken(): String? {
        return WebViewManger.getInstance().getToken()
    }


    protected open fun monitorKeyboard() {
        mSoftKeyBoardListener = SoftKeyBoardListener.registerListener(this, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                updateRootViewHeight(ScreenUtil.getScreenHeight(this@BaseWebViewActivity) - height)
            }

            override fun keyBoardHide(height: Int) {
                updateRootViewHeight(ScreenUtil.getScreenHeight(this@BaseWebViewActivity))
            }
        })
    }

    private fun updateRootViewHeight(height: Int) {
        val layoutParams = root_view!!.layoutParams
        layoutParams.height = height
        root_view!!.requestLayout()
    }

    protected fun reload() {
        sm_webview_container!!.reload()
    }

    protected fun getTitleBar(): TitleBar {
        return title_bar!!
    }

    protected fun getWebView(): SWebView {
        return sm_webview_container.sWebView
    }

    // ----------- OnWebViewListener start -----------
    protected fun canWebGoBack(): Boolean {
        return getWebView().canGoBack()
    }

    override fun onPageStarted(view: WebView?) {
    }

    override fun onProgressChange(view: WebView?, newProgress: Int) {
    }

    override fun onPageFinish(view: WebView?) {
    }

    override fun onRequestErrorCallback(view: WebView?, responseCode: Int) {
    }

    override fun onRequestNetworkErrorCallback(view: WebView?) {
    }

    override fun onReceiveTitle(webView: WebView, title: String) {
        title_bar?.setTitle(title)
    }
    // ----------- OnWebViewListener end -----------
}