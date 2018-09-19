package com.sumian.sd.theme.three.attr

import android.view.View
import com.sumian.hw.report.widget.SleepAvgAndCompareView
import com.sumian.sd.theme.three.attr.base.SkinAttr
import com.sumian.sd.theme.three.utils.SkinResourcesUtils


class SleepAvgAndCompareAttr : SkinAttr() {

    override fun applySkin(view: View) {
        val sleepAvgAndCompareView = view as? SleepAvgAndCompareView
        sleepAvgAndCompareView?.setDrawableLabel(SkinResourcesUtils.getDrawable(attrValueRefId))
    }

    override fun applyNightMode(view: View) {
        super.applyNightMode(view)
        val sleepAvgAndCompareView = view as? SleepAvgAndCompareView
        sleepAvgAndCompareView?.setDrawableLabel(SkinResourcesUtils.getNightDrawable(attrValueRefName))

    }
}
