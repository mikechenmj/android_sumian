package com.sumian.hw.common.helper;

import android.app.NotificationManager;
import android.content.Context;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/4 13:36
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationHelper {

    public static void clearNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }
}
