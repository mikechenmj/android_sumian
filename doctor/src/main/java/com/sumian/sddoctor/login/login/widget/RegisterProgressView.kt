package com.sumian.sddoctor.login.login.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.view_register_progress.view.*

@Suppress("DEPRECATION")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 14:51
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class RegisterProgressView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_register_progress, this, true)
        setBackgroundColor(context.resources.getColor(R.color.white))
        setProgress(0)
    }

    fun setProgress(progress: Int) {
        iv_dot_0.setImageLevel(if (progress == 0) 1 else 2)
        iv_dot_1.setImageLevel(Math.min(progress, 2))
        iv_dot_2.setImageLevel(if (progress < 2) 0 else 1)
        v_line_0.isEnabled = progress > 0
        v_line_1.isEnabled = progress > 1
        val enableColor = context.resources.getColor(R.color.b3_color)
        val disableColor = context.resources.getColor(R.color.t2_color)
        tv_0.setTextColor(enableColor)
        tv_1.setTextColor(if (progress > 0) enableColor else disableColor)
        tv_2.setTextColor(if (progress > 1) enableColor else disableColor)
    }
}