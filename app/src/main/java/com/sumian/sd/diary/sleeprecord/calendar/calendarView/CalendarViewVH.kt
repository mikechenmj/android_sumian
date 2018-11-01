package com.sumian.sd.diary.sleeprecord.calendar.calendarView

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sumian.sd.R

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/5/29 15:59
 * desc   :
 * version: 1.0
</pre> *
 */
open class CalendarViewVH protected constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var mContext: Context = itemView.context

    val mTextView: TextView  by lazy {
        itemView.findViewById<TextView>(R.id.tv)
    }

    open fun setDay(day: Int, dayType: Int) {
        val text = if (day > 0) day.toString() else ""
        mTextView.text = text
        mTextView.setTextColor(getTextColor(dayType))
        mTextView.background = getBgDrawable(dayType)
    }

    protected open fun getTextColor(textType: Int): Int {
        return Color.DKGRAY
    }

    protected open fun getBgDrawable(textType: Int): Drawable? {
        return null
    }

    companion object {

        fun create(parent: ViewGroup): CalendarViewVH {
            val context = parent.context
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false)
            return CalendarViewVH(inflate)
        }
    }
}
