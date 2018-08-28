package com.sumian.sd.widget.sheet

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import com.blankj.utilcode.util.SnackbarUtils.dismiss
import com.sumian.sd.R
import com.sumian.sd.R.id.*
import com.umeng.socialize.utils.DeviceConfig.context
import kotlinx.android.synthetic.main.bottom_sheet_select_time_hh_mm.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/28 15:03
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SelectTimeHHmmBottomSheet(context: Context, @StringRes title: Int,
                                initHour: Int = 0, initMinute: Int = 0,
                                listener: OnTimePickedListener?) : BottomSheetDialog(context) {
    interface OnTimePickedListener {
        fun onTimePicked(hour: Int, minute: Int)
    }

    init {
        val inflate = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_select_time_hh_mm, null, false)
        setContentView(inflate)

        tv_title.setText(title)
        picker_hour.refreshByNewDisplayedValues(Array(24) { i -> i.toString() })
        picker_minute.refreshByNewDisplayedValues(Array(60) { i -> i.toString() })
//        picker_hour.minValue = 0
//        picker_hour.maxValue = 23
//        picker_minute.minValue = 0
//        picker_minute.maxValue = 59
        picker_hour.value = initHour
        picker_minute.value = initMinute
        tv_confirm.setOnClickListener {
            listener?.onTimePicked(picker_hour.value, picker_minute.value)
            dismiss()
        }
    }
}