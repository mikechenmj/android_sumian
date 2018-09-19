package com.sumian.hw.common.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.sumian.sd.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

@SuppressWarnings("ALL")
public final class TimeUtil {

    private static ThreadLocal<SimpleDateFormat> mDateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm", Locale.getDefault());
        }
    };

    private static ThreadLocal<SimpleDateFormat> mDateFormatSlashThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        }
    };

    private static ThreadLocal<SimpleDateFormat> mDateFormatLineThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }
    };

    private static ThreadLocal<SimpleDateFormat> mDateFormatFileNameThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        }
    };

    private static ThreadLocal<SimpleDateFormat> mDateFormatMsgThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        }
    };

    public static String formatMsgTime(long unixTime) {
        return mDateFormatMsgThreadLocal.get().format(new Date(unixTime));
    }


    public static String formatDate2FileName(long unixTime) {
        Date date = new Date();
        date.setTime(unixTime * 1000L);
        return mDateFormatFileNameThreadLocal.get().format(date);
    }

    public static String formatLineToday(Date date) {
        return mDateFormatLineThreadLocal.get().format(date);
    }

    public static String formatTime(int time) {
        return mDateFormatThreadLocal.get().format(new Date(time * 1000L));
    }

    public static String calculateHour(int second) {
        int hour = Math.abs(second) / (60 * 60);
        return String.format(Locale.getDefault(), "%d", hour);
    }

    public static String calculateMin(int second) {
        int hour = Math.abs(second) / (60 * 60);
        int min = (Math.abs(second) - hour * (60 * 60)) / 60;
        return String.format(Locale.getDefault(), "%02d", min);
    }

    public static String second2Min(int second) {
        return String.format(Locale.getDefault(), "%d", (Math.abs(second) / 60));
    }

    public static String formatDate(int date) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date * 1000L);
        int month = (instance.get(Calendar.MONTH) + 1);
        int day = instance.get(Calendar.DATE);
        return String.format(Locale.getDefault(), "%d月%d日", month, day);
    }

    public static String formatSlashDate(int today) {
        return mDateFormatSlashThreadLocal.get().format(new Date(today * 1000L));
    }

    public static int formatNumberTime(int time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time * 1000L);
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int minute = instance.get(Calendar.MINUTE);
        return (hour * 100 + minute);
    }

    public static String formatWeek(int date) {

        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date * 1000L);
        int week = instance.get(Calendar.DAY_OF_WEEK);
        String realWeak = "周日";
        switch (week) {
            case 1:
                realWeak = "周日";
                break;
            case 2:
                realWeak = "周一";
                break;
            case 3:
                realWeak = "周二";
                break;
            case 4:
                realWeak = "周三";
                break;
            case 5:
                realWeak = "周四";
                break;
            case 6:
                realWeak = "周五";
                break;
            case 7:
                realWeak = "周六";
                break;
        }
        return String.format(Locale.getDefault(), "%s", realWeak);
    }

    public static String formatFromTime2ToTime(int fromTime, int toTime) {
        return String.format(Locale.getDefault(), "%s%s%s", formatTime(fromTime), " - ", formatTime(toTime));
    }

    /**
     * 返回一个昨天的时间区间
     *
     * @param year  year
     * @param month month
     * @param date  date
     * @return yesterday
     */
    public static long getYesterday(int year, int month, int date) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, 0, 0, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int tempYear = calendar.get(Calendar.YEAR);
        int tempMonth = calendar.get(Calendar.MONTH);
        int tempDate = calendar.get(Calendar.DATE);

        String yesterday = String.format(Locale.getDefault(), "%d%02d%02d%d%02d", tempYear, tempMonth, tempDate, 20, 0);

        return Long.parseLong(yesterday);
    }

    /**
     * 获取入参时间戳当天0点Calendar
     *
     * @param time 某个时间戳
     * @return 当天0点Calendar
     */
    public static Calendar getDayStartCalendar(long time) {
        time = time - time % 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        return calendar;
    }

    /**
     * 获取入参时间戳当天0点时间
     *
     * @param time 某个时间戳
     * @return 当天0点时间戳
     */
    public static long getDayStartTime(long time) {
        return getDayStartCalendar(time).getTimeInMillis();
    }

    /**
     * 获取入参时间戳所在周 周日0点时间
     *
     * @param time 某个时间戳
     * @return 周日0点时间戳
     */
    public static long getStartTimeOfWeek(long time) {
        Calendar calendar = getDayStartCalendar(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.roll(Calendar.DAY_OF_WEEK, -(dayOfWeek - 1));
        return calendar.getTimeInMillis();
    }

    /**
     * 获取入参时间戳所在周 周六0点时间
     *
     * @param time 某个时间戳
     * @return 周六0点时间戳
     */
    public static long getWeekEndDayTime(long time) {
        Calendar calendar = getDayStartCalendar(time);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.roll(Calendar.DAY_OF_WEEK, 7 - dayOfWeek);
        return calendar.getTimeInMillis();
    }

    public static String formatCalendar(Calendar calendar) {
        return String.format(Locale.getDefault(),
                "%04d-%02d-%02d %02d:%02d:%02d:%02d week of day:%d",
                // yyyy-mm-dd
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                // hh:MM:ss:ms ms
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND),
                // day of week
                calendar.get(Calendar.DAY_OF_WEEK)
        );
    }

    public static Calendar getCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar;
    }

    /**
     * 给时间偏移rollDays天
     *
     * @param calendar 需要偏移的calendar
     * @param rollDays 偏移天数，可正可负
     */
    public static void rollDay(Calendar calendar, int rollDays) {
        long time = calendar.getTimeInMillis() + DateUtils.DAY_IN_MILLIS * rollDays;
        calendar.setTime(new Date(time));
    }

    /**
     * @param unixTime unix time
     * @return yyyy-MM-dd
     */
    public static String unixTimeToDateString(int unixTime) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(unixTime * 1000L));
    }

    /**
     * @param time time in millisecond
     * @return yyyy-MM-dd
     */
    public static String timeToDateString(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(time));
    }

    public static void printCalendar(Calendar calendar) {
        System.out.println(TimeUtil.formatCalendar(calendar));
    }

    public static Calendar getStartDayOfMonth(long time) {
        time = getStartTimeOfTheDay(time);
        Calendar calendar = getDayStartCalendar(time);
        String s = formatCalendar(calendar);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        rollDay(calendar, -(dayOfMonth - 1));
        s = formatCalendar(calendar);
        return calendar;
    }

    /**
     * @param time 时间戳
     * @return 入参时间当天00:00的时间戳
     */
    public static long getStartTimeOfTheDay(long time) {
        return time - time % DateUtils.DAY_IN_MILLIS;
    }

    public static int getDayCountInTheMonth(long time) {
        Calendar calendar = getCalendar(time);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public static boolean isInTheSameMonth(long t1, long t2) {
        Calendar c1 = getCalendar(t1);
        Calendar c2 = getCalendar(t2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }

    public static boolean isInTheSameDayOfYear(long t1, long t2) {
        Calendar c1 = getCalendar(t2);
        Calendar c2 = getCalendar(t2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static int getHourFromSecond(int second) {
        return second / 3600;
    }

    public static int getMinuteFromSecond(int second) {
        return second / 60 % 60;
    }

    public static CharSequence formatSleepDurationText(Context context, Integer second) {
        int numberSize = context.getResources().getDimensionPixelSize(R.dimen.font_22);
        int unitSize = context.getResources().getDimensionPixelSize(R.dimen.font_12);
        return formatSleepDurationText(second, numberSize, unitSize);
    }

    public static CharSequence formatSleepDurationText(Integer second, int numberSizeInPx, int unitSizeInPx) {
        if (second == null || second == 0) {
//            Drawable drawable = App.getAppContext().getResources().getDrawable(R.drawable.bg_text_t5);
//            CharSequence charSequence = QMUISpanHelper.generateSideIconText(false, 0, " ", drawable);
//            CharSequence concat = TextUtils.concat(charSequence, " ");
            return null;
        }

        List<SpannableString> charSequenceList = new ArrayList<>();
        CharSequence[] charSequences;
        int hour = TimeUtil.getHourFromSecond(second);
        int minute = TimeUtil.getMinuteFromSecond(second);
        charSequences = new CharSequence[4];
        if (hour > 0) {
            charSequences[0] = TextUtil.getSpannableString(Math.abs(hour), numberSizeInPx);
            charSequences[1] = TextUtil.getSpannableString("小时", unitSizeInPx);
        }
        charSequences[2] = TextUtil.getSpannableString(Math.abs(minute), numberSizeInPx);
        charSequences[3] = TextUtil.getSpannableString("分", unitSizeInPx);
        if (hour > 0) {
            return TextUtils.concat(charSequences);
        } else {
            return TextUtils.concat(charSequences[2], charSequences[3]);
        }
    }

    public static String secondToHHMM(int second) {
        int hour = second / 3600;
        int minute = (second / 60) % 60;
        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            sb.append(hour).append("小时");
        }
        sb.append(minute).append("分");
        return sb.toString();
    }
}
