package com.sumian.sd.buz.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.view_with_line_progress.view.*

@Suppress("DEPRECATION")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/11 14:43
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class WithLineProgressView(context: Context, attributeSet: AttributeSet?) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_with_line_progress, this, true)
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.WithLineProgressView)
        val showLine = a.getBoolean(R.styleable.WithLineProgressView_wlpv_show_line, true)
        a.recycle()
        showLeftLine(showLine)
        setProgress(0)
    }

    fun setProgress(progress: Int) {
        progress_view_plus.setProgress(progress)
        v_line.setBackgroundColor(resources.getColor(if (progress > 0) R.color.b5_color else R.color.l3_color))
    }

    fun showLeftLine(show: Boolean) {
        v_line.visibility = if (show) View.VISIBLE else View.GONE
    }
}