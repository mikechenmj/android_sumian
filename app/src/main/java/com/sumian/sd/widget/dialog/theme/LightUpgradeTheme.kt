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
class LightUpgradeTheme : LightTheme() {

    @ColorRes
    override var bgColorRes: Int = R.color.b2_color

    @DrawableRes
    override var dismissImageRes: Int = R.mipmap.ic_close

    @DrawableRes
    override var noticeImageRes: Int = R.drawable.ic_popups_update

    @ColorRes
    override var titleColorRes: Int = R.color.t3_color

    @ColorRes
    override var messageColorRes: Int = R.color.t1_color

    @DrawableRes
    override var leftButtonBgRes: Int = R.drawable.bg_btn_white
    @ColorRes
    override var leftButtonFontColorRes: Int = R.color.t5_color

    @DrawableRes
    override var rightButtonBgRes: Int = R.drawable.bg_bt
    @ColorRes
    override var rightButtonFontColorRes: Int = R.color.b2_color


}