package com.sumian.hw.setting.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.hw_view_setting_item.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/2 10:53
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HwSettingItemView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    init {
        inflate(context, R.layout.hw_view_setting_item, this)
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.HwSettingItemView)
        val imageRes = a.getResourceId(R.styleable.HwSettingItemView_hw_siv_icon, 0)
        val label = a.getString(R.styleable.HwSettingItemView_hw_siv_label)
        val content = a.getString(R.styleable.HwSettingItemView_hw_siv_content)
        val showArrow = a.getBoolean(R.styleable.HwSettingItemView_hw_siv_show_arrow, true)
        val showBottomLine = a.getBoolean(R.styleable.HwSettingItemView_hw_siv_show_bottom_line, true)
        val showRedDot = a.getBoolean(R.styleable.HwSettingItemView_hw_siv_show_red_dot, false)
        a.recycle()
        if (imageRes != 0) {
            iv_icon.visibility = View.VISIBLE
            iv_icon.setImageResource(imageRes)
        }
        tv_label.text = label
        tv_content.text = content
        iv_arrow.visibility = if (showArrow) View.VISIBLE else GONE
        iv_red_dot.visibility = if (showRedDot) View.VISIBLE else GONE
        v_bottom_line.visibility = if (showBottomLine) View.VISIBLE else GONE
    }

    fun showDot(show: Boolean) {
        iv_red_dot.visibility = if (show) View.VISIBLE else GONE
    }

    fun setContent(content: String) {
        tv_content.text = content
    }
}