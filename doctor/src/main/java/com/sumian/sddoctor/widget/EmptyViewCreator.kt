package com.sumian.sddoctor.widget

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.me.mywallet.widget.ImageTextEmptyView

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/22 16:30
 * desc   :
 * version: 1.0
 */
object EmptyViewCreator {
    fun createSingleLineTextViewEmptyView(context: Context, textRes: Int): TextView {
        val textView = TextView(context)
        textView.text = context.resources.getString(textRes)
        textView.setTextColor(ColorCompatUtil.getColor(context, R.color.t2_color))
        textView.textSize = 14f
        textView.gravity = Gravity.CENTER
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.resources.getDimension(R.dimen.space_60).toInt())
        return textView
    }

    fun createImageTextEmptyView(context: Context, @DrawableRes imageRes: Int, @StringRes textRes: Int): ImageTextEmptyView {
        return ImageTextEmptyView(context).setImage(imageRes).setText(textRes)
    }
}