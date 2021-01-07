package com.sumian.sd.examine.main.me

import android.util.Log
import android.view.View
import android.webkit.*
import androidx.core.view.isVisible
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.buz.account.bean.Answers
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.model.AccountManager
import kotlinx.android.synthetic.main.examine_question.*

class ExamineQuestionActivity : BaseActivity() {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineQuestionActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_question
    }

    override fun initWidget() {
        super.initWidget()
        bt_finish.setOnClickListener { finish() }
        val haveAnswer = AccountManager.userInfo?.isHaveAnswers ?: false
        if (!haveAnswer) {
            initWebView()
        } else {
            finish_container.visibility = View.VISIBLE
            web.visibility = View.GONE
        }
        iv_back.setOnClickListener { finish() }
        iv_re_do.setOnClickListener { initWebView() }
    }

    private fun initWebView() {
        finish_container.visibility = View.GONE
        //声明WebSettings子类
        val webSettings: WebSettings = web.settings

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        // 设置允许JS弹窗
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        //设置自适应屏幕，两者合用（下面这两个方法合用）
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.setSupportMultipleWindows(true)
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        //Android 5.0上Webview默认不允许加载Http与Https混合内容,所以需要兼容混合模式(比如 src 中图片 url 为 http)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        web.loadUrl("http://sumian-question-h5-production.oss-cn-shenzhen.aliyuncs.com/index.html?token="
                + AccountManager.token?.token)
        web.setWebChromeClient(WebChromeClient())
        web.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                //sumian://sleep_quality_result?data={"id":35,"answers":"06:04,1,00:08,05:08,2,2,2","score":9,"level":1,"created_at":1521047312}
                val url = request.url.toString()
                if (url.startsWith("sumian://sleep_quality_result?data=")) {
                    val json = url.substring(url.indexOf("{"))
                    val answer: Answers = JSON.parseObject(json, Answers::class.java)
                    val userInfo: UserInfo? = AccountManager.userInfo
                    userInfo?.setAnswers(answer)
                    finish()
                }
                return super.shouldInterceptRequest(view, request)
            }
        })
        web.visibility = View.VISIBLE
    }
}