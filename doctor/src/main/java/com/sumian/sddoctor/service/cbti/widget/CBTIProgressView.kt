package com.sumian.sddoctor.service.cbti.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.lay_cbti_progress_view.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/11 14:14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CBTIProgressView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    private var progress = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.lay_cbti_progress_view, this, true)
        setProgress(0, true)
    }

    fun setProgress(progress: Int, isLock: Boolean) {
        this.progress = progress
        when (progress) {
            0 -> {
                if (isLock) {
                    iv_cover.setImageResource(R.drawable.ic_cbti_exercise_lock)
                    progress_view.visibility = View.GONE
                    iv_cover.visibility = View.VISIBLE
                } else {
                    iv_cover.visibility = View.GONE
                    progress_view.progress = progress
                    progress_view.visibility = View.VISIBLE
                }
            }
            100 -> {
                progress_view.visibility = View.GONE
                iv_cover.setImageResource(R.drawable.ic_home_icon_cbti_complete)
                iv_cover.visibility = View.VISIBLE
            }
            else -> {
                progress_view.progress = progress
                progress_view.visibility = View.VISIBLE
                iv_cover.visibility = View.GONE
            }
        }
    }
}