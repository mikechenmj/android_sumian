package com.sumian.sleepdoctor.h5;

import android.content.Context;
import android.os.Bundle;

import com.sumian.sleepdoctor.base.BaseWebViewActivity;

public class SimpleWebActivity extends BaseWebViewActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL_CONTENT_PART = "urlContentPart";
    private String mTitle;
    private String mUrlContentPart;

    public static void launch(Context context, String title, String urlContentPart) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_URL_CONTENT_PART, urlContentPart);
        show(context, SimpleWebActivity.class, bundle);
    }

    public static void launch(Context context, String urlContentPart) {
        launch(context, "", urlContentPart);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mTitle = bundle.getString(KEY_TITLE);
        mUrlContentPart = bundle.getString(KEY_URL_CONTENT_PART);
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
}
