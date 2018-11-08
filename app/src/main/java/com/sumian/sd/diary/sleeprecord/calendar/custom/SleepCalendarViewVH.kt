package com.sumian.sd.diary.sleeprecord.calendar.custom

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarViewVH

@Suppress("DEPRECATION")
/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/5/29 15:59
 * desc   :
 * version: 1.0
</pre> *
 */
class SleepCalendarViewVH private constructor(itemView: View) : CalendarViewVH(itemView) {

    override fun setDay(day: Int, dayType: Int, secondType: Int) {
        val text = if (day > 0) day.toString() else ""
        mTextView.text = text
        mTextView.setTextColor(getTextColor(dayType, secondType))
        mTextView.background = getBgDrawable(dayType)
        mTextView.setTypeface(Typeface.DEFAULT, if (isBold(dayType)) Typeface.BOLD else Typeface.NORMAL)
        mViewBg.background = getSecondBgDrawable(secondType)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun isBold(dayType: Int): Boolean {
        return false
    }

    override fun getTextColor(dayType: Int, secondType: Int): Int {
        val textColor: Int = if (secondType == SECOND_BG_TYPE_NONE) {
            when (dayType) {
                SleepDayType.HAS_DATA, SleepDayType.NO_DATA -> R.color.t1_color
                SleepDayType.SELECT_HAS_DATA, SleepDayType.SELECT_NO_DATA -> R.color.white
                SleepDayType.FEATURE -> R.color.t1_color_40
                else -> R.color.t1_color
            }
        } else {
            R.color.white
        }
        return ColorCompatUtil.getColor(mContext, textColor)
    }

    override fun getBgDrawable(dayType: Int): Drawable? {
        val drawableRes: Int
        when (dayType) {
            SleepDayType.HAS_DATA -> drawableRes = R.drawable.ic_calendar_date
            SleepDayType.SELECT_HAS_DATA -> drawableRes = R.drawable.ic_calendar_selecteddate
            SleepDayType.SELECT_NO_DATA -> drawableRes = R.drawable.ic_calendar_selected
            else -> return null
        }
        return mContext.resources.getDrawable(drawableRes)
    }

    private fun getSecondBgDrawable(secondBgType: Int): Drawable? {
        val resId = when (secondBgType) {
            SECOND_BG_TYPE_START -> R.drawable.shape_calendar_week_start_bg
            SECOND_BG_TYPE_MIDDLE -> R.drawable.shape_calendar_week_middle_bg
            SECOND_BG_TYPE_END -> R.drawable.shape_calendar_week_end_bg
            SECOND_BG_TYPE_START_END -> R.drawable.shape_calendar_week_start_end_bg
            else -> 0
        }
        return if (resId == 0) {
            null
        } else {
            mContext.resources.getDrawable(resId)
        }
    }

    companion object {
        @JvmStatic
        val SECOND_BG_TYPE_NONE = 0
        @JvmStatic
        val SECOND_BG_TYPE_START = 1
        @JvmStatic
        val SECOND_BG_TYPE_MIDDLE = 2
        @JvmStatic
        val SECOND_BG_TYPE_END = 3
        @JvmStatic
        val SECOND_BG_TYPE_START_END = 4


        fun create(parent: ViewGroup): CalendarViewVH {
            val context = parent.context
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false)
            return SleepCalendarViewVH(inflate)
        }
    }
}
