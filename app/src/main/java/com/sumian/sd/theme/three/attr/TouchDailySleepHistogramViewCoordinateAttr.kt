package com.sumian.sd.theme.three.attr

import android.view.View
import com.sumian.hw.report.widget.histogram.TouchDailySleepHistogramView
import com.sumian.sd.theme.three.attr.base.SkinAttr
import com.sumian.sd.theme.three.utils.SkinResourcesUtils

/**
 * Created by sm
 *
 * on 2018/9/19
 *
 * desc:
 *
 */
class TouchDailySleepHistogramViewCoordinateAttr : SkinAttr() {

    override fun applySkin(view: View?) {
        val touchDailySleepHistogramView = view as? TouchDailySleepHistogramView
        if (isColor) {
            touchDailySleepHistogramView?.setCoordinateColor(SkinResourcesUtils.getColor(attrValueRefId))
        }
    }

    override fun applyNightMode(view: View?) {
        super.applyNightMode(view)
        val touchDailySleepHistogramView = view as? TouchDailySleepHistogramView
        if (isColor) {
            touchDailySleepHistogramView?.setCoordinateColor(SkinResourcesUtils.getNightColor(attrValueRefId))
        }
    }
}