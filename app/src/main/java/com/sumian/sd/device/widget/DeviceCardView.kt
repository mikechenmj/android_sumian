package com.sumian.sd.device.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sumian.sd.R
import com.umeng.socialize.utils.DeviceConfig.context
import kotlinx.android.synthetic.main.view_device_card.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 19:43
 * desc   :
 * version: 1.0
 */
class DeviceCardView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_device_card, this, true)
        sleeper_battery_view.setProgress(0)
        monitor_battery_view.setProgress(50)
    }
}