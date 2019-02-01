package com.sumian.sddoctor.widget

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import com.sumian.sddoctor.R

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/28 17:24
 * desc   :
 * version: 1.0
 */
class SddDividerItemDecoration(context: Context, orientation: Int = DividerItemDecoration.VERTICAL) : DividerItemDecoration(context, orientation) {
    init {
        if (orientation == VERTICAL) {
            setDrawable(context.getDrawable(R.drawable.item_divider_line_horizontal))
        } else {
            setDrawable(context.getDrawable(R.drawable.item_divider_line_vertical))
        }
    }
}