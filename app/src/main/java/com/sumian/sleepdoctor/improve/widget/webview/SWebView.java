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

    private OnX5WebViewListener mX5WebViewListener;

    private int mErrorCode = -1;

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (mX5WebViewListener != null) {
                mX5WebViewListener.onRequestNetworkErrorCallback(SWebView.this);
            }
        }
    };

    public void setOnWebViewListener(OnX5WebViewListener listener) {
        this.mX5WebViewListener = listener;
    }

    public SWebView(Context context) {
        this(context, null);
    }

    public SWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true);
        }

        initWebSettings(context);

        setDefaultHandler(new DefaultHandler());
        setWebChromeClient(new WVChromeClient());
        setWebViewClient(new WVClient(this));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings(Context context) {
        WebSettings webSettings = this.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    //进度显示
    private class WVChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.e(TAG, "onProgressChanged: ---------->progress=" + newProgress);
            if (newProgress == 100 && mErrorCode != -1) {
                if (mX5WebViewListener != null) {
                    mX5WebViewListener.onRequestErrorCallback(view, mErrorCode);
                }
            } else {
                if (mX5WebViewListener != null) {
                    mX5WebViewListener.onProgressChange(view, newProgress);
                }
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
            if (mX5WebViewListener != null) {
                mX5WebViewListener.onRequestNetworkErrorCallback(webView);
            }
        }

        @Override
        public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
            super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
            Log.e(TAG, "onReceivedHttpError: ---------3------->");
            mErrorCode = webResourceResponse.getStatusCode();
            if (mX5WebViewListener != null) {
                mX5WebViewListener.onRequestErrorCallback(webView, webResourceResponse.getStatusCode());
            }
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
            if (mX5WebViewListener != null) {
                mX5WebViewListener.onPageStarted(webView);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            removeCallbacks(mDismissRunnable);
            Log.e(TAG, "onPageFinished: ---------->" + url);
            if (mErrorCode != -1) {
                if (mX5WebViewListener != null) {
                    mX5WebViewListener.onRequestErrorCallback(view, mErrorCode);
                }
            } else {
                if (mX5WebViewListener != null) {
                    mX5WebViewListener.onPageFinish(view);
                }
            }
        }
    }

    public void loadRequestUrl(String url) {
        this.mErrorCode = -1;
        postDelayed(mDismissRunnable, DEFAULT_DELAY_MILLIS);
        getSettings().setUserAgentString(" Sumian-Doctor-Android");
        loadUrl(url);
    }

    //进度回调接口
    public interface OnX5WebViewListener {

        void onPageStarted(WebView view);

        void onProgressChange(WebView view, int newProgress);

        void onPageFinish(WebView view);

        void onRequestErrorCallback(WebView view, int responseCode);

        void onRequestNetworkErrorCallback(WebView view);
    }
}
