package com.sumian.sddoctor.me.mywallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.image_text_empty_view.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/25 17:13
 * desc   :
 * version: 1.0
 */
class ImageTextEmptyView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.image_text_empty_view, this, true)
    }

    fun setImage(imageRes: Int): ImageTextEmptyView {
        iv.setImageResource(imageRes)
        return this
    }

    fun setText(textRes: Int): ImageTextEmptyView {
        tv.setText(textRes)
        return this
    }
}