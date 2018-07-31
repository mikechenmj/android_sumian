package com.sumian.hw.common.util;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public final class NumberUtil {

    public static String formatHour(Integer second) {
        return second == null ? "-" : TimeUtil.calculateHour(second);
    }

    public static String formatMin(Integer second) {
        return second == null ? "-" : TimeUtil.calculateMin(second);
    }

    public static String second2Min(Integer second) {
        return second == null ? "--" : TimeUtil.second2Min(second);
    }

    public static String formatDuration(Integer second) {
        String text;
        if (second == null || second == 0) {
            text = "----";
        } else {
            if (second > 60 * 60) {
                text = String.format(Locale.getDefault(), "%s%s%s%s", formatHour(second), "小时", formatMin(second), "分");
            } else {
                text = String.format(Locale.getDefault(), "%s%s", formatMin(second), "分");
            }
        }
        return text;
    }

    public static int formatVersionCode(String version) {
        if (TextUtils.isEmpty(version)) {
            return 0;
        } else {
            version = version.replace(".", "");
            return Integer.parseInt(version);
        }
    }
}
