package com.sumian.sleepdoctor.improve.widget.webview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.error.EmptyErrorView;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/25 09:54
 * desc:
 **/
public class X5WebViewContainer extends FrameLayout implements X5WebView.OnX5WebViewListener, EmptyErrorView.OnEmptyCallback {

    @BindView(R.id.web_view_progress)
    ProgressBar mWebViewProgress;

    @BindView(R.id.empty_error_view)
    EmptyErrorView mEmptyErrorView;

    private X5WebView mX5WebView;


    public X5WebViewContainer(@NonNull Context context) {
        this(context, null);
    }

    public X5WebViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public X5WebViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_webview_container_view, this));
        this.mEmptyErrorView.setOnEmptyCallback(this);
        this.mX5WebView = new X5WebView(context);
        this.mX5WebView.setOnWebViewListener(this);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mX5WebView, 0, layoutParams);
    }

    public X5WebView getX5WebView() {
        return mX5WebView;
    }

    public void resumeWebView() {
        mX5WebView.onResume();
        mX5WebView.resumeTimers();
    }

    public void pauseWebView() {
        mX5WebView.onPause();
        mX5WebView.pauseTimers();
    }

    public void destroyWebView() {
        removeView(mX5WebView);
        mX5WebView.destroy();
    }

    public boolean webViewCanGoBack() {
        if (mX5WebView != null && mX5WebView.canGoBack()) {
            mX5WebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    public void loadRequestUrl(String requestUrl) {
        this.mEmptyErrorView.hide();
        this.mWebViewProgress.setVisibility(VISIBLE);
        this.mWebViewProgress.setProgress(0);
        this.mX5WebView.loadRequestUrl(requestUrl);
    }

    @Override
    public void onPageStarted(WebView view) {
        this.mEmptyErrorView.hide();
        this.mWebViewProgress.setVisibility(VISIBLE);
        this.mWebViewProgress.setProgress(0);
    }

    @Override
    public void onProgressChange(WebView view, int newProgress) {
        this.mWebViewProgress.setProgress(newProgress);
    }

    @Override
    public void onPageFinish(WebView view) {
        this.mWebViewProgress.setVisibility(GONE);
        this.mWebViewProgress.setProgress(0);
        this.mEmptyErrorView.hide();
    }

    @Override
    public void onRequestErrorCallback(WebView view, int responseCode) {
        this.mWebViewProgress.setVisibility(GONE);
        this.mWebViewProgress.setProgress(0);
        this.mEmptyErrorView.invalidRequestError();
    }

    @Override
    public void onRequestNetworkErrorCallback(WebView view) {
        this.mWebViewProgress.setVisibility(GONE);
        this.mWebViewProgress.setProgress(0);
        this.mEmptyErrorView.invalidNetworkError();
    }

    @Override
    public void onReload() {
        this.mX5WebView.reload();
    }
}
