package com.sumian.common.h5

import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.google.gson.reflect.TypeToken
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

abstract class BaseWebViewFragment : BaseViewModelFragment<BaseViewModel>() ,SWebView.OnWebViewListener{

    protected var mSumianImageTextDialog: SumianImageTextDialog? = null

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
        registerHandler(getSWebViewLayout().sWebView)
        registerBaseHandler(getSWebViewLayout().sWebView)
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
        sWebView.registerHandler("goToPage") { data, function ->
            run {
                val page = JsonUtil.getJsonObject(data)?.get("page")?.asString
                        ?: return@registerHandler
                onGoToPage(page, data)
            }
        }
        sWebView.registerHandler("share") { data, function ->
            run {
                val shareData = JsonUtil.fromJson(data, ShareData::class.java) ?: return@run
                onShare(shareData)
            }
        }
    }

    protected open fun onGoToPage(page: String) {

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

    override fun onDestroyView() {
        super.onDestroyView()
        mSumianImageTextDialog?.release()
    }

}