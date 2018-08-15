package com.sumian.sd.h5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sumian.sd.base.ActivityLauncher;
import com.sumian.sd.base.SdBaseWebViewActivity;

public class SimpleWebActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL_CONTENT_PART = "urlContentPart";
    private String mTitle;
    private String mUrlContentPart;

    public static void launch(Context context, String urlContentPart) {
        Intent intent = getIntent(context, urlContentPart);
        context.startActivity(intent);
    }

    public static void launchForResult(ActivityLauncher launcher, String urlContentPart, int requestCode) {
        Intent intent = getIntent(launcher.getActivity(), urlContentPart);
        launcher.startActivityForResult(intent, requestCode);
    }

    @NonNull
    private static Intent getIntent(Context context, String urlContentPart) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL_CONTENT_PART, urlContentPart);
        Intent intent = new Intent(context, SimpleWebActivity.class);
        intent.putExtras(bundle);
        return intent;
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
