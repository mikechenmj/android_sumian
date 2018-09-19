package com.sumian.sd.theme.three.attr

import android.view.View
import com.sumian.sd.record.widget.ColorfulProgressView
import com.sumian.sd.theme.three.attr.base.SkinAttr
import com.sumian.sd.theme.three.utils.SkinResourcesUtils


class ColorfulProgressRingAttr : SkinAttr() {

    override fun applySkin(view: View) {
        val colorfulProgressView = view as? ColorfulProgressView
        val color = SkinResourcesUtils.getColor(attrValueRefId)
        colorfulProgressView?.setRingBgColor(color)
    }

    override fun applyNightMode(view: View) {
        val colorfulProgressView = view as? ColorfulProgressView
        val color = SkinResourcesUtils.getNightColor(attrValueRefId)
        colorfulProgressView?.setRingBgColor(color)
    }
}
