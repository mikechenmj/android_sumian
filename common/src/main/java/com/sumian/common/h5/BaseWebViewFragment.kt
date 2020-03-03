package com.sumian.common.h5

import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.h5.widget.SWebViewLayout
import com.tencent.smtt.sdk.WebView

abstract class BaseWebViewFragment : BaseViewModelFragment<BaseViewModel>() ,SWebView.OnWebViewListener{

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

    override fun onReceiveTitle(view: WebView?, title: String?) {
    }


    abstract fun getSWebViewLayout(): SWebViewLayout

    override fun initData() {
        super.initData()
        getSWebViewLayout().setWebListener(this)
        getSWebViewLayout().loadRequestUrl(getCompleteUrl())
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

    protected fun reload() {
        getSWebViewLayout().reload()
    }

    protected fun getWebView(): SWebView {
        return getSWebViewLayout().sWebView
    }

    fun goBack(): Boolean {
        return getSWebViewLayout().webViewCanGoBack()
    }

}