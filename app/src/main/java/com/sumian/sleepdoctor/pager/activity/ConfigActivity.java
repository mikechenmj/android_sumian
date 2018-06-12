package com.sumian.sleepdoctor.pager.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class ConfigActivity extends BaseActivity implements TitleBar.OnBackClickListener {

    public static final String ARGS_CONFIG_TYPE = "args_config_type";

    public static final int ABOUT_ME = 0x01;
    public static final int USER_AGREEMENT = 0x02;
    public static final int USER_PRIVACY = 0x03;

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.lay_container)
    LinearLayout mLayContainer;

    WebView mWebView;

    int mConfigType = ABOUT_ME;
    String mLoadUrl = BuildConfig.ABOUT_ME_URL;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mConfigType = bundle.getInt(ARGS_CONFIG_TYPE, ABOUT_ME);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_about_me;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        @StringRes int resType;
        switch (mConfigType) {
            case ABOUT_ME:
                resType = R.string.about_me;
                mLoadUrl = BuildConfig.ABOUT_ME_URL;
                break;
            case USER_AGREEMENT:
                resType = R.string.register_rule_user_agreement_title;
                mLoadUrl = BuildConfig.USER_AGREEMENT_URL;
                break;
            case USER_PRIVACY:
                resType = R.string.register_rule_privacy_policy_title;
                mLoadUrl = BuildConfig.USER_POLICY_URL;
                break;
            default:
                resType = R.string.about_me;
                mLoadUrl = BuildConfig.ABOUT_ME_URL;
                break;
        }

        mTitleBar.setTitle(resType);
        mTitleBar.setOnBackClickListener(this);

        //防止 webview  内存泄漏  不在 xml 文件中声明
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayContainer.addView(mWebView);

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
        mWebView.loadUrl(mLoadUrl);
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


    @Override
    protected void onRelease() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onRelease();
    }
}
