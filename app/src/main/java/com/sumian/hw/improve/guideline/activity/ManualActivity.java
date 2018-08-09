package com.sumian.hw.improve.guideline.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sleepdoctor.base.SdBaseWebViewActivity;

/**
 * Created by sm
 * on 2018/3/27.
 * <p>
 * desc:新手指南,使用手册
 */

public class ManualActivity extends SdBaseWebViewActivity implements TitleBar.OnBackListener {

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setBgColor(getResources().getColor(R.color.hw_colorPrimary));
        mTitleBar.setTextColor(getResources().getColor(R.color.bt_hole_color));
    }

    @Override
    protected String getCompleteUrl() {
        return BuildConfig.HW_USER_GUIDELINE_URL;
    }
}
