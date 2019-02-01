package com.sumian.sddoctor.service.advisory.onlinereport;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.sumian.common.utils.JsonUtil;
import com.sumian.sddoctor.h5.SimpleWebActivity;

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
        return SimpleWebActivity.getLaunchIntentWithRouteData(context, JsonUtil.Companion.toJson(page), OnlineReportDetailActivity.class);
    }

}
