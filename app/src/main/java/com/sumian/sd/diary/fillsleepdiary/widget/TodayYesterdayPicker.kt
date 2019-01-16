package com.sumian.sd.diary.fillsleepdiary.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import kotlinx.android.synthetic.main.view_today_yesterday_picker.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/14 16:15
 * desc   :
 * version: 1.0
 */
class TodayYesterdayPicker(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var mIsToday: Boolean? = null
    private val unselectedDayTvColor by lazy { ColorCompatUtil.getColor(context, R.color.l3_color) }
    private val selectedDayTvColor by lazy { ColorCompatUtil.getColor(context, R.color.b3_color) }
    private val mDayAnimator: ValueAnimator by lazy {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 500
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            val height = value * resources.getDimension(R.dimen.today_yesterday_picker_item_height)
            val layoutParams = v_day_place_holder.layoutParams
            layoutParams.height = height.toInt()
            v_day_place_holder.layoutParams = layoutParams
            val colorYesterday = ArgbEvaluator().evaluate(value, unselectedDayTvColor, selectedDayTvColor) as Int
            val colorToday = ArgbEvaluator().evaluate((1 - value), unselectedDayTvColor, selectedDayTvColor) as Int
            tv_yesterday.setTextColor(colorYesterday)
            tv_today.setTextColor(colorToday)
            bg_yesterday.backgroundTintList = ColorStateList.valueOf(colorYesterday)
            bg_today.backgroundTintList = ColorStateList.valueOf(colorToday)
        }
        animator
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_today_yesterday_picker, this, true)
    }

    fun setIsToday(isToday: Boolean) {
        if (mIsToday == null) {
            val todayColor = if (isToday) selectedDayTvColor else unselectedDayTvColor
            val yesterdayColor = if (isToday) unselectedDayTvColor else selectedDayTvColor
            tv_today.setTextColor(todayColor)
            tv_yesterday.setTextColor(yesterdayColor)
            bg_today.backgroundTintList = ColorStateList.valueOf(todayColor)
            bg_yesterday.backgroundTintList = ColorStateList.valueOf(yesterdayColor)
            mIsToday = isToday
            setDayPlaceHolderHeight(if (isToday) 0f else 1f)
            return
        }
        if (mIsToday == isToday) {
            return
        }

        if (isToday) {
            mDayAnimator.reverse()
        } else {
            mDayAnimator.start()
        }
        mIsToday = isToday
    }

    private fun setDayPlaceHolderHeight(value: Float) {
        val height = value * resources.getDimension(R.dimen.today_yesterday_picker_item_height)
        val layoutParams = v_day_place_holder.layoutParams
        layoutParams.height = height.toInt()
        v_day_place_holder.layoutParams = layoutParams
    }

    override fun onDetachedFromWindow() {
        mDayAnimator.removeAllUpdateListeners()
        mDayAnimator.cancel()
        super.onDetachedFromWindow()
    }

}