package com.sumian.sd.examine.guide.bean

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */
class Guideline {
    @StringRes
    var h1Label = 0

    @StringRes
    var h2Label = 0

    @DrawableRes
    var iconId = 0
    var indicatorPosition = 0
    override fun toString(): String {
        return "Guideline{" +
                "h1Label='" + h1Label + '\'' +
                ", h2Label='" + h2Label + '\'' +
                ", iconId=" + iconId +
                ", indicatorPosition=" + indicatorPosition +
                '}'
    }
}