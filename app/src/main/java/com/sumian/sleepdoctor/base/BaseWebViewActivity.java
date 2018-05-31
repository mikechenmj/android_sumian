package com.sumian.sleepdoctor.base;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.improve.widget.webview.SWebViewContainer;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class BaseWebViewActivity<Presenter extends BasePresenter> extends BaseActivity<Presenter> implements TitleBar.OnBackListener {

    private static final String TAG = BaseWebViewActivity.class.getSimpleName();

    public static final String ARGS_URL = "com.sumian.sleepdoctor.extra.args.url";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.sm_webview_container)
    SWebViewContainer mSWebViewContainer;

    protected String mUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_base_webview;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            parseUrl(bundle.getString(ARGS_URL));
        }
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setTitle(initTitle());
        mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        registerHandler(mSWebViewContainer.getSWebView());
        mSWebViewContainer.loadRequestUrl(mUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSWebViewContainer.resumeWebView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSWebViewContainer.pauseWebView();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        mSWebViewContainer.destroyWebView();
    }

    protected String initBaseH5Url() {
        return BuildConfig.BASE_H5_URL;
    }

    protected abstract String h5HandlerName();

    @StringRes
    protected abstract int initTitle();

    protected abstract String queryParameter();

    protected abstract String appendUri();

    protected abstract void registerHandler(SWebView sWebView);


    protected void parseUrl(String url) {
        this.mUrl = initBaseH5Url() + appendToken(url);
    }

    private String appendToken(String formatUrl) {
        return formatUrl + "?token=" + AppManager.getAccountViewModel().accessToken();
    }

    @Override
    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!mSWebViewContainer.webViewCanGoBack()) {
            super.onBackPressed();
        }
    }

}
