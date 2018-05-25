package com.sumian.sleepdoctor.base;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.improve.widget.webview.X5WebViewContainer;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class BaseX5Activity extends BaseActivity implements TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.x5_webview_container)
    X5WebViewContainer mX5WebViewContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_base_x5;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setText(initTitleBarText());
        mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mX5WebViewContainer.loadRequestUrl(initRequestUrl());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mX5WebViewContainer.resumeWebView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mX5WebViewContainer.pauseWebView();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        mX5WebViewContainer.destroyWebView();
    }

    protected abstract String initTitleBarText();

    protected abstract String initRequestUrl();


    @Override
    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!mX5WebViewContainer.webViewCanGoBack()) {
            super.onBackPressed();
        }
    }

}
