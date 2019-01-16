package com.sumian.sd.diary.fillsleepdiary.bean

import android.telephony.PhoneNumberUtils.formatNumber
import android.text.format.DateUtils
import com.sumian.common.utils.TimeUtilV2
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/13 18:03
 * desc   : 详情参考v1.14.0需求文档
 * version: 1.0
 */
class SleepTimeData {
    companion object {
        const val FIVE_MIN = DateUtils.MINUTE_IN_MILLIS * 5
        val YESTERDAY_18_00 = TimeUtilV2.parseTimeStr("18:00")
        val TODAY_00_00 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("00:00")
        val TODAY_17_55 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("17:55")
        val TODAY_23_50 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("23:50")
        val TODAY_23_55 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("23:55")
        val DEFAULT_INIT_TIME_OF_SLEEP_TIME = TimeUtilV2.parseTimeStr("23:00")
        val DEFAULT_INIT_TIME_OF_WAKEUP_TIME = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("7:00")

        fun parseHHmmToTime(hour: Int, minute: Int, addDay: Boolean = false): Long {
            return TimeUtilV2.parseTimeStr("HH:mm", "$hour:$minute") + DateUtils.DAY_IN_MILLIS * (if (addDay) 1 else 0)
        }

        fun formatNumber(number: Int): String {
            return String.format("%02d", number)
        }
    }

    val mTimeArray = longArrayOf(
            DEFAULT_INIT_TIME_OF_SLEEP_TIME,
            YESTERDAY_18_00,
            DEFAULT_INIT_TIME_OF_WAKEUP_TIME,
            TODAY_00_00
    )

    private fun indexTooBigException() = IllegalArgumentException("index must < 4")

    fun getStartAndEndTime(index: Int): Pair<Long, Long> {
        return when (index) {
            0 -> Pair(YESTERDAY_18_00, TODAY_17_55)
            1 -> Pair(mTimeArray[0], TODAY_23_50)
            2 -> Pair(Math.max(mTimeArray[1] + FIVE_MIN, TODAY_00_00), TODAY_23_55)
            3 -> Pair(mTimeArray[2], TODAY_23_55)
            else -> throw indexTooBigException()
        }
    }

    private fun getCurrentOrMinValidTime(index: Int): Long {
        return when (index) {
            0 -> mTimeArray[0]
            1 -> if (mTimeArray[1] >= mTimeArray[0]) mTimeArray[1] else mTimeArray[0]
            2 -> if (mTimeArray[2] >= mTimeArray[1] + FIVE_MIN) mTimeArray[2] else mTimeArray[1] + FIVE_MIN
            3 -> if (mTimeArray[3] >= mTimeArray[2]) mTimeArray[3] else mTimeArray[2]
            else -> throw indexTooBigException()
        }
    }

    fun createHoursByIndex(index: Int): Array<String?> {
        val startAndEndTime = getStartAndEndTime(index)
        return createHoursByStartAndEndTime(startAndEndTime.first, startAndEndTime.second)
    }

    fun createMinutesByIndexAndHour(index: Int, hour: Int, isToday: Boolean = false): Array<String?> {
        val startMinute: Int
        val endMinute: Int
        val time: Long
        when (index) {
            0 -> {
                startMinute = 0
                endMinute = 55
            }
            1 -> {
                time = getTimeByHour(hour, isToday)
                startMinute = getZeroOrPreviousMinute(index, time)
                endMinute = if (isInTheSameHour(TODAY_23_50, time)) {
                    50
                } else {
                    55
                }
            }
            2 -> {
                time = getTimeByHour(hour)
                startMinute = getZeroOrPreviousMinute(index, time, true)
                endMinute = 55
            }
            3 -> {
                time = getTimeByHour(hour)
                startMinute = getZeroOrPreviousMinute(index, time)
                endMinute = 55
            }
            else -> throw indexTooBigException()
        }
        return createMinutesByStartAndEndTime(startMinute, endMinute)
    }

    private fun getTimeByHour(hour: Int, nextDay: Boolean = true) =
            DateUtils.DAY_IN_MILLIS * (if (nextDay) 1 else 0) + TimeUtilV2.parseTimeStr("$hour:00")

    private fun getZeroOrPreviousMinute(index: Int, time: Long, addFiveMinutesIfNeed: Boolean = false): Int {
        return if (isInTheSameHour(mTimeArray[index - 1], time)) {
            getMinuteOfTime(mTimeArray[index - 1]) + if (addFiveMinutesIfNeed) 5 else 0
        } else {
            0
        }
    }

    private fun createHoursByStartAndEndTime(startTime: Long, endTime: Long): Array<String?> {
        val list = ArrayList<String>()
        val formatStartTime = startTime - DateUtils.MINUTE_IN_MILLIS * getMinuteOfTime(startTime)
        val formatEndTime = endTime - DateUtils.MINUTE_IN_MILLIS * getMinuteOfTime(endTime)
        for (i in formatStartTime..formatEndTime step DateUtils.HOUR_IN_MILLIS) {
            list.add(formatNumber(getHourOfTime(i)))
        }
        return listToArray(list)
    }

    private fun createMinutesByStartAndEndTime(startMinutes: Int, endMinutes: Int): Array<String?> {
        val list = ArrayList<String>()
        for (i in startMinutes..endMinutes step 5) {
            list.add(formatNumber(i))
        }
        return listToArray(list)
    }

    private fun listToArray(list: ArrayList<String>): Array<String?> {
        val array = arrayOfNulls<String>(list.size)
        list.toArray(array)
        return array
    }

    fun getHourOfTime(time: Long): Int {
        return TimeUtilV2.getCalendar(time).get(Calendar.HOUR_OF_DAY)
    }

    private fun getMinuteOfTime(time: Long): Int {
        return TimeUtilV2.getCalendar(time).get(Calendar.MINUTE)
    }

    private fun isInTheSameHour(t0: Long, t1: Long): Boolean {
        return TimeUtilV2.isInTheSame(t0, t1, Calendar.HOUR_OF_DAY)
    }

    /**
     * 设置time\[i]后，如果后续时间变为非法值，自动赋值为最小合法值
     */
    fun setTime(index: Int, time: Long) {
        mTimeArray[index] = time
    }

    /**
     * 刷新 时间 index，使其合法
     */
    fun updateTimeInNeed(index: Int) {
        val currentOrMinValidTime = getCurrentOrMinValidTime(index)
        if (currentOrMinValidTime > mTimeArray[index]) {
            mTimeArray[index] = currentOrMinValidTime
        }
    }

    fun setTime(index: Int, hour: Int, minute: Int) {
        val addDay = when (index) {
            0 -> hour < 18
            1 -> if (mTimeArray[0] < TODAY_00_00) {
                hour < 18
            } else {
                true
            }
            2, 3 -> true
            else -> false
        }
        val time = parseHHmmToTime(hour, minute, addDay)
        setTime(index, time)
    }

    fun getTime(index: Int): Long {
        return mTimeArray[index]
    }
}