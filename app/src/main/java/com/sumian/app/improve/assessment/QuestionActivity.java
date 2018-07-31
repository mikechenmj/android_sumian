package com.sumian.app.improve.assessment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.app.account.bean.Answer;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.network.response.HwUserInfo;

/**
 * Created by sm
 * on 2018/3/14.
 * desc:
 */

public class QuestionActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = QuestionActivity.class.getSimpleName();

    ImageView mIvBack;
    TextView mTvTitle;
    TextView mIvReDo;
    WebView mWebView;
    LinearLayout mFinishContainer;

    public static void show(Context context) {
        context.startActivity(new Intent(context, QuestionActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_question;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mIvReDo = findViewById(R.id.iv_re_do);
        mWebView = findViewById(R.id.web);
        mFinishContainer = findViewById(R.id.finish_container);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_re_do).setOnClickListener(this);
        findViewById(R.id.bt_finish).setOnClickListener(this);

        boolean haveAnswers = HwAppManager.getAccountModel().isHaveAnswers();
        if (!haveAnswers) {
            initWebView();
        } else {
            mFinishContainer.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mFinishContainer.setVisibility(View.GONE);
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //设置自适应屏幕，两者合用（下面这两个方法合用）
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportMultipleWindows(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //Android 5.0上Webview默认不允许加载Http与Https混合内容,所以需要兼容混合模式(比如 src 中图片 url 为 http)
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        // todo in gradle pro
        String HW_DEV_QUESTION_URL="http://sumian-question-h5-dev.oss-cn-shenzhen.aliyuncs.com/index.html";
        String HW_TEST_QUESTION_URL="http://sumian-question-h5-test.oss-cn-shenzhen.aliyuncs.com/index.html";
        String HW_OFFICIAL_QUESTION_URL="http://sumian-question-h5-production.oss-cn-shenzhen.aliyuncs.com/index.html";
        String HW_CLINIC_OFFICIAL_QUESTION_URL="http://sumian-question-h5-clinic.oss-cn-shenzhen.aliyuncs.com/index.html";
        mWebView.loadUrl(HW_DEV_QUESTION_URL + "?token=" + HwAppManager.getAccountModel().accessToken());

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                //sumian://sleep_quality_result?data={"id":35,"answers":"06:04,1,00:08,05:08,2,2,2","score":9,"level":1,"created_at":1521047312}
                String url = request.getUrl().toString();
                if (url.startsWith("sumian://sleep_quality_result?data=")) {
                    String json = url.substring(url.indexOf("{"));
                    Answer answer = JSON.parseObject(json, Answer.class);
                    HwUserInfo userInfo = HwAppManager.getAccountModel().getUserInfo();
                    userInfo.setAnswers(answer);
                    HwAppManager.getAccountModel().updateUserCache(userInfo);
                    finish();
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        mWebView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            finish();
        } else if (i == R.id.iv_re_do) {
            initWebView();
        } else if (i == R.id.bt_finish) {
            finish();
        }
    }
}
