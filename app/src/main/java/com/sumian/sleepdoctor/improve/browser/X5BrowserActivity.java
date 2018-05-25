package com.sumian.sleepdoctor.improve.browser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.widget.SmWebView;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

public class X5BrowserActivity extends BaseActivity implements TitleBar.OnBackListener {

    public static final String ARGS_URL = "args_url";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.sm_web_view)
    SmWebView mWebView;

    private String mUrl;

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mUrl = bundle.getString(ARGS_URL);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_browser;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
        mWebView.initWebViewSettings();
        mWebView.initClient();
        mWebView.initChromeClient();
    }

    @Override
    protected void initData() {
        super.initData();
        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
