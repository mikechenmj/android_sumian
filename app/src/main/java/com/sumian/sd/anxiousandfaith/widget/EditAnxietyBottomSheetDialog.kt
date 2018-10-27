package com.sumian.sd.anxiousandfaith.widget

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import com.sumian.sd.R
import kotlinx.android.synthetic.main.layout_bottom_sheet_edit_anxiety.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 15:30
 * desc   :
 * version: 1.0
 */
class EditAnxietyBottomSheetDialog(context: Context, onItemClickListener: OnItemClickListener) : BottomSheetDialog(context) {
    init {
        setContentView(R.layout.layout_bottom_sheet_edit_anxiety)
        tv_edit.setOnClickListener {
            onItemClickListener.onEditClick()
            dismiss()
        }
        tv_delete.setOnClickListener {
            onItemClickListener.onDeleteClick()
            dismiss()
        }
        tv_cancel.setOnClickListener {
            dismiss()
        }
    }


    interface OnItemClickListener {
        fun onEditClick()
        fun onDeleteClick()
    }
}