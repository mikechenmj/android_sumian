package com.sumian.hw.guideline.activity;

import android.view.View;

import com.sumian.sd.BuildConfig;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sd.base.SdBaseWebViewActivity;

/**
 * Created by sm
 * on 2018/3/27.
 * <p>
 * desc:新手指南,使用手册
 */

public class ManualActivity extends SdBaseWebViewActivity{

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setIsDarkTheme(true);
    }

    @Override
    protected String getCompleteUrl() {
        return BuildConfig.HW_USER_GUIDELINE_URL;
    }
}
