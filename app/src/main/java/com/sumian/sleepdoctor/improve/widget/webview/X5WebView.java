package com.sumian.sleepdoctor.improve.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.app.AppManager;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sm
 * on 2018/5/24 17:08
 * desc:
 **/
@SuppressWarnings("deprecation")
public class X5WebView extends WebView {

    private static final String TAG = X5WebView.class.getSimpleName();

    private static final long DEFAULT_DELAY_MILLIS = 10 * 1000L;

    private OnX5WebViewListener mX5WebViewListener;

    private int mErrorCode = -1;

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (mX5WebViewListener != null) {
                mX5WebViewListener.onRequestNetworkErrorCallback(X5WebView.this);
            }
        }
    };

    public void setOnWebViewListener(OnX5WebViewListener listener) {
        this.mX5WebViewListener = listener;
    }

    public X5WebView(Context context) {
        this(context, null);
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }


    private void initView(Context context) {
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true);
        }

        initWebSettings(context);

        setWebChromeClient(new WVChromeClient());
        setWebViewClient(new WVClient());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings(Context context) {
        WebSettings webSettings = this.getSettings();

        //开启js脚本支持
        webSettings.setJavaScriptEnabled(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口

        webSettings.setAllowFileAccess(true);//设置可以访问文件

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //缩放操作
        webSettings.setSupportZoom(true);////支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //设置自适应屏幕，两者合用（下面这两个方法合用）
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小

        webSettings.setSupportMultipleWindows(true);

        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
        webSettings.setAppCachePath(getContext().getCacheDir().getAbsolutePath());

        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setPluginsEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // if (NetUtil.isHaveNet(getContext())) {
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //} else {
        //   webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //}
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

    private class WVClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //在当前Activity打开
            // view.loadUrl(url);很多网上都在这里加这一句,其实不用.因为我们默认用的就是  webView.loadUrl();
            //所以只要把该标志位设置为 true 就行.
            Log.e(TAG, "shouldOverrideUrlLoading: --------1---->" + url);
            return true;
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
        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            super.onReceivedError(webView, webResourceRequest, webResourceError);
            Log.e(TAG, "onReceivedError: -------2----->errorCode=" + webResourceError.getErrorCode() + "   error=" + webResourceError.getDescription());
            mErrorCode = webResourceError.getErrorCode();
            if (mX5WebViewListener != null) {
                mX5WebViewListener.onRequestErrorCallback(webView, mErrorCode);
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
        String encode = Uri.encode(url, "utf-8");

        Log.e(TAG, "loadRequestUrl: ---------->" + encode);

        Map<String, String> headers = new HashMap<>(0);
        headers.put("User-Agent", getSettings().getUserAgentString() + " Sumian-Doctor-iOS");
        // headers.put("Accept", "application/vnd.sumianapi+json");
        headers.put("Authorization", "Bearer " + AppManager.getAccountViewModel().accessToken());
        //  headers.put("Content-Type", "application/json");
        loadUrl(url, headers);
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
