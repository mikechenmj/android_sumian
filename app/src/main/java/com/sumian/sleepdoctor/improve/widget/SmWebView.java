package com.sumian.sleepdoctor.improve.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import static com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.NARROW_COLUMNS;

public class SmWebView extends WebView {

    private static final String TAG = SmWebView.class.getSimpleName();

    private static final int MAX_PROGRESS = 100;

    private WebViewClient mWebViewClient;

    private WebChromeClient mWebChromeClient;

    private QMUIProgressBar mQMUIProgressBar;


    public SmWebView(Context context) {
        this(context, null);
    }

    public SmWebView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SmWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setClickable(false);
        setFocusable(false);
        setHorizontalScrollBarEnabled(false);

        mQMUIProgressBar = new QMUIProgressBar(context);
        mQMUIProgressBar.setMaxValue(100);
        addView(mQMUIProgressBar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 20));

    }


    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    public void initClient() {
        setWebViewClient(mWebViewClient = new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e(TAG, "onPageStarted: --------->" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e(TAG, "onPageFinished: ----------->" + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "onReceivedError: ---------->errorCode=" + error.getErrorCode() + "  request=" + request.getUrl() + "  error=" + error.getDescription());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.e(TAG, "onReceivedHttpError: ---------->errorCode=" + errorResponse.getStatusCode());
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
                Log.e(TAG, "onReceivedSslError: ------------>" + sslError.toString());
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                Log.e(TAG, "onReceivedError: ---------------->");
            }

            @Override
            public void onLoadResource(WebView webView, String s) {
                super.onLoadResource(webView, s);
                Log.e(TAG, "onLoadResource: ------------>" + s);
            }
        });

    }

    public void initChromeClient() {
        setWebChromeClient(mWebChromeClient = new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsAlert(webView, s, s1, jsResult);
            }

            @Override
            public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsConfirm(webView, s, s1, jsResult);
            }

            @Override
            public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
                return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
            }

            @Override
            public boolean onJsBeforeUnload(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsBeforeUnload(webView, s, s1, jsResult);
            }

            @Override
            public boolean onJsTimeout() {
                return super.onJsTimeout();
            }

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                super.onProgressChanged(webView, progress);
                mQMUIProgressBar.setProgress(progress);
                Log.e(TAG, "onProgressChanged: --------->" + progress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e(TAG, "onConsoleMessage: ----------->" + consoleMessage.message());
                return true;
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
                Log.e(TAG, "onReceivedTitle: --------->title=" + s);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebViewSettings() {
        WebSettings webSettings = this.getSettings();
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }


        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(NARROW_COLUMNS);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // webSettings.setDefaultFontSize(14);
        webSettings.setDefaultTextEncodingName("utf-8");
        //禁用放缩
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);

        //10M缓存，api 18后，系统自动管理。
        webSettings.setAppCacheMaxSize(10 * 1024 * 1024);
        //允许缓存，设置缓存位置
        webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        webSettings.setDatabaseEnabled(true);

        //允许WebView使用File协议
        webSettings.setAllowFileAccess(true);

        //不保存密码
        webSettings.setSavePassword(false);

        //设置UA
        Log.e(TAG, "initWebViewSettings: ------->" + webSettings.getUserAgentString());
        webSettings.setUserAgentString(WebSettings.getDefaultUserAgent(getContext()) + " Sumian-Doctor-Android");
        Log.e(TAG, "initWebViewSettings: --------->" + webSettings.getUserAgentString());
        //移除部分系统JavaScript接口
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);

        //webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void destroy() {
        setWebViewClient(null);
        setWebChromeClient(null);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);

        removeAllViewsInLayout();

        removeAllViews();
        clearCache(true);
        super.destroy();
    }

}
