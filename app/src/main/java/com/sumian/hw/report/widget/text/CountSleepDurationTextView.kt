package com.sumian.hw.report.widget.text

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.hw.common.util.TimeUtil
import com.sumian.sd.R

@Suppress("DEPRECATION")
/**
 * Created by dq
 *
 * on 2018/9/17
 *
 * desc:用于统计睡眠时间的 view   默认是横向drawable view   有 second>0 时,会自动转换 view
 */
class CountSleepDurationTextView : TextView {

    private var defaultDrawable: Drawable? = null
    private var numberTextSize: Int = 0
    private var unitTextSize = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CountSleepDurationTextView, defStyleAttr, defStyleRes)
        defaultDrawable = attributes.getDrawable(R.styleable.CountSleepDurationTextView_csdtv_default_drawable)
        numberTextSize = attributes.getDimensionPixelSize(R.styleable.CountSleepDurationTextView_csdtv_number_text_size, resources.getDimensionPixelSize(R.dimen.font_21))
        unitTextSize = attributes.getDimensionPixelSize(R.styleable.CountSleepDurationTextView_csdtv_unit_text_size, resources.getDimensionPixelSize(R.dimen.font_13))
        attributes.recycle()
    }


    /**
     * 设置统计的睡眠时间长度
     */
    fun setDuration(second: Int?) {
        text = if (second == null || second == 0) {
            //val drawable = App.getAppContext().resources.getDrawable(R.drawable.bg_text_t5)
            defaultDrawable?.setTint(currentTextColor)
            val charSequence = QMUISpanHelper.generateSideIconText(false, 0, " ", defaultDrawable)
            TextUtils.concat(charSequence, " ")
        } else {
            TimeUtil.formatSleepDurationText(second, numberTextSize, unitTextSize)
        }
    }

}