package com.sumian.sd.widget.dialog.theme

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import com.sumian.sd.R

/**
 * Created by dq
 *
 * on 2018/8/17
 *
 * desc:黑色主题
 */
class BlackTheme : ITheme {

    @ColorRes
    var bgColorRes: Int = R.color.t3_color

    @DrawableRes
    var dismissImageRes: Int = R.drawable.ic_msg_delected_black

    @DrawableRes
    var noticeImageRes: Int = R.drawable.ic_msg_notice_black

    @ColorRes
    var titleColorRes: Int = R.color.b2_color

    @ColorRes
    var messageColorRes: Int = R.color.hw_tv_normal

    @DrawableRes
    var leftButtonBgRes: Int = R.drawable.bg_btn_black
    @ColorRes
    var leftButtonFontColorRes: Int = R.color.bt_hole_color

    @DrawableRes
    var rightButtonBgRes: Int = R.drawable.bg_bt
    @ColorRes
    var rightButtonFontColorRes: Int = R.color.b2_color


}