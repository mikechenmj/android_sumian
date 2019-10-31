package com.sumian.sd.widget.sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.sd.R
import kotlinx.android.synthetic.main.bottom_sheet_select_time_month_dd_hh_mm.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/28 15:03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SelectTimeHHmmBottomSheet(context: Context, @StringRes title: Int,
                                initHour: Int = 0, initMinute: Int = 0,
                                listener: OnTimePickedListener?) : BottomSheetDialog(context) {

    private var mMinTime = FIRST_DAY

    companion object {
        private const val TIME_IN_MILLIS_ONE_DAY = 24 * 60 * 60 * 1000L
        private const val HOUR_SIZE = 24
        private const val MINUTE_SIZE = 60
        private const val DAY_SIZE = 365
        private const val MMdd_PATTERN = "yyyy-MM-dd"
        val FIRST_DAY = getNextDay()
        const val DEFAULT_DAY = 0
        const val DEFAULT_HOUR = 9
        const val DEFAULT_MINUTE = 30

        private fun getNextDay(): Calendar {
            return Calendar.getInstance().apply { timeInMillis += TIME_IN_MILLIS_ONE_DAY }
        }
    }

    constructor(context: Context, @StringRes title: Int,
                initDay: Int, initHour: Int = 0, initMinute: Int = 0, minTime: Calendar = FIRST_DAY,
                listener: OnTimePickedListener?) : this(context, title, initHour, initMinute, listener) {
        mMinTime = minTime
        if (initDay >= 0) {
            picker_day.visibility = View.VISIBLE

            var format = SimpleDateFormat(MMdd_PATTERN)
            fun format(i: Int): String {
                var targetDateMillis = mMinTime.timeInMillis + TIME_IN_MILLIS_ONE_DAY * i
                return format.format(Date(targetDateMillis))
            }
            picker_day.refreshByNewDisplayedValues(Array(DAY_SIZE) { i ->
                var format = format(i)
                format
            })

            fun refreshHourAndMinute() {
                var minHour = mMinTime.get(Calendar.HOUR_OF_DAY)
                var minMinute = mMinTime.get(Calendar.MINUTE)
                picker_hour.refreshByNewDisplayedValues(Array(HOUR_SIZE - minHour) { i -> String.format("%02d", i + minHour) })
                picker_minute.refreshByNewDisplayedValues(Array(MINUTE_SIZE - minMinute) { i -> String.format("%02d", i + minMinute) })
            }

            picker_day.setOnValueChangedListener { _, oldVal, newVal ->
                if (newVal == initDay) {
                    refreshHourAndMinute()
                } else if (oldVal == initDay) {
                    picker_hour.refreshByNewDisplayedValues(Array(HOUR_SIZE) { i -> String.format("%02d", i) })
                    picker_minute.refreshByNewDisplayedValues(Array(MINUTE_SIZE) { i -> String.format("%02d", i) })
                }
            }
            picker_day.value = initDay
            refreshHourAndMinute()
        }
    }

    interface OnTimePickedListener {
        fun onTimePicked(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            onTimePicked(hour, minute)
        }

        fun onTimePicked(hour: Int, minute: Int)
    }

    init {
        val inflate = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_select_time_month_dd_hh_mm, null, false)
        setContentView(inflate)
        tv_title.setText(title)
        picker_hour.refreshByNewDisplayedValues(Array(HOUR_SIZE) { i -> String.format("%02d", i) })
        picker_minute.refreshByNewDisplayedValues(Array(MINUTE_SIZE) { i -> String.format("%02d", i) })
        picker_hour.value = initHour
        picker_minute.value = initMinute
        tv_confirm.setOnClickListener {
            var calendar = getPickedCalendar(picker_day.value)
            var isMinDay = mMinTime.get(Calendar.DATE) == calendar.get(Calendar.DATE)
            var minHour = if (isMinDay) mMinTime.get(Calendar.HOUR_OF_DAY) else 0
            var minMinute = if (isMinDay) mMinTime.get(Calendar.MINUTE) else 0
            listener?.onTimePicked(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), picker_hour.value + minHour, picker_minute.value + minMinute)
            dismiss()
        }
    }

    private fun getPickedCalendar(pickedIndex: Int): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = mMinTime.timeInMillis + TIME_IN_MILLIS_ONE_DAY * pickedIndex
        }
    }
}