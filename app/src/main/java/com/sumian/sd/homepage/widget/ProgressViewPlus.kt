package com.sumian.sd.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.view_progress_plus.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/11 14:14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ProgressViewPlus(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_progress_plus, this, true)
        setProgress(0)
    }

    fun setProgress(progress: Int) {
        val showProgress = progress in 1..99
        iv_cover.visibility = if (showProgress) View.VISIBLE else GONE
        iv_cover.visibility = if (!showProgress) View.VISIBLE else GONE
        when (progress) {
            0 -> {
                iv_cover.setImageResource(R.drawable.circle_l3)
            }
            100 -> {
                iv_cover.setImageResource(R.drawable.ic_home_icon_cbti_complete)
            }
            else -> {
                progress_view.progress = progress
            }
        }
    }
}