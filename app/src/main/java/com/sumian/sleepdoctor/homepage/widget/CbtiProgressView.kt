package com.sumian.sleepdoctor.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.view_cbti_progress.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/11 14:43
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class CbtiProgressView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_cbti_progress, this, true)
        progress_view_0.setProgress(100)
        progress_view_1.setProgress(50)
        progress_view_2.setProgress(0)
        progress_view_3.setProgress(0)
        progress_view_4.setProgress(0)
    }
}