package com.sumian.sd.diary.fillsleepdiary

import android.text.format.DateUtils
import com.sumian.common.utils.TimeUtilV2
import java.lang.IllegalArgumentException
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
        const val FIVE_MINUTES_IN_MILLIS = DateUtils.MINUTE_IN_MILLIS * 5
        val FIRST_DAY_18_00 = TimeUtilV2.parseTimeStr("18:00")
        val SECOND_DAY_00_00 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("00:00")
        val SECOND_DAY_17_55 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("17:55")
        val SECOND_DAY_23_50 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("23:50")
        val SECOND_DAY_23_55 = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("23:55")
        val DEFAULT_INIT_TIME_OF_SLEEP_TIME = TimeUtilV2.parseTimeStr("23:00")
        val DEFAULT_INIT_TIME_OF_WAKEUP_TIME = DateUtils.DAY_IN_MILLIS + TimeUtilV2.parseTimeStr("7:00")
    }

    private val mTimeArray = longArrayOf(DEFAULT_INIT_TIME_OF_SLEEP_TIME, 0, DEFAULT_INIT_TIME_OF_WAKEUP_TIME, 0)
    private fun indexTooBigException() = IllegalArgumentException("index must < 4")

    fun getStartAndEndTime(index: Int): Pair<Long, Long> {
        return when (index) {
            0 -> Pair(FIRST_DAY_18_00, SECOND_DAY_17_55)
            1 -> if (mTimeArray[0] < SECOND_DAY_00_00) Pair(mTimeArray[0], SECOND_DAY_17_55) else Pair(mTimeArray[0], SECOND_DAY_23_50)
            2 -> if (mTimeArray[1] < SECOND_DAY_00_00) Pair(SECOND_DAY_00_00, SECOND_DAY_23_55) else Pair(mTimeArray[1] + FIVE_MINUTES_IN_MILLIS, SECOND_DAY_23_55)
            3 -> Pair(mTimeArray[2], SECOND_DAY_23_55)
            else -> throw indexTooBigException()
        }
    }

    private fun getCurrentOrMinValidTime(index: Int): Long {
        return when (index) {
            0 -> mTimeArray[0]
            1 -> if (mTimeArray[1] >= mTimeArray[0]) mTimeArray[1] else mTimeArray[0]
            2 -> if (mTimeArray[2] >= mTimeArray[1] + FIVE_MINUTES_IN_MILLIS) mTimeArray[2] else mTimeArray[1] + FIVE_MINUTES_IN_MILLIS
            3 -> if (mTimeArray[3] >= mTimeArray[2]) mTimeArray[3] else mTimeArray[2]
            else -> throw indexTooBigException()
        }
    }

    fun createHoursByIndex(index: Int): Array<String?> {
        val startAndEndTime = getStartAndEndTime(index)
        return createHoursByStartAndEndTime(startAndEndTime.first, startAndEndTime.second)
    }

    fun createMinutesByIndexAndHour(index: Int, hour: Int): Array<String?> {
        val startMinute: Int
        val endMinute: Int
        val time: Long
        when (index) {
            0 -> {
                startMinute = 0
                endMinute = 55
            }
            1 -> {
                time = if (mTimeArray[index - 1] < SECOND_DAY_00_00) {
                    getNextDayTimeByHour(hour, hour < 18)
                } else {
                    getNextDayTimeByHour(hour)
                }
                startMinute = getZeroOrPreviewMinute(index, time)
                endMinute = if (isInTheSameHour(SECOND_DAY_23_50, time)) {
                    50
                } else {
                    55
                }
            }
            2 -> {
                time = getNextDayTimeByHour(hour)
                startMinute = getZeroOrPreviewMinute(index, time, true)
                endMinute = 55
            }
            3 -> {
                time = getNextDayTimeByHour(hour)
                startMinute = getZeroOrPreviewMinute(index, time)
                endMinute = 55
            }
            else -> throw indexTooBigException()
        }
        return createMinutesByStartAndEndTime(startMinute, endMinute)
    }

    private fun getNextDayTimeByHour(hour: Int, nextDay: Boolean = true) =
            DateUtils.DAY_IN_MILLIS * (if (nextDay) 1 else 0) + TimeUtilV2.parseTimeStr("$hour:00")

    private fun getZeroOrPreviewMinute(index: Int, time: Long, addFiveMinutesIfNeed: Boolean = false): Int {
        return if (isInTheSameHour(mTimeArray[index - 1], time)) {
            getMinuteOfTime(mTimeArray[index - 1]) + if (addFiveMinutesIfNeed) 5 else 0
        } else {
            0
        }
    }

    fun createHoursByStartAndEndTime(startTime: Long, endTime: Long): Array<String?> {
        val list = ArrayList<String>()
        for (i in startTime..endTime step DateUtils.HOUR_IN_MILLIS) {
            list.add(getHourOfTime(i).toString())
        }
        return listToArray(list)
    }

    private fun createMinutesByStartAndEndTime(startMinutes: Int, endMinutes: Int): Array<String?> {
        val list = ArrayList<String>()
        for (i in startMinutes..endMinutes step 5) {
            list.add(i.toString())
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
        if (index < mTimeArray.size - 1) {
            for (i in index + 1 until mTimeArray.size) {
                val currentOrMinValidTime = getCurrentOrMinValidTime(i)
                if (currentOrMinValidTime > mTimeArray[i]) {
                    mTimeArray[i] = currentOrMinValidTime
                }
            }
        }
    }

    fun setTime(index: Int, hour: Int, minute: Int) {
//        var time = parseHHmmToTime()
    }

    fun getTime(index: Int): Long {
        return mTimeArray[index]
    }

    fun parseHHmmToTime(hour: Int, minute: Int, addDay: Boolean = false): Long {
        return TimeUtilV2.parseTimeStr("HH:mm", "$hour:$minute") + DateUtils.DAY_IN_MILLIS * (if (addDay) 1 else 0)
    }

}