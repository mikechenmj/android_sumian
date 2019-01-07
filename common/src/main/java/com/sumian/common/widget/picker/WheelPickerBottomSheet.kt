package com.sumian.common.widget.picker

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.R
import kotlinx.android.synthetic.main.view_wheel_picker_bottom_sheet.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/14 16:08
 * desc   :
 * version: 1.0
 */
class WheelPickerBottomSheet(context: Context,
                             title: String,
                             pickerData: List<Pair<Array<String?>, Int>>,   // list of Pair<PickerValues, initSelectedIndex>
                             listener: Listener
) : BottomSheetDialog(context) {

    private val mPickers by lazy { ArrayList<NumberPickerView>() }
    private val mValues = ArrayList<Int>()

    init {
        setContentView(R.layout.view_wheel_picker_bottom_sheet)
        tv_title.text = title

        createAndAddPickers(pickerData)
        tv_confirm.setOnClickListener {
            mValues.clear()
            for (picker in mPickers) {
                mValues.add(picker.value)
            }
            listener.onConfirmClick(mValues)
            dismiss()
        }
    }

    private fun createAndAddPickers(list: List<Pair<Array<String?>, Int>>) {
        mPickers.clear()
        vg_picker_container.removeAllViews()
        for (item in list) {
            val picker = LayoutInflater.from(context).inflate(R.layout.layout_wheel_picker, vg_picker_container, false)
            mPickers.add(picker as NumberPickerView)
            vg_picker_container.addView(picker)
            picker.refreshByNewDisplayedValues(item.first)
            picker.value = item.second
        }
    }

    interface Listener {
        /**
         * @param values pickers' selected index list
         */
        fun onConfirmClick(values: List<Int>)
    }
}