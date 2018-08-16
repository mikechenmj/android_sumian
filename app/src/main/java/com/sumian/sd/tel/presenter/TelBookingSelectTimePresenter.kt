package com.sumian.sd.tel.presenter

import com.sumian.sd.tel.bean.BookingTime
import com.sumian.sd.tel.contract.TelBookingSelectTimeContract
import java.util.*

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
class TelBookingSelectTimePresenter private constructor(view: TelBookingSelectTimeContract.View) : TelBookingSelectTimeContract.Presenter {

    private var mView: TelBookingSelectTimeContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: TelBookingSelectTimeContract.View): TelBookingSelectTimeContract.Presenter {
            return TelBookingSelectTimePresenter(view)
        }

    }

    override fun calculateDate(currentTelBookingUnixTime: Int) {
        val showBookingTimes = calculateShowBookingTimes()
        val dates = mutableListOf<String>()
        var isCheckDatePosition = 0
        //计算出日期
        showBookingTimes
                .forEachIndexed { index, bookingTime ->
                    val unixTime = bookingTime.unixTime
                    if (isSameDay(unixTime, currentTelBookingUnixTime)) {
                        isCheckDatePosition = index
                    }
                    val formatDate = bookingTime.formatDate()
                    dates.add(formatDate)
                }
        this.mView?.transformOneDisplayedValues(isCheckDatePosition, "", dates.toTypedArray())
    }

    override fun calculateHour(currentHour: Int) {
        val hours = mutableListOf<String>()
        var isCheckDatePosition = 0
        var startHour = 19 //19-22
        for (i in 0..4) {
            startHour += i
            if (startHour == currentHour) {
                isCheckDatePosition = i
            }
            hours.add(startHour.toString())
        }

        this.mView?.transformTwoDisplayedValues(isCheckDatePosition, ":", hours.toTypedArray())
    }

    override fun calculateMinute(currentMinute: Int) {
        val minutes = mutableListOf<String>()
        var isCheckDatePosition = 0
        for (i in 0..59) { //00-59
            if (i == currentMinute) {
                isCheckDatePosition = i
            }
            minutes.add(i.toString())
        }
        this.mView?.transformThreeDisplayedValues(isCheckDatePosition, "", minutes.toTypedArray())
    }

    private fun calculateShowBookingTimes(): MutableList<BookingTime> {
        val calendar = Calendar.getInstance()
        val showBookingTimes = mutableListOf<BookingTime>()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        calendar.set(year, month, date, 0, 0, 0)

        val tmpCurrentTimeInMillis = calendar.timeInMillis//当前日期的起始时间戳 往之后推28天

        val segmentUnixTime = 60 * 60 * 24 //24小时间隔时间

        //val next28TimeInMillis = tmpCurrentTimeInMillis * 60 * 60 * 24 * 1000L//28天之后的时间戳

        for (i in 0..27) {
            val tmpTimeInMillis = (tmpCurrentTimeInMillis / 1000L).toInt() + i * segmentUnixTime
            if (isWeekends(tmpTimeInMillis)) {
                continue
            }
            val bookingTime = BookingTime()
            bookingTime.unixTime = tmpTimeInMillis
            showBookingTimes.add(bookingTime)
        }
        return showBookingTimes
    }

    private fun isSameDay(srcUnixTime: Int, desUnixTime: Int): Boolean {
        return formatTodayUnixTime(srcUnixTime) == formatTodayUnixTime(desUnixTime)
    }

    private fun formatTodayUnixTime(unixTime: Int): Long {
        val calendar = Calendar.getInstance()
        if (unixTime != 0) {
            calendar.timeInMillis = unixTime * 1000L
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        calendar.set(year, month, date, 0, 0, 0)
        return calendar.timeInMillis
    }

    private fun isWeekends(unixTime: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime * 1000L
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
    }
}