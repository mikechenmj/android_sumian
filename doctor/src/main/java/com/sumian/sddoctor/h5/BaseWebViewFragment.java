package com.sumian.sddoctor.h5;

import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.common.h5.widget.SWebViewLayout;
import com.sumian.sddoctor.BuildConfig;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.base.BaseFragment;
import com.sumian.sddoctor.widget.TitleBar;
import com.tencent.smtt.sdk.WebView;


/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
@SuppressWarnings("ConstantConditions")
public abstract class BaseWebViewFragment extends BaseFragment implements TitleBar.OnBackClickListener, SWebView.OnWebViewListener {

    protected TitleBar mTitleBar;

    protected View mDivider;

    protected SWebViewLayout mSWebViewLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_base_webview;
    }

    @Override
    public void initWidget(View root) {
        super.initWidget(root);
        mTitleBar = root.findViewById(R.id.title_bar);
        mTitleBar.setOnBackClickListener(this);
        mDivider = root.findViewById(R.id.view_divider);
        mSWebViewLayout = root.findViewById(R.id.sm_webview_container);
        mSWebViewLayout.loadRequestUrl(getCompleteUrl());
//        new H5LocalCacheInterceptor(getActivity(), H5LocalCachePath.ASSET_PATH).interceptH5Request(mSWebViewLayout.getSWebView());
    }

    @Override
    public void initData() {
        super.initData();
        registerHandler(mSWebViewLayout.getSWebView());
        mSWebViewLayout.setWebListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSWebViewLayout.resumeWebView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSWebViewLayout.pauseWebView();
    }

    @Override
    public void onDestroy() {
        mSWebViewLayout.destroyWebView();
        super.onDestroy();
    }

    protected String h5HandlerName() {
        return null;
    }

    protected void registerHandler(SWebView sWebView) {

    }

    @Override
    public void onBack(View v) {
        if (!mSWebViewLayout.webViewCanGoBack()) {
            getActivity().onBackPressed();
        }
    }

    protected String getCompleteUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        String urlServerPart = getUrlServerPart();
        String urlContentPart = getUrlContentPart();
        boolean contentContainerParams = urlContentPart.contains("?");
        stringBuilder.append(urlServerPart)
                .append(urlContentPart)
                .append(contentContainerParams ? "&" : "?")
                .append(getUrlToken());
        String url = stringBuilder.toString();
        LogUtils.d("load url: %s", url);
        return url;
    }

    protected String getUrlServerPart() {
        return BuildConfig.BASE_H5_URL;
    }

    protected String getUrlContentPart() {
        return null;
    }

    private String getUrlToken() {
        return "token=" + AppManager.getAccountViewModel().getToken();
    }

    @Override
    public void onReceiveTitle(WebView webView, String title) {
        mTitleBar.setTitle(title);
    }

    @Override
    public void onPageStarted(WebView webView) {

    }

    @Override
    public void onProgressChange(WebView webView, int i) {

    }

    @Override
    public void onPageFinish(WebView webView) {

    }

    @Override
    public void onRequestErrorCallback(WebView webView, int i) {

    }

    @Override
    public void onRequestNetworkErrorCallback(WebView webView) {

    }
}
