package com.sumian.common.h5.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.sumian.common.BuildConfig;
import com.sumian.common.h5.WebViewManger;
import com.sumian.common.h5.factory.WebViewTlsSniSocketFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


/**
 * Created by sm
 * on 2018/5/24 17:08
 * desc:
 **/
@SuppressWarnings("ALL")
public class SWebView extends BridgeWebView {

    private static final String TAG = SWebView.class.getSimpleName();
    private static final long DEFAULT_DELAY_MILLIS = 30 * 1000L;

    private OnWebViewListener mWebViewListener;
    private OnRequestFileCallback mOnRequestFileCallback;
    private int mErrorCode = -1;

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWebViewListener != null) {
                mWebViewListener.onRequestNetworkErrorCallback(SWebView.this);
            }
        }
    };

    public SWebView(Context context) {
        this(context, null);
    }

    public SWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setOnWebViewListener(OnWebViewListener listener) {
        this.mWebViewListener = listener;
    }

    public void setOnRequestFileCallback(OnRequestFileCallback onRequestFileCallback) {
        mOnRequestFileCallback = onRequestFileCallback;
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
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    public void loadRequestUrl(String url) {
        this.mErrorCode = -1;
        postDelayed(mDismissRunnable, DEFAULT_DELAY_MILLIS);
        getSettings().setUserAgentString(" Sumian-Doctor-Android");
        postOnAnimation(() -> loadUrl(url));
    }

    @Override
    public void destroy() {
        setWebViewClient(null);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);
        removeAllViews();
        super.destroy();
    }

    public interface OnRequestFileCallback {

        boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
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
            if (isDebug()) {
                Log.d(TAG, "onProgressChanged: ---------->progress=" + newProgress);
            }
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

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (isDebug()) {
                Log.e(TAG, "onShowFileChooser: ---------->");
            }
            return mOnRequestFileCallback != null && mOnRequestFileCallback.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    }

    private class WVClient extends BridgeWebViewClient {

        WVClient(BridgeWebView webView) {
            super(webView);
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String scheme = request.getUrl().getScheme();
            String method = request.getMethod();
            Map<String, String> headerFields = request.getRequestHeaders();
            String url = request.getUrl().toString();
            if (isDebug()) {
                Log.e(TAG, "url:" + url);
            }
            // 无法拦截body，拦截方案只能正常处理不带body的请求；
            if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) && method.equalsIgnoreCase("get")) {
                try {
                    URLConnection connection = recursiveRequest(url, headerFields, null);

                    if (connection == null) {
                        if (isDebug()) {
                            Log.e(TAG, "connection null");
                        }
                        return super.shouldInterceptRequest(view, request);
                    }

                    // 注*：对于POST请求的Body数据，WebResourceRequest接口中并没有提供，这里无法处理
                    String contentType = connection.getContentType();
                    String mime = getMime(contentType);
                    String charset = getCharset(contentType);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                    int statusCode = httpURLConnection.getResponseCode();
                    String response = httpURLConnection.getResponseMessage();
                    Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                    Set<String> headerKeySet = headers.keySet();
                    if (isDebug()) {
                        Log.e(TAG, "code:" + httpURLConnection.getResponseCode());
                        Log.e(TAG, "mime:" + mime + "; charset:" + charset);
                    }

                    // 无mime类型的请求不拦截
                    if (TextUtils.isEmpty(mime)) {
                        if (isDebug()) {
                            Log.e(TAG, "no MIME");
                        }
                        return super.shouldInterceptRequest(view, request);
                    } else {
                        // 二进制资源无需编码信息
                        if (!TextUtils.isEmpty(charset) || (isBinaryRes(mime))) {
                            WebResourceResponse resourceResponse = new WebResourceResponse(mime, charset, httpURLConnection.getInputStream());
                            resourceResponse.setStatusCodeAndReasonPhrase(statusCode, response);
                            Map<String, String> responseHeader = new HashMap<String, String>();
                            for (String key : headerKeySet) {
                                // HttpUrlConnection可能包含key为null的报头，指向该http请求状态码
                                responseHeader.put(key, httpURLConnection.getHeaderField(key));
                            }
                            resourceResponse.setResponseHeaders(responseHeader);
                            return resourceResponse;
                        } else {
                            if (isDebug()) {
                                Log.e(TAG, "non binary resource for " + mime);
                            }
                            return super.shouldInterceptRequest(view, request);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return super.shouldInterceptRequest(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            // API < 21 只能拦截URL参数
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            if (isDebug()) {
                Log.e(TAG, "onReceivedError: ------1--->");
            }
            if (mWebViewListener != null) {
                mWebViewListener.onRequestNetworkErrorCallback(webView);
            }
        }

        @Override
        public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
            super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
            if (isDebug()) {
                Log.e(TAG, "onReceivedHttpError: ---------3------->");
            }
//            mErrorCode = webResourceResponse.getStatusCode();
//            if (mWebViewListener != null) {
//                mWebViewListener.onRequestErrorCallback(webView, webResourceResponse.getStatusCode());
//            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //https忽略证书问题
            if (isDebug()) {
                Log.e(TAG, "onReceivedSslError: ---------->");
            }
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            if (isDebug()) {
                Log.e(TAG, "onPageStarted: ----------->" + s);
            }
            if (mWebViewListener != null) {
                mWebViewListener.onPageStarted(webView);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            removeCallbacks(mDismissRunnable);
            if (isDebug()) {
                Log.e(TAG, "onPageFinished: ---------->" + url);
            }
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

    /**
     * 从contentType中获取MIME类型
     *
     * @param contentType contentType
     * @return Mime
     */
    private String getMime(String contentType) {
        if (contentType == null) {
            return null;
        }
        return contentType.split(";")[0];
    }

    /**
     * 从contentType中获取编码信息
     *
     * @param contentType contentType
     * @return return charSet
     */
    private String getCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        String[] fields = contentType.split(";");
        if (fields.length <= 1) {
            return null;
        }

        String charset = fields[1];
        if (!charset.contains("=")) {
            return null;
        }
        charset = charset.substring(charset.indexOf("=") + 1);
        return charset;
    }

    /**
     * 是否是二进制资源，二进制资源可以不需要编码信息
     *
     * @param mime mime
     * @return isBinaryRes
     */
    private boolean isBinaryRes(String mime) {
        return mime.startsWith("image") || mime.startsWith("audio") || mime.startsWith("video");
    }

    /**
     * header中是否含有cookie
     *
     * @param headers headers
     */
    private boolean containCookie(Map<String, String> headers) {
        for (Map.Entry<String, String> headerField : headers.entrySet()) {
            if (headerField.getKey().contains("Cookie")) {
                return true;
            }
        }
        return false;
    }

    private boolean needRedirect(int code) {
        return code >= 300 && code < 400;
    }

    private URLConnection recursiveRequest(String path, Map<String, String> headers, String reffer) {
        HttpURLConnection conn;
        URL url;
        try {
            url = new URL(path);
            // 异步接口获取IP
            String ip = WebViewManger.getInstance().getHttpDnsEngine().getHostIpFromHostname(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                if (isDebug()) {
                    Log.e(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                }
                String newUrl = path.replaceFirst(url.getHost(), ip);
                conn = (HttpURLConnection) new URL(newUrl).openConnection();

                if (headers != null) {
                    for (Map.Entry<String, String> field : headers.entrySet()) {
                        conn.setRequestProperty(field.getKey(), field.getValue());
                    }
                }
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            } else {
                return null;
            }
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(false);
            if (conn instanceof HttpsURLConnection) {
                final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
                WebViewTlsSniSocketFactory sslSocketFactory = new WebViewTlsSniSocketFactory((HttpsURLConnection) conn);

                // sni场景，创建SSLScocket
                httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                // https场景，证书校验
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        String host = httpsURLConnection.getRequestProperty("Host");
                        if (null == host) {
                            host = httpsURLConnection.getURL().getHost();
                        }
                        return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                    }
                });
            }
            int code = conn.getResponseCode();// Network block
            if (needRedirect(code)) {
                // 原有报头中含有cookie，放弃拦截
                if (containCookie(headers)) {
                    return null;
                }

                String location = conn.getHeaderField("Location");
                if (location == null) {
                    location = conn.getHeaderField("location");
                }

                if (location != null) {
                    if (!(location.startsWith("http://") || location.startsWith("https://"))) {
                        //某些时候会省略host，只返回后面的path，所以需要补全url
                        URL originalUrl = new URL(path);
                        location = originalUrl.getProtocol() + "://"
                                + originalUrl.getHost() + location;
                    }
                    if (isDebug()) {
                        Log.e(TAG, "code:" + code + "; location:" + location + "; path" + path);
                    }
                    return recursiveRequest(location, headers, path);
                } else {
                    // 无法获取location信息，让浏览器获取
                    return null;
                }
            } else {
                // redirect finish.
                if (isDebug()) {
                    Log.e(TAG, "redirect finish");
                }
                return conn;
            }
        } catch (MalformedURLException e) {
            if (isDebug()) {
                Log.w(TAG, "recursiveRequest MalformedURLException");
            }
        } catch (IOException e) {
            if (isDebug()) {
                Log.w(TAG, "recursiveRequest IOException");
            }
        } catch (Exception e) {
            if (isDebug()) {
                Log.w(TAG, "unknow exception");
            }
        }
        return null;
    }


    private boolean isDebug() {
        return WebViewManger.getInstance().isDebug();
    }
}
