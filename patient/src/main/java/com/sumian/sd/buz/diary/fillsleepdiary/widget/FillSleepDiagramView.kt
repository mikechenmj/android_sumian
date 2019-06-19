package com.sumian.sd.buz.diary.fillsleepdiary.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import kotlinx.android.synthetic.main.fill_sleep_diagram.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 14:15
 * desc   :
 * version: 1.0
 */
class FillSleepDiagramView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private val mFocusedTvColor = ColorCompatUtil.getColor(context, R.color.b3_color)
    private val mUnfocusedTvColor = ColorCompatUtil.getColor(context, R.color.t2_color)
    var mNoSleep = false

    init {
        LayoutInflater.from(context).inflate(R.layout.fill_sleep_diagram, this, true)
    }

    fun setTimeAndCurrentIndex(timeArray: LongArray, currentIndex: Int) {
        if (timeArray.size != 4) {
            throw IllegalArgumentException("timeArray must have 4 elements")
        }
        if (currentIndex >= 4) {
            throw IllegalArgumentException("currentIndex must < 4")
        }
        for (i in 0 until timeArray.size) {
            val topTv = getTvTopByIndex(i)
            val bottomTv = getTvBottomByIndex(i)
            topTv.text =
                    if (i <= currentIndex) {
                        if (mNoSleep && (i == 1 || i == 2)) "—" else getFormatTime(timeArray[i])
                    } else {
                        context.getString(R.string.question_mark_x3)
                    }
            val textColor = if (i == currentIndex) mFocusedTvColor else mUnfocusedTvColor
            topTv.setTextColor(textColor)
            topTv.setTypeface(topTv.typeface, if (i <= currentIndex) Typeface.BOLD else Typeface.NORMAL)
            bottomTv.setTextColor(textColor)
        }
        updateBgColor(currentIndex)
    }

    private fun getTvTopByIndex(index: Int): TextView {
        return when (index) {
            0 -> tv_sleep_time
            1 -> tv_fall_asleep_time
            2 -> tv_wakeup_time
            3 -> tv_get_up_time
            else -> tv_sleep_time
        }
    }

    private fun getTvBottomByIndex(index: Int): TextView {
        return when (index) {
            0 -> tv_sleep
            1 -> tv_fall_asleep
            2 -> tv_wakeup
            3 -> tv_get_up
            else -> tv_sleep
        }
    }

    private fun updateBgColor(index: Int) {
        bg_0.setBackgroundColor(
                ColorCompatUtil.getColor(
                        context,
                        if (index >= 1) R.color.sleep_status_wake else R.color.l1_color
                )
        )
        bg_1.setBackgroundColor(
                ColorCompatUtil.getColor(
                        context,
                        if (index >= 2)
                            if (mNoSleep) R.color.sleep_status_wake else R.color.sleep_status_sleep
                        else R.color.l1_color
                )
        )
        bg_2.setBackgroundColor(
                ColorCompatUtil.getColor(
                        context,
                        if (index >= 3) R.color.sleep_status_wake else R.color.l1_color
                )
        )
    }

    private fun getFormatTime(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
        return simpleDateFormat.format(Date(time))
    }
}