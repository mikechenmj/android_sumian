package com.sumian.sleepdoctor.chat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sm
 * on 2018/1/29.
 * desc:
 */

public final class TimeUtils {

    private static ThreadLocal<SimpleDateFormat> mDateFormatMsgThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        }
    };

    public static String formatMsgTime(long unixTime) {
        return mDateFormatMsgThreadLocal.get().format(new Date(unixTime));
    }
}
