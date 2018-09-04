package com.sumian.sd.h5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.base.SdBaseWebViewActivity;

public class SimpleWebActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_URL_CONTENT_PART = "KEY_URL_CONTENT_PART";
    public static final String KEY_URL_COMPLETE = "KEY_URL_COMPLETE";
    private String mTitle;
    private String mUrlContentPart;
    private String mUrlComplete;

    public static void launch(Context context, String urlContentPart) {
        Intent intent = getLaunchIntentWithPartUrl(context, urlContentPart);
        ActivityUtils.startActivity(intent);
    }

    public static void launchWithCompleteUrl(Context context, String completeUrl) {
        Intent intent = getLaunchIntentWithCompleteUrl(context, completeUrl, SimpleWebActivity.class);
        ActivityUtils.startActivity(intent);
    }

    public static void launchWithCompleteUrl(Context context, String completeUrl, Class<? extends SimpleWebActivity> cls) {
        Intent intent = getLaunchIntentWithCompleteUrl(context, completeUrl, cls);
        ActivityUtils.startActivity(intent);
    }

    public static void launchWithRouteData(Context context, String routePageData) {
        Intent intent = getLaunchIntentWithRouteData(context, routePageData, SimpleWebActivity.class);
        ActivityUtils.startActivity(intent);
    }

    public static void launchWithRouteData(Context context, String routePageData, Class<? extends SimpleWebActivity> cls) {
        Intent intent = getLaunchIntentWithRouteData(context, routePageData, cls);
        ActivityUtils.startActivity(intent);
    }

    @NonNull
    public static Intent getLaunchIntentWithPartUrl(Context context, String urlContentPart) {
        Intent intent = new Intent(context, SimpleWebActivity.class);
        intent.putExtra(KEY_URL_CONTENT_PART, urlContentPart);
        return intent;
    }

    public static Intent getLaunchIntentWithCompleteUrl(Context context, String completeUrl) {
        return getLaunchIntentWithCompleteUrl(context, completeUrl, SimpleWebActivity.class);
    }

    public static Intent getLaunchIntentWithCompleteUrl(Context context, String completeUrl, Class<? extends SimpleWebActivity> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(KEY_URL_COMPLETE, completeUrl);
        return intent;
    }

    public static Intent getLaunchIntentWithRouteData(Context context, String routePageData) {
        return getLaunchIntentWithRouteData(context, routePageData, SimpleWebActivity.class);
    }

    public static Intent getLaunchIntentWithRouteData(Context context, String routePageData, Class<? extends SimpleWebActivity> cls) {
        String urlContent = H5Uri.NATIVE_ROUTE
                .replace("{pageData}", routePageData)
                .replace("{token}", getToken());
        String completeUrl = BuildConfig.BASE_H5_URL + urlContent;
        return getLaunchIntentWithCompleteUrl(context, completeUrl, cls);
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
