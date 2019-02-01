package com.sumian.sddoctor.widget.sheet

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.widget.picker.NumberPickerView
import com.sumian.sddoctor.R

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/29 8:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SelectBottomSheet(context: Context) {

    private val mSheetDialog = BottomSheetDialog(context)
    private lateinit var mTvTitle: TextView
    private lateinit var mNpv: NumberPickerView
    private lateinit var mTvConfirm: TextView
    private var mContext: Context = context

    private var mTitleText: String? = null
    private var mConfirmText: String? = context.resources.getString(R.string.confirm)
    private var mSelectArrays: Array<String>? = null
    private var mOnItemSelectListener: OnItemSelectListener? = null

    interface OnItemSelectListener {
        fun onItemSelect(text: String)
    }

    fun setTitleText(title: String): SelectBottomSheet {
        mTitleText = title
        return this
    }

    fun setConfirmText(confirmText: String): SelectBottomSheet {
        mConfirmText = confirmText
        return this
    }

    fun setSelectArrays(selectArrays: Array<String>): SelectBottomSheet {
        mSelectArrays = selectArrays
        return this
    }

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener): SelectBottomSheet {
        mOnItemSelectListener = onItemSelectListener
        return this
    }

    fun show() {
        mSheetDialog.setContentView(createContentView())
        mSheetDialog.show()
    }

    fun dismiss() {
        if (mSheetDialog.isShowing) {
            mSheetDialog.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    private fun createContentView(): View {
        val inflate = LayoutInflater.from(mContext).inflate(R.layout.view_select_bottom_sheet, null, false)
        mTvTitle = inflate.findViewById(R.id.tv_title)
        mNpv = inflate.findViewById(R.id.npv)
        mTvConfirm = inflate.findViewById(R.id.tv_confirm)

        mTvConfirm.setOnClickListener {
            dismiss()
            mOnItemSelectListener?.onItemSelect(mNpv.contentByCurrValue)
        }

        mTvTitle.visibility = if (TextUtils.isEmpty(mTitleText)) GONE else VISIBLE
        mTvTitle.text = mTitleText
        if (!TextUtils.isEmpty(mConfirmText)) {
            mTvConfirm.text = mConfirmText
        }
        mNpv.refreshByNewDisplayedValues(mSelectArrays)
        return inflate
    }
}