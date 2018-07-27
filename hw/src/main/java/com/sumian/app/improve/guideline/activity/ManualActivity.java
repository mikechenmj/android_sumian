package com.sumian.app.improve.guideline.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sumian.app.BuildConfig;
import com.sumian.app.R;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/3/27.
 * <p>
 * desc:新手指南,使用手册
 */

public class ManualActivity extends BaseActivity implements TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.webview)
    WebView mWebView;

    public static void show(Context context) {
        context.startActivity(new Intent(context, ManualActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_manual;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initWidget() {
        super.initWidget();

        mTitleBar.addOnBackListener(this);

        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //设置自适应屏幕，两者合用（下面这两个方法合用）
        //webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportMultipleWindows(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //缩放操作
        //webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        //webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        //webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //Android 5.0上Webview默认不允许加载Http与Https混合内容,所以需要兼容混合模式(比如 src 中图片 url 为 http)
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
    }

    @Override
    protected void initData() {
        super.initData();
        mWebView.loadUrl(BuildConfig.USER_GUIDELINE_URL);
        mWebView.setWebChromeClient(new WebChromeClient() {
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
