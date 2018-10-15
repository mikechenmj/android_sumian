package com.sumian.sd.onlinereport;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.hw.utils.JsonUtil;
import com.sumian.sd.h5.SimpleWebActivity;
import com.sumian.sd.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReportDetailActivity extends SimpleWebActivity {

    public static void launch(Context context, OnlineReport onlineReport) {
        Intent intent = getLaunchIntent(context, onlineReport.getId());
        ActivityUtils.startActivity(intent);
    }

    public static Intent getLaunchIntent(Context context, int id) {
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("id", id);
        Map<String, Object> page = new HashMap<>(2);
        page.put("page", "onlineReport");
        page.put("payload", payload);
        return SimpleWebActivity.getLaunchIntentWithRouteData(context, JsonUtil.toJson(page), OnlineReportDetailActivity.class);
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("analyseReport", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                MainActivity.Companion.launch(MainActivity.TAB_2, null);
            }
        });
    }
}
