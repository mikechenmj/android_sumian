package com.sumian.chat.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sumian.app.R;
import com.sumian.app.main.HomeActivity;

/**
 * Created by jzz
 * on 2017/11/28.
 * <p>
 * desc:
 */

public class LeanCloudReceiver extends BroadcastReceiver {

    //private static final String TAG = LeanCloudHelper.class.getSimpleName();

    public static final String ROUTER_PATH_DAY_REPORT = "day_report";
    public static final String ROUTER_PATH_WEEK_REPORT = "week_report";
    public static final String ROUTER_PATH_MONTH_REPORT = "month_report";
    public static final String EXTRA_ROUTER_PATH = "router_path";
    public static final String EXTRA_ROUTER_PARAMETER = "router_parameter";
    private static final String ACTION_PUSH = "com.tech.sumian.action.PUSH";
    private static final String EXTRA_DATA_JSON = "com.avos.avoscloud.Data";
    private static final int PUSH_ID = 0x01;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e(TAG, "onReceive: -------->" + intent.getAction());
        String action = intent.getAction();
        //获取消息内容
        String alert;
        if (!TextUtils.isEmpty(action) && ACTION_PUSH.equals(action)) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            String json = bundle.getString(EXTRA_DATA_JSON);
            //Log.e(TAG, "onReceive: ----json--->" + json);
            JSONObject jsonObject = JSON.parseObject(json);
            alert = jsonObject.getString("alert");
            String scheme = jsonObject.getString("scheme");
            // Log.e(TAG, "onReceive: ---scheme-->" + scheme);
            if (TextUtils.isEmpty(scheme)) return;
            String[] split = scheme.split("/");
            String routerPath = split[3].substring(0, split[3].indexOf("?"));
            if (TextUtils.isEmpty(routerPath)) return;
            // Log.e(TAG, "onReceive: -------->routerPath=" + routerPath);
            String parameter = split[3].substring(split[3].indexOf("=") + 1);
            // Log.e(TAG, "onReceive: ------>p=" + parameter);

            intent = new Intent(context, HomeActivity.class);
            intent.putExtra(EXTRA_ROUTER_PATH, routerPath);
            intent.putExtra(EXTRA_ROUTER_PARAMETER, parameter);
            PendingIntent contentIntent = PendingIntent.getActivity(context, PUSH_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

            String channelId = "channel";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setTicker(alert)
                .setContentTitle(alert)
                .setContentText(alert)
                .setAutoCancel(true)
                .setOngoing(false)
                .setVisibility(1)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOnlyAlertOnce(true)
                .setNumber(3)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setDefaults(Notification.DEFAULT_ALL);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager == null) return;
                //ChannelId为"1",ChannelName为"Channel1"
                NotificationChannel channel = new NotificationChannel(channelId,
                    "push", NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true); //是否在桌面icon右上角展示小红点
                channel.setLightColor(Color.GREEN); //小红点颜色
                channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);

                notificationManager.notify(PUSH_ID, builder.build());
            } else {
                Notification notification = builder.build();
                NotificationManagerCompat.from(context).notify(PUSH_ID, notification);
            }
        }
        //  Log.e(TAG, "onReceive: ------->" + pushData.toString());

    }
}
