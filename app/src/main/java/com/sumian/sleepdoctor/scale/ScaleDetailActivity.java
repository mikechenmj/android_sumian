package com.sumian.sleepdoctor.scale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.base.SdBaseWebViewActivity;
import com.sumian.sleepdoctor.event.EventBusUtil;
import com.sumian.sleepdoctor.event.ScaleFinishFillingEvent;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.widget.webview.SWebView;

public class ScaleDetailActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_SCALE_ID = "scale_id";
    private String mTitle;
    private long mScaleId;

    public static void launch(Context context, String title, long scaleId) {
        show(context, getLaunchIntent(context, title, scaleId));
    }

    public static Intent getLaunchIntent(Context context, String title, long scaleId) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putLong(KEY_SCALE_ID, scaleId);
        Intent intent = new Intent(context, ScaleDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mTitle = bundle.getString(KEY_TITLE);
        mScaleId = bundle.getLong(KEY_SCALE_ID);
        return super.initBundle(bundle);
    }

    @Override
    protected String initTitle() {
        return mTitle;
    }

    @Override
    protected String getUrlContentPart() {
        String uri = H5Uri.FILL_SCALE;
        uri = uri.replace("{id}", String.valueOf(mScaleId));
        return uri;
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        sWebView.registerHandler("scaleFinishFilling", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                EventBusUtil.postStickyEvent(new ScaleFinishFillingEvent());
            }
        });
    }
}
