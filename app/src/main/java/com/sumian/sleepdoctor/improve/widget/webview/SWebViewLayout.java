package com.sumian.sleepdoctor.improve.widget.webview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.widget.error.EmptyErrorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/25 09:54
 * desc:
 **/
public class SWebViewLayout extends FrameLayout implements SWebView.OnWebViewListener, EmptyErrorView.OnEmptyCallback {

    @BindView(R.id.web_view_progress)
    ProgressBar mWebViewProgress;
    @BindView(R.id.empty_error_view)
    EmptyErrorView mEmptyErrorView;

    private SWebView mSWebView;
    private WebListener mWebListener;

    public SWebViewLayout(@NonNull Context context) {
        this(context, null);
    }

    public SWebViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SWebViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_webview_container_view, this));
        this.mEmptyErrorView.setOnEmptyCallback(this);
        this.mSWebView = new SWebView(getContext());
        this.mSWebView.setOnWebViewListener(this);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mSWebView, 0, layoutParams);
    }

    public SWebView getSWebView() {
        return mSWebView;
    }

    public void resumeWebView() {
        mSWebView.onResume();
        mSWebView.resumeTimers();
    }

    public void pauseWebView() {
        mSWebView.onPause();
        mSWebView.pauseTimers();
    }

    public void destroyWebView() {
        removeView(mSWebView);
        if (mSWebView != null) {
            mSWebView.destroy();
        }
    }

    public boolean webViewCanGoBack() {
        if (mSWebView != null && mSWebView.canGoBack()) {
            mSWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    public void loadRequestUrl(String requestUrl) {
        this.mEmptyErrorView.hide();
        this.mWebViewProgress.setVisibility(VISIBLE);
        this.mWebViewProgress.setProgress(0);
        this.mSWebView.loadRequestUrl(requestUrl);
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
    public void onReceiveTitle(WebView view, String title) {
        if (mWebListener != null) {
            mWebListener.onReceiveTitle(view, title);
        }
    }

    @Override
    public void onReload() {
        this.mSWebView.reload();
    }


    public void setWebListener(WebListener webListener) {
        mWebListener = webListener;
    }

    public interface WebListener {
        void onReceiveTitle(WebView webView, String title);
    }

    public ProgressBar getWebViewProgress() {
        return mWebViewProgress;
    }
}
