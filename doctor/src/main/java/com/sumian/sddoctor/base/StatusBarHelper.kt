package com.sumian.sddoctor.base

import android.content.Context
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.widget.TitleBar
import com.sumian.sddoctor.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/3 16:32
 * desc   :
 * version: 1.0
 */
class StatusBarHelper {
    companion object {
        fun initTitleBarUI(context: Context, titleBar: TitleBar) {
            val primaryColor = ColorCompatUtil.getColor(context, R.color.colorPrimary)
            titleBar.setBackgroundColor(ColorCompatUtil.getColor(context, R.color.b2_color))
            titleBar.title.setTextColor(ColorCompatUtil.getColor(context, R.color.t1_color))
            titleBar.mIvBack.setImageDrawable(context.resources.getDrawable(R.drawable.ic_nav_icon_back))
            titleBar.mIvBack.setColorFilter(primaryColor)
            titleBar.mTvMenu.setTextColor(primaryColor)
            titleBar.mTvMenu.textSize = 14f
        }
    }
}