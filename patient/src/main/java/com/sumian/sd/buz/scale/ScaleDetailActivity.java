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
import com.sumian.sd.common.h5.H5Uri;
import com.sumian.sd.common.utils.EventBusUtil;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

public class ScaleDetailActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_SCALE_ID = "scale_id";
    public static final String KEY_SCALE_DISTRIBUTION_ID = "scale_distribution_id";
    private String mTitle;
    private long mScaleId;
    private long mScaleDistributionId;

    public static void launch(Context context, String title, long scaleDistributionId, long scaleId) {
        ActivityUtils.startActivity(getLaunchIntent(context, title, scaleDistributionId, scaleId));
    }

    public static Intent getLaunchIntent(Context context, String title, long scaleDistributionId, long scaleId) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putLong(KEY_SCALE_DISTRIBUTION_ID, scaleDistributionId);
        bundle.putLong(KEY_SCALE_ID, scaleId);
        Intent intent = new Intent(context, ScaleDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void initBundle(@NonNull Bundle bundle) {
        mTitle = bundle.getString(KEY_TITLE);
        mScaleId = bundle.getLong(KEY_SCALE_ID);
        mScaleDistributionId = bundle.getLong(KEY_SCALE_DISTRIBUTION_ID);
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
        String uri = H5Uri.FILL_SCALE;
        uri = uri.replace("{scale_distribution_id}", String.valueOf(mScaleDistributionId));
        if (mScaleId != 0) {
            uri = uri + "?scale_id_v2=" + mScaleId;
        }
        return uri;
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
