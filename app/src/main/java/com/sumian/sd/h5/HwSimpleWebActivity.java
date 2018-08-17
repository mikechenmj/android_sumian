package com.sumian.sd.h5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.sumian.sd.R;
import com.sumian.sd.base.ActivityLauncher;
import com.sumian.sd.base.SdBaseWebViewActivity;

public class HwSimpleWebActivity extends SimpleWebActivity {

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setIsDarkTheme(true);
    }

    public static void launch(Context context, String urlContentPart) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL_CONTENT_PART, urlContentPart);
        Intent intent = new Intent(context, HwSimpleWebActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void launchWithCompleteUrl(Context context, String completeUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL_COMPLETE, completeUrl);
        Intent intent = new Intent(context, HwSimpleWebActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
