package com.sumian.sd.common.utils;


import android.text.format.DateUtils;

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

    private static ThreadLocal<SimpleDateFormat> mDateFormatMinuteThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
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

    public static String formatYYYYMMDDHHMM(int unixTime) {
        return mDateFormatMinuteThreadLocal.get().format(new Date(unixTime * 1000L));
    }

    public static String formatYYYYMMDD(int unixTime) {
        Date date = new Date();
        date.setTime(unixTime * 1000L);
        return mDateFormatLineThreadLocal.get().format(date);
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

    public static String formatDate(int timeInSecond) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timeInSecond * 1000L);
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

    public static boolean isInTheSameDay(long t1, long t2) {
        Calendar c1 = getCalendar(t1);
        Calendar c2 = getCalendar(t2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
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
    public static long getWeekStartDayTime(long time) {
        Calendar calendar = getDayStartCalendar(time);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
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
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return calendar.getTimeInMillis();
    }

    public static long getMonthStartDayTime(long time) {
        Calendar calendar = getDayStartCalendar(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static long getMonthEndDayTime(long time) {
        Calendar calendar = getDayStartCalendar(time);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTimeInMillis();
    }

    public static boolean isAtStartOfWeek(long time) {
        return getDayStartCalendar(time).get(Calendar.DAY_OF_WEEK) == 1;
    }

    public static boolean isAtEndOfWeek(long time) {
        return getDayStartCalendar(time).get(Calendar.DAY_OF_WEEK) == 7;
    }

    public static boolean isAtStartOfMonth(long time) {
        return getDayStartCalendar(time).get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static boolean isAtEndOfMonth(long time) {
        Calendar calendar = getDayStartCalendar(time);
        return calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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
        Calendar calendar = getCalendar(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE), 0, 0, 0);
        return calendar.getTimeInMillis();
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
        Calendar c1 = getCalendar(t1);
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

    public static String formatDate(String pattern, long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(timeInMillis));
    }

    public static int getDayOfMonth(long timeInMillis) {
        Calendar calendar = getCalendar(timeInMillis);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getHourMinuteStringFromSecond(int second, String hourString, String minuteString) {
//        if (second == 0) {
//            return "——";
//        }
        int hour = getHourFromSecond(second);
        int min = getMinuteFromSecond(second);
        String hourMinuteStr;
        StringBuilder stringBuilder = new StringBuilder();
        if (hour != 0) {
            stringBuilder.append(hour)
                    .append(hourString);
        }
        stringBuilder.append(min)
                .append(minuteString);
        hourMinuteStr = stringBuilder.toString();
        return hourMinuteStr;
    }

    public static String getHourMinuteStringFromSecondInZh(int second) {
        return getHourMinuteStringFromSecond(second, "小时", "分钟");
    }

    public static String getHourMinuteStringFromSecondInEn(int second) {
        return getHourMinuteStringFromSecond(second, "h", "min");
    }

    public static List<Long> createMonthTimes(long startMonth, int count, boolean includeStartMonth) {
        List<Long> list = new ArrayList<>();
        Calendar calendar = TimeUtil.getStartDayOfMonth(startMonth);
        if (includeStartMonth) {
            list.add(calendar.getTimeInMillis());
        }
        for (int i = 0; i < count; i++) {
            if (calendar.get(Calendar.MONTH) == 0) {
                calendar.roll(Calendar.YEAR, -1);
            }
            calendar.roll(Calendar.MONTH, -1);
            list.add(calendar.getTimeInMillis());
        }
        if (includeStartMonth) {
            list.remove(list.size() - 1);
        }
        return list;
    }

    public static List<String> formatDateList(List<Long> timeList) {
        List<String> list = new ArrayList<>();
        for (long l : timeList) {
            String date = TimeUtil.formatDate("yyyy/MM", l);
            list.add(date);
        }
        return list;
    }

    public static int getUnixTimeFromHourAndMinute(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int getMonthDistance(long time1, long time2) {
        Calendar c1 = getCalendar(time1);
        Calendar c2 = getCalendar(time2);
        return (c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
    }
}
