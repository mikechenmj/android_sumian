package com.sumian.common.utils;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/6 21:31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationUtil {
    public static boolean areNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
}
