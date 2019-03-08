package com.sumian.module_core.patientdoctorim.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.KeyboardUtils
import com.sumian.module_core.R
import kotlinx.android.synthetic.main.core_im_input_box.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:37
 * desc   :
 * version: 1.0
 */
class InputBoxView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    init {
        View.inflate(context, R.layout.core_im_input_box, this)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun showRecordVoicePanel(show:Boolean) {
        iv_switch_voice.isSelected = show
        record_voice_container.isVisible = show
    }
}