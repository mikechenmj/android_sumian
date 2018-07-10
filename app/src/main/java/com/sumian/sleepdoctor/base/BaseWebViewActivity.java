package com.sumian.sleepdoctor.base;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.h5.bean.H5ShowToastData;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.improve.widget.webview.SWebViewLayout;
import com.sumian.sleepdoctor.utils.ScreenUtil;
import com.sumian.sleepdoctor.utils.SoftKeyBoardListener;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.dialog.SumianImageTextDialog;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class BaseWebViewActivity<Presenter extends BasePresenter> extends BaseActivity<Presenter> implements TitleBar.OnBackClickListener, SWebViewLayout.WebListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.root_view)
    View mRootView;
    @BindView(R.id.sm_webview_container)
    protected SWebViewLayout mSWebViewLayout;
    private SoftKeyBoardListener mSoftKeyBoardListener;
    private SumianImageTextDialog mSumianImageTextDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_base_webview;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monitorKeyboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoftKeyBoardListener.release();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        SWebView sWebView = mSWebViewLayout.getSWebView();
        registerHandler(sWebView);
        mSWebViewLayout.setWebListener(this);
        registerDialogHandler(sWebView);
        mSWebViewLayout.loadRequestUrl(getCompleteUrl());
    }

    private void registerDialogHandler(SWebView sWebView) {
        sWebView.registerHandler("showToast", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                H5ShowToastData toastData = H5ShowToastData.Companion.fromJson(data);
                //1.text 2.success 3.error 4.loading 5.warning
                if (mSumianImageTextDialog != null && mSumianImageTextDialog.isShowing()) {
                    mSumianImageTextDialog.dismiss();
                }
                mSumianImageTextDialog = new SumianImageTextDialog(mActivity);
                switch (toastData.getType()) {
                    case "text":
                        mSumianImageTextDialog.setType(SumianImageTextDialog.TYPE_TEXT).setText(toastData.getMessage());
                        break;
                    case "success":
                        mSumianImageTextDialog.setType(SumianImageTextDialog.TYPE_SUCCESS);
                        break;
                    case "error":
                        mSumianImageTextDialog.setType(SumianImageTextDialog.TYPE_FAIL);
                        break;
                    case "loading":
                        mSumianImageTextDialog.setType(SumianImageTextDialog.TYPE_LOADING);
                        break;
                    case "warning":
                        mSumianImageTextDialog.setType(SumianImageTextDialog.TYPE_WARNING);
                        break;
                    default:
                        break;
                }
                if (mSumianImageTextDialog != null) {
                    mSumianImageTextDialog.show(toastData.getDelay(), toastData.getDuration());
                }
            }
        });
        sWebView.registerHandler("hideToast", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                H5ShowToastData toastData = H5ShowToastData.Companion.fromJson(data);
                if (mSumianImageTextDialog != null && mSumianImageTextDialog.isShowing()) {
                    mSumianImageTextDialog.dismiss(toastData.getDelay());
                }
            }
        });
    }


    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        super.onStart(owner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSWebViewLayout.resumeWebView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSWebViewLayout.pauseWebView();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        mSWebViewLayout.destroyWebView();
    }

    protected String h5HandlerName() {
        return null;
    }

    protected String initTitle() {
        return null;
    }

    protected void registerHandler(SWebView sWebView) {

    }

    @Override
    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!mSWebViewLayout.webViewCanGoBack()) {
            super.onBackPressed();
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

    private String getUrlServerPart() {
        return BuildConfig.BASE_H5_URL;
    }

    protected String getUrlContentPart() {
        return null;
    }

    private String getUrlToken() {
        return "token=" + AppManager.getAccountViewModel().accessToken();
    }

    @Override
    public void onReceiveTitle(WebView webView, String title) {
        mTitleBar.setTitle(title);
    }

    private void monitorKeyboard() {
        mSoftKeyBoardListener = SoftKeyBoardListener.registerListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                updateRootViewHeight(ScreenUtil.getScreenHeight(mActivity) - height);
            }

            @Override
            public void keyBoardHide(int height) {
                updateRootViewHeight(ScreenUtil.getScreenHeight(mActivity));
            }
        });
    }

    private void updateRootViewHeight(int height) {
        ViewGroup.LayoutParams layoutParams = mRootView.getLayoutParams();
        layoutParams.height = height;
        mRootView.requestLayout();
    }
}
