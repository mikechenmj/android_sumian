package com.sumian.sd.h5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sumian.sd.base.SdBaseWebViewActivity;

public class SimpleWebActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_URL_CONTENT_PART = "KEY_URL_CONTENT_PART";
    public static final String KEY_URL_COMPLETE = "KEY_URL_COMPLETE";
    private String mTitle;
    private String mUrlContentPart;
    private String mUrlComplete;

    public static void launch(Context context, String urlContentPart) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL_CONTENT_PART, urlContentPart);
        Intent intent = new Intent(context, SimpleWebActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void launchWithCompleteUrl(Context context, String completeUrl) {
        Intent intent = getLaunchIntentWithCompleteUrl(context, completeUrl);
        context.startActivity(intent);
    }

    public static Intent getLaunchIntentWithCompleteUrl(Context context, String completeUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL_COMPLETE, completeUrl);
        Intent intent = new Intent(context, SimpleWebActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mTitle = bundle.getString(KEY_TITLE);
        mUrlContentPart = bundle.getString(KEY_URL_CONTENT_PART);
        mUrlComplete = bundle.getString(KEY_URL_COMPLETE);
        return super.initBundle(bundle);
    }

    @Override
    protected String initTitle() {
        return mTitle;
    }

    @Override
    protected String getUrlContentPart() {
        return mUrlContentPart;
    }

    @Override
    protected String getCompleteUrl() {
        if (mUrlComplete != null) {
            return mUrlComplete;
        }
        return super.getCompleteUrl();
    }
}
