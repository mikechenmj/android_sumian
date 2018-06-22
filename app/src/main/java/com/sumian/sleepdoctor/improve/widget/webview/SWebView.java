package com.sumian.sleepdoctor.improve.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.sumian.sleepdoctor.BuildConfig;


/**
 * Created by sm
 * on 2018/5/24 17:08
 * desc:
 **/
public class SWebView extends BridgeWebView {

    private static final String TAG = SWebView.class.getSimpleName();
    private static final long DEFAULT_DELAY_MILLIS = 30 * 1000L;

    private OnWebViewListener mWebViewListener;
    private int mErrorCode = -1;

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWebViewListener != null) {
                mWebViewListener.onRequestNetworkErrorCallback(SWebView.this);
            }
        }
    };


    public void setOnWebViewListener(OnWebViewListener listener) {
        this.mWebViewListener = listener;
    }

    public SWebView(Context context) {
        this(context, null);
    }

    public SWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true);
        }

        initWebSettings();

        setDefaultHandler(new DefaultHandler());
        setWebChromeClient(new WVChromeClient());
        setWebViewClient(new WVClient(this));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings() {
        WebSettings webSettings = this.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    public void loadRequestUrl(String url) {
        this.mErrorCode = -1;
        postDelayed(mDismissRunnable, DEFAULT_DELAY_MILLIS);
        getSettings().setUserAgentString(" Sumian-Doctor-Android");
        loadUrl(url);
    }

    //进度回调接口
    public interface OnWebViewListener {

        void onPageStarted(WebView view);

        void onProgressChange(WebView view, int newProgress);

        void onPageFinish(WebView view);

        void onRequestErrorCallback(WebView view, int responseCode);

        void onRequestNetworkErrorCallback(WebView view);

        void onReceiveTitle(WebView view, String title);
    }

    //进度显示
    private class WVChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.d(TAG, "onProgressChanged: ---------->progress=" + newProgress);
            if (newProgress == 100 && mErrorCode != -1) {
                if (mWebViewListener != null) {
                    mWebViewListener.onRequestErrorCallback(view, mErrorCode);
                }
            } else {
                if (mWebViewListener != null) {
                    mWebViewListener.onProgressChange(view, newProgress);
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            LogUtils.d(title);
            if (title.startsWith("http")) { // 中间出现的无效title，过滤掉
                return;
            }
            if (mWebViewListener != null) {
                mWebViewListener.onReceiveTitle(view, title);
            }
        }
    }

    private class WVClient extends BridgeWebViewClient {

        WVClient(BridgeWebView webView) {
            super(webView);
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            Log.e(TAG, "onReceivedError: ------1--->");
            if (mWebViewListener != null) {
                mWebViewListener.onRequestNetworkErrorCallback(webView);
            }
        }

        @Override
        public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
            super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
            Log.e(TAG, "onReceivedHttpError: ---------3------->");
            mErrorCode = webResourceResponse.getStatusCode();
//            if (mWebViewListener != null) {
//                mWebViewListener.onRequestErrorCallback(webView, webResourceResponse.getStatusCode());
//            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //https忽略证书问题
            Log.e(TAG, "onReceivedSslError: ---------->");
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            Log.e(TAG, "onPageStarted: ----------->" + s);
            if (mWebViewListener != null) {
                mWebViewListener.onPageStarted(webView);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            removeCallbacks(mDismissRunnable);
            Log.e(TAG, "onPageFinished: ---------->" + url);
            if (mErrorCode != -1) {
                if (mWebViewListener != null) {
                    mWebViewListener.onRequestErrorCallback(view, mErrorCode);
                }
            } else {
                if (mWebViewListener != null) {
                    mWebViewListener.onPageFinish(view);
                }
            }
        }
    }
}
