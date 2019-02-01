package com.sumian.sd.buz.diary.sleeprecord.calendar.calendarViewWrapper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumian.sd.R
import com.sumian.sd.buz.diary.sleeprecord.calendar.calendarView.CalendarView


/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/5/29 20:30
 * desc   :
 * version: 1.0
</pre> *
 */
class CalendarWrapperVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mCalendarView: CalendarView  by lazy {
        itemView.findViewById<CalendarView>(R.id.cv)
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): CalendarWrapperVH {
            val context = parent.context
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_view_calendar_wrapper, parent, false)
            return CalendarWrapperVH(inflate)
        }
    }
}
