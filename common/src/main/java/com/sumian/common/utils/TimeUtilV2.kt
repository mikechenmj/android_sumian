package com.sumian.common.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 16:28
 * desc   :
 * version: 1.0
 */
class TimeUtilV2 {
    companion object {
        fun formatDate(pattern: String, timeInMillis: Long): String {
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            return format.format(Date(timeInMillis))
        }

        fun formatTimeYYYYMMDD(time: Long): String {
            return formatDate("yyyy.MM.dd", time)
        }

        fun formatTimeYYYYMMDD_HHMM(time: Long): String {
            return formatDate("yyyy.MM.dd HH:mm", time)
        }

        fun formatTimeYYYYMMDD_HHMM(unixTime: Int): String {
            return formatDate("yyyy.MM.dd HH:mm", unixTime * 1000L)
        }

        fun getCalendar(time: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.time = Date(time)
            return calendar
        }

        /**
         * @param time 时间戳
         * @return 入参时间当天00:00的时间戳
         */
        fun getStartTimeOfTheDay(time: Long): Long {
            val calendar = getCalendar(time)
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE), 0, 0, 0)
            return calendar.timeInMillis
        }

        /**
         * return t1 距离 t2 的天数
         */
        fun getDayDistance(t1: Long, t2: Long): Long {
            return (getStartTimeOfTheDay(t1) - getStartTimeOfTheDay(t2)) / DateUtils.DAY_IN_MILLIS
        }

        /**
         * 获取入参时间戳当天0点Calendar
         *
         * @param time 某个时间戳
         * @return 当天0点Calendar
         */
        fun getDayStartCalendar(time: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time - time % 1000
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0)
            return calendar
        }

        fun getStartDayOfMonthCalendar(time: Long): Calendar {
            val calendar = getDayStartCalendar(time)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            return calendar
        }

        /**
         * 获取入参时间戳当天0点时间
         *
         * @param time 某个时间戳
         * @return 当天0点时间戳
         */
        fun getDayStartTime(time: Long): Long {
            return getDayStartCalendar(time).timeInMillis
        }

        /**
         * 获取入参时间戳所在周 周日0点时间
         *
         * @param time 某个时间戳
         * @return 周日0点时间戳
         */
        fun getWeekStartDayTime(time: Long): Long {
            val calendar = getDayStartCalendar(time)
            calendar.set(Calendar.DAY_OF_WEEK, 1)
            return calendar.timeInMillis
        }

        /**
         * 获取入参时间戳所在周 周六0点时间
         *
         * @param time 某个时间戳
         * @return 周六0点时间戳
         */
        fun getWeekEndDayTime(time: Long): Long {
            val calendar = getDayStartCalendar(time)
            calendar.set(Calendar.DAY_OF_WEEK, 7)
            return calendar.timeInMillis
        }

        fun getMonthStartDayTime(time: Long): Long {
            val calendar = getDayStartCalendar(time)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            return calendar.timeInMillis
        }

        fun getMonthEndDayTime(time: Long): Long {
            val calendar = getDayStartCalendar(time)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            return calendar.timeInMillis
        }

        fun isAtStartOfWeek(time: Long): Boolean {
            return getDayStartCalendar(time).get(Calendar.DAY_OF_WEEK) == 1
        }

        fun isAtEndOfWeek(time: Long): Boolean {
            return getDayStartCalendar(time).get(Calendar.DAY_OF_WEEK) == 7
        }

        fun isAtStartOfMonth(time: Long): Boolean {
            return getDayStartCalendar(time).get(Calendar.DAY_OF_MONTH) == 1
        }

        fun isAtEndOfMonth(time: Long): Boolean {
            val calendar = getDayStartCalendar(time)
            return calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        /**
         * 给时间偏移rollDays天
         *
         * @param calendar 需要偏移的calendar
         * @param rollDays 偏移天数，可正可负
         */
        fun rollDay(calendar: Calendar, rollDays: Int) {
            val time = calendar.timeInMillis + DateUtils.DAY_IN_MILLIS * rollDays
            calendar.time = Date(time)
        }

        fun createDays(time: Long, dayCount: Int, increase: Boolean, include: Boolean): ArrayList<Long> {
            val calendar = getDayStartCalendar(time)
            val list = ArrayList<Long>()
            for (i in 0..dayCount) {
                list.add(calendar.timeInMillis)
                rollDay(calendar, if (increase) 1 else -1)
            }
            list.removeAt(if (include) list.size - 1 else 0)
            list.sort()
            return list
        }

        fun createMonths(startMonth: Long, count: Int, increase: Boolean, include: Boolean): List<Long> {
            val list = ArrayList<Long>()
            val calendar = getStartDayOfMonthCalendar(startMonth)
            for (i in 0..count) {
                list.add(calendar.getTimeInMillis())
                val month = calendar.get(Calendar.MONTH)
                if (increase && month == 11) {
                    calendar.roll(Calendar.YEAR, 1)
                } else if (!increase && month == 0) {
                    calendar.roll(Calendar.YEAR, -1)
                }
                calendar.roll(Calendar.MONTH, if (increase) 1 else -1)
            }
            list.removeAt(if (include) list.size - 1 else 0)
            list.sort()
            return list
        }
    }
}