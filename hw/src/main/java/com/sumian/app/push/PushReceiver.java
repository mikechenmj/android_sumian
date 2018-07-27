package com.sumian.app.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.app.app.AppManager;
import com.sumian.app.improve.main.HomeActivity;
import com.sumian.app.log.LogManager;
import com.sumian.app.utils.JsonUtil;
import com.sumian.app.utils.NotificationUtil;

/**
 * Created by jzz
 * on 2017/11/28.
 * <p>
 * desc:
 */

public class PushReceiver extends BroadcastReceiver {

    private static final String ACTION_PUSH = "com.tech.sumian.action.PUSH";
    private static final String EXTRA_DATA_JSON = "com.avos.avoscloud.Data";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case ACTION_PUSH:
                onPush(context, intent);
                break;
            default:
                break;
        }
    }

    private void onPush(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String json = bundle.getString(EXTRA_DATA_JSON);
        LogManager.appendPhoneLog("PushReport json: " + json);
        PushData pushData = JsonUtil.fromJson(json, PushData.class);
        if (pushData == null) {
            return;
        }

        // 过滤非当前用户的推送
        String scheme = pushData.getScheme();
        if (!isUserIdValid(scheme)) {
            return;
        }
        String alert = pushData.getAlert();
        if (TextUtils.isEmpty(alert)) {
            LogUtils.d("alert intent is null");
            return;
        }
        Intent notificationIntent = SchemeUtil.Companion.resolveScheme(context, scheme);
        if (notificationIntent == null) {
            LogUtils.d("push intent is null");
            return;
        }
        NotificationUtil.Companion.showNotification(context, alert, notificationIntent);
    }

    private boolean isUserIdValid(String scheme) {
        String userIdStr = SchemeUtil.Companion.getUserIdFromScheme(scheme);
        if (TextUtils.isEmpty(userIdStr)) {
            return false;
        }
        long pushUserId = Long.valueOf(userIdStr);
        long id = AppManager.getAccountModel().getUserInfo().getId();
        if (pushUserId != id) {
            LogUtils.d("user id not equal");
            return false;
        }
        return true;
    }
}
