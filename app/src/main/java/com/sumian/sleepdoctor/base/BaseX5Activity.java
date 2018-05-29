package com.sumian.sleepdoctor.base;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.improve.widget.webview.X5WebViewContainer;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class BaseX5Activity extends BaseActivity implements TitleBar.OnBackListener {

    private static final String TAG = BaseX5Activity.class.getSimpleName();

    public static final String ARGS_URL = "com.sumian.sleepdoctor.extra.args.url";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.x5_webview_container)
    X5WebViewContainer mX5WebViewContainer;

    protected String mUrl;
    protected String mTitleContent = "错误页面";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_base_x5;
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
        mTitleBar.setText(mTitleContent);
        mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mX5WebViewContainer.loadRequestUrl(mUrl);
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

    protected void parseUrl(String url) {
        Uri parseUri = Uri.parse(url);
        Log.e(TAG, "parseUrl: ---------->" + mUrl);
        String scheme = parseUri.getScheme();
        String tmpFormatUrl = null;
        switch (scheme) {
            case "sleepdoctor":
                tmpFormatUrl = formatDoctorDetailUrl(parseUri);
                mTitleContent = "绑定医生";
                break;
            default:
                mTitleContent = "错误页面";
                break;
        }
        this.mUrl = appendToken(tmpFormatUrl);
        Log.e(TAG, "parseUrl: -----decode url--->" + mUrl);
    }

    /**
     * 之所以用 protected,不用 private  是因为子类如果有需要,可以直接重写该方法,进行编码 url 覆盖操作
     *
     * @param url url  默认识别出的 url
     * @return 根据 scheme 类型 编码好的正确的 url;
     */
    protected String formatDoctorDetailUrl(Uri url) {
        return BuildConfig.BASE_H5_URL + BuildConfig.H5_URI_DOCTOR.replace("{id}", url.getQueryParameter("id"));
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
        if (!mX5WebViewContainer.webViewCanGoBack()) {
            super.onBackPressed();
        }
    }

}
