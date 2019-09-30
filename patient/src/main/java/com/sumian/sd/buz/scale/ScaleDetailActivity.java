package com.sumian.sd.buz.scale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.buz.scale.event.ScaleFinishFillingEvent;
import com.sumian.sd.buz.scale.event.ScaleFinishFillingEvent2;
import com.sumian.sd.buz.stat.StatConstants;
import com.sumian.sd.common.utils.EventBusUtil;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

public class ScaleDetailActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL_CONTENT_PART = "url_content_part";
    private String mTitle;
    private String mUrlContentPart;

    public static void launch(Context context, String title, String urlContentPart) {
        ActivityUtils.startActivity(getLaunchIntent(context, title, urlContentPart));
    }

    public static Intent getLaunchIntent(Context context, String title, String urlContentPart) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_URL_CONTENT_PART, urlContentPart);
        Intent intent = new Intent(context, ScaleDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void initBundle(@NonNull Bundle bundle) {
        mTitle = bundle.getString(KEY_TITLE);
        mUrlContentPart = bundle.getString(KEY_URL_CONTENT_PART);
    }

    @Override
    protected String initTitle() {
        return mTitle;
    }

    @NotNull
    @Override
    public String getPageName() {
        return StatConstants.page_scale_detail;
    }

    @Override
    protected String getUrlContentPart() {
        return mUrlContentPart;
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        sWebView.registerHandler("scaleFinishFilling", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                EventBusUtil.postStickyEvent(new ScaleFinishFillingEvent());
                EventBusUtil.postStickyEvent(new ScaleFinishFillingEvent2());
            }
        });
    }
}
