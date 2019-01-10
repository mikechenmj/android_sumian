package com.sumian.sd.sleepdiary

import android.text.format.DateUtils
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.diary.fillsleepdiary.bean.SleepTimeData
import org.junit.Test
import java.text.DecimalFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/13 20:49
 * desc   :
 * version: 1.0
 */
class FillDiaryTest {
    @Test
    fun testSleepTimeModel() {
        val model = SleepTimeData()
        for (minuteShift in arrayOf(-5, 0, 5)) {
//        for (minuteShift in arrayOf(0)) {
            val firstTime = SleepTimeData.TODAY_00_00 + DateUtils.MINUTE_IN_MILLIS * minuteShift
            println("T0 --> ${TimeUtilV2.formatDate("HH:mm", firstTime)}")
            model.setTime(0, firstTime)
            model.setTime(1, model.getStartAndEndTime(1).first)
            model.setTime(2, model.getStartAndEndTime(2).first)
            model.setTime(3, model.getStartAndEndTime(3).first)
            for (i in 0..3) {
                print("index: $i time:${TimeUtilV2.formatDate("HH:mm", model.getTime(i))}")
                val hours = model.createHoursByIndex(i)
                val minutesForFirstHour = model.createMinutesByIndexAndHour(i, model.getHourOfTime(model.getStartAndEndTime(i).first))
                val minutesForLastHour = model.createMinutesByIndexAndHour(i, model.getHourOfTime(model.getStartAndEndTime(i).second))
                print("\t hours: [${format(hours[0]!!.toInt())}, ${hours[hours.size - 1]}]")
                print("\t minutes for first hour: [${format(minutesForFirstHour[0]!!.toInt())}, ${minutesForFirstHour[minutesForFirstHour.size - 1]}]")
                print("\t minutes for last hour: [${format(minutesForLastHour[0]!!.toInt())}, ${minutesForLastHour[minutesForLastHour.size - 1]}]")
                println()
            }
        }

        println("${Date(SleepTimeData.parseHHmmToTime(1, 1))}")
        println("${Date(SleepTimeData.parseHHmmToTime(13, 21))}")
    }

    private fun format(num: Int): String? {
        return DecimalFormat("00").format(num)
    }

    @Test
    fun format() {
        println(format(1))
    }
}