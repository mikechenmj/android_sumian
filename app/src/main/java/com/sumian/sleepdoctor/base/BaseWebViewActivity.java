package com.sumian.sleepdoctor.base;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
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

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.sm_webview_container)
    SWebViewContainer mSWebViewContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_base_webview;
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
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        super.onStart(owner);
        mSWebViewContainer.loadRequestUrl(getCompleteUrl());
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

    protected String h5HandlerName(){
        return null;
    }

    @StringRes
    protected abstract int initTitle();

    protected void registerHandler(SWebView sWebView){

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

    private String getCompleteUrl() {
        return getUrlServerPart() + getUrlContentPart() + getUrlToken();
    }

    private String getUrlServerPart() {
        return BuildConfig.BASE_H5_URL;
    }

    protected abstract String getUrlContentPart();

    private String getUrlToken() {
        return "?token=" + AppManager.getAccountViewModel().accessToken();
    }

}
