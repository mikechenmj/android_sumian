package com.sumian.sd.base;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.sumian.common.utils.JsonUtil;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.h5.bean.H5ShowToastData;
import com.sumian.sd.utils.ScreenUtil;
import com.sumian.sd.utils.SoftKeyBoardListener;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.dialog.SumianImageTextDialog;
import com.sumian.sd.widget.webview.SBridgeHandler;
import com.sumian.sd.widget.webview.SWebView;
import com.sumian.sd.widget.webview.SWebViewLayout;

import java.util.Map;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 **/
public abstract class SdBaseWebViewActivity<Presenter extends SdBasePresenter> extends SdBaseActivity<Presenter> implements TitleBar.OnBackClickListener, SWebViewLayout.WebListener {

    @BindView(R.id.sm_webview_container)
    protected SWebViewLayout mSWebViewLayout;
    @BindView(R.id.title_bar)
    protected TitleBar mTitleBar;
    @BindView(R.id.root_view)
    View mRootView;
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
        mSWebViewLayout.setWebListener(this);
        mSWebViewLayout.loadRequestUrl(getCompleteUrl());
        registerHandler(sWebView);
        registerBaseHandler(sWebView);
    }

    private void registerBaseHandler(SWebView sWebView) {
        sWebView.registerHandler("showToast", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                H5ShowToastData toastData = H5ShowToastData.Companion.fromJson(data);
                if (mSumianImageTextDialog != null) {
                    mSumianImageTextDialog.dismiss();
                } else {
                    mSumianImageTextDialog = new SumianImageTextDialog(mActivity);
                }
                mSumianImageTextDialog.show(toastData);
            }
        });
        sWebView.registerHandler("hideToast", new SBridgeHandler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                H5ShowToastData toastData = H5ShowToastData.Companion.fromJson(data);
                if (mSumianImageTextDialog != null) {
                    mSumianImageTextDialog.dismiss(toastData.getDelay());
                }
            }
        });
        sWebView.registerHandler("finish", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                finish();
            }
        });
        sWebView.registerHandler("updatePageUI", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                super.handler(data);
                Map<String, Object> map = JsonUtil.Companion.fromJson(data, new TypeToken<Map<String, Object>>() {
                }.getType());
                if (map == null) {
                    return;
                }
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    switch (entry.getKey()) {
                        case "showNavigationBar":
                            if (entry.getValue() instanceof Boolean) {
                                mTitleBar.setVisibility((boolean) entry.getValue() ? View.VISIBLE : View.GONE);
                            }
                            break;
                        case "showTitle":
                            if (entry.getValue() instanceof Boolean) {
                                mTitleBar.showTitle((boolean) entry.getValue());
                            }
                            break;
                        case "showBackArrow":
                            if (entry.getValue() instanceof Boolean) {
                                mTitleBar.showBackArrow((boolean) entry.getValue());
                            }
                            break;
                        default:
                            break;
                    }
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

    @Override
    protected void onStop() {
        if (mSumianImageTextDialog != null) {
            mSumianImageTextDialog.release();
        }
        super.onStop();
    }

    protected String h5HandlerName() {
        return null;
    }

    protected String initTitle() {
        return null;
    }

    protected void registerHandler(@NonNull SWebView sWebView) {
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
        boolean contentContainerParams = urlContentPart != null && urlContentPart.contains("?");
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
        return "token=" + getToken();
    }

    protected static String getToken() {
        return AppManager.getAccountViewModel().getTokenString();
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

    protected void reload() {
        mSWebViewLayout.reload();
    }
}
