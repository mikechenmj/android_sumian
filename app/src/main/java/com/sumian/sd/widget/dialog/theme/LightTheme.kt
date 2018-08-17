package com.sumian.sd.widget.dialog.theme

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import com.sumian.sd.R

/**
 * Created by dq
 *
 * on 2018/8/17
 *
 * desc:白色主题
 */
class LightTheme : ITheme {

    @ColorRes
    var bgColorRes: Int = R.color.b2_color

    @DrawableRes
    var dismissImageRes: Int = R.mipmap.ic_close

    @DrawableRes
    var noticeImageRes: Int = R.mipmap.ic_notification_alert

    @ColorRes
    var titleColorRes: Int = R.color.t3_color

    @ColorRes
    var messageColorRes: Int = R.color.t1_color

    @DrawableRes
    var leftButtonBgRes: Int = R.drawable.bg_btn_white
    @ColorRes
    var leftButtonFontColorRes: Int = R.color.t5_color

    @DrawableRes
    var rightButtonBgRes: Int = R.drawable.bg_bt
    @ColorRes
    var rightButtonFontColorRes: Int = R.color.b2_color


}