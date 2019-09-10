package com.sumian.sd.buz.scale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

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

public class ScaleDetailActivity extends SdBaseWebViewActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DISTRIBUTION_ID = "distribution_id";
    public static final String KEY_COLLECTION_ID = "collection_ID";
    public static final long INVALID_ID = -1L;
    private String mTitle;
    private long mDistributionId;
    private long mCollectionId;

    public static void launch(Context context, String title, long collectionId, long distributionId) {
        ActivityUtils.startActivity(getLaunchIntent(context, title, collectionId, distributionId));
    }

    public static Intent getLaunchIntent(Context context, String title, long collectionId, long distributionId) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putLong(KEY_COLLECTION_ID, collectionId);
        bundle.putLong(KEY_DISTRIBUTION_ID, distributionId);
        Intent intent = new Intent(context, ScaleDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void initBundle(@NonNull Bundle bundle) {
        mTitle = bundle.getString(KEY_TITLE);
        mCollectionId = bundle.getLong(KEY_COLLECTION_ID);
        mDistributionId = bundle.getLong(KEY_DISTRIBUTION_ID);
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
        String uri;
        if (mDistributionId == INVALID_ID) {
            uri = H5Uri.RELEASED_SCALE_COLLECTIONS.replace("{collection_id}", String.valueOf(mCollectionId));
        } else {
            uri = H5Uri.FILLED_SCALE_COLLECTIONS
                    .replace("{collection_id}", String.valueOf(mCollectionId))
                    .replace("{distribution_id}", String.valueOf(mDistributionId));
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
