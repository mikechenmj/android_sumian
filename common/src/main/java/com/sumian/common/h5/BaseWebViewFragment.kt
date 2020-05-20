package com.sumian.common.h5

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.LogUtils
import com.google.gson.reflect.TypeToken
import com.sumian.common.R
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.dialog.SumianImageTextDialog
import com.sumian.common.h5.bean.H5ShowToastData
import com.sumian.common.h5.bean.ShareData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.h5.widget.SWebViewLayout
import com.sumian.common.utils.JsonUtil
import com.sumian.common.utils.StatusBarUtil
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.base_h5_fragment.*
import kotlinx.android.synthetic.main.base_h5_fragment.sm_webview_container

abstract class BaseWebViewFragment : BaseViewModelFragment<BaseViewModel>(), SWebView.OnWebViewListener {

    protected var mSumianImageTextDialog: SumianImageTextDialog? = null
    protected var mShare: ImageView? = null
    protected var mTitleBar: LinearLayout? = null

    override fun onPageStarted(view: WebView?) {
    }

    override fun onProgressChange(view: WebView?, newProgress: Int) {
        if (newProgress <= 20) {
            iv_share?.visibility = View.INVISIBLE
        }
    }

    override fun onPageFinish(view: WebView?) {
    }

    override fun onRequestErrorCallback(view: WebView?, responseCode: Int) {
    }

    override fun onRequestNetworkErrorCallback(view: WebView?) {
    }

    override fun onReceiveTitle(view: WebView?, title: String?) {
        tv_title?.text = title
    }

    override fun getLayoutId(): Int {
        return R.layout.base_h5_fragment
    }

    open fun getSWebViewLayout(): SWebViewLayout? {
        return sm_webview_container
    }

    override fun initWidget() {
        super.initWidget()
        mShare = iv_share
        mTitleBar = fl_header
    }

    override fun initData() {
        super.initData()
        getSWebViewLayout()?.setWebListener(this)
        getSWebViewLayout()?.loadRequestUrl(getCompleteUrl())
        registerHandler(getSWebViewLayout()!!.sWebView)
        registerBaseHandler(getSWebViewLayout()!!.sWebView)
        iv_back?.setOnClickListener {
            if (sm_webview_container.sWebView.canGoBack()) {
                sm_webview_container.sWebView.goBack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sm_webview_container?.resumeWebView()
    }

    override fun onPause() {
        sm_webview_container?.pauseWebView()
        super.onPause()
    }

    protected open fun registerHandler(sWebView: SWebView) {}

    private fun registerBaseHandler(sWebView: SWebView) {
        sWebView.registerHandler("showToast") { data, function ->
            LogUtils.d(data)
            val toastData = H5ShowToastData.fromJson(data)
            if (mSumianImageTextDialog != null) {
                mSumianImageTextDialog!!.dismiss()
            } else {
                mSumianImageTextDialog = SumianImageTextDialog(activity!!)
            }
            mSumianImageTextDialog!!.show(toastData)
        }
        sWebView.registerHandler("hideToast") { data, function ->
            LogUtils.d(data)
            val toastData = H5ShowToastData.fromJson(data)
            if (mSumianImageTextDialog != null) {
                mSumianImageTextDialog!!.dismiss(toastData.delay)
            }
        }
        sWebView.registerHandler("return") { data, function -> goBack() }
        sWebView.registerHandler("updatePageUI") { data, function ->
            val map = JsonUtil.fromJson<Map<String, Any>>(data, object : TypeToken<Map<String, Any>>() {
            }.type) ?: return@registerHandler
            for ((key, value) in map) {
                when (key) {
                    "showNavigationBar" -> if (value is Boolean) {
                        fl_header?.isVisible = value
                    }
                    "showTitle" -> if (value is Boolean) {
                        tv_title?.isVisible = value
                    }
                    "showBackArrow" -> if (value is Boolean) {
                        iv_back?.visibility = if (value) View.VISIBLE else View.INVISIBLE
                    }
                    "setStatusBarTextColorDark" -> if (value is Boolean) {
                        if (activity == null) {
                            return@registerHandler
                        }
                        StatusBarUtil.setStatusBarTextColorDark(activity!!, value)
                    }
                    else -> {
                    }
                }
            }
        }
        sWebView.registerHandler("goToPage") { data, function ->
            run {
                val page = JsonUtil.getJsonObject(data)?.get("page")?.asString
                        ?: return@registerHandler
                onGoToPage(page, data)
            }
        }
    }

    protected open fun onGoToPage(page: String, rawData: String) {

    }

    protected open fun onShare(shareData: ShareData) {

    }

    protected open fun getCompleteUrl(): String {
        val stringBuilder = StringBuilder()
        val urlServerPart = getUrlServerPart()
        val urlContentPart = getUrlContentPart()
        val contentContainerParams = urlContentPart != null && urlContentPart.contains("?")
        stringBuilder.append(urlServerPart)
                .append(if (urlContentPart.isNullOrEmpty()) "" else urlContentPart)
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

    protected fun reload() {
        getSWebViewLayout()?.reload()
    }

    protected fun getWebView(): SWebView? {
        return getSWebViewLayout()?.sWebView
    }

    fun goBack(): Boolean {
        return getSWebViewLayout()?.webViewCanGoBack() ?: false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sm_webview_container?.destroyWebView()
        mSumianImageTextDialog?.release()

    }

}