package com.sumian.sd.widget.dialog.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.sumian.sd.R

/**
 * Created by dq
 *
 * on 2018/8/17
 *
 * desc:白色主题
 */
open class LightTheme : ITheme {

    @ColorRes
    open var bgColorRes: Int = R.color.b2_color

    @DrawableRes
    open var dismissImageRes: Int = R.mipmap.ic_close

    @DrawableRes
    open var noticeImageRes: Int = R.mipmap.ic_notification_alert

    @ColorRes
    open var titleColorRes: Int = R.color.t3_color

    @ColorRes
    open var messageColorRes: Int = R.color.t1_color

    @DrawableRes
    open var leftButtonBgRes: Int = R.drawable.bg_btn_white
    @ColorRes
    open var leftButtonFontColorRes: Int = R.color.t5_color

    @DrawableRes
    open var rightButtonBgRes: Int = R.drawable.bg_bt
    @ColorRes
    open var rightButtonFontColorRes: Int = R.color.b2_color


}