package com.sumian.sd.buz.upgrade.dialog

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.sumian.sd.R
import com.sumian.sd.widget.BaseDialogFragment

class UpgradeConfirmDialog(var title: String, var content: String, var confirm: () -> Unit) : BaseDialogFragment() {

    var mTitleTextView: TextView? = null
    var mContentTextView: TextView? = null
    var mConfirmButton: Button? = null

    override fun getLayout(): Int {
        return R.layout.dialog_dfu_upgrade_confirm_layout
    }

    override fun initView(rootView: View?) {
        mTitleTextView = rootView?.findViewById(R.id.tv_title)
        mContentTextView = rootView?.findViewById(R.id.tv_content)
        mConfirmButton = rootView?.findViewById(R.id.bt_confirm)
        mTitleTextView?.text = title
        mContentTextView?.text = content
        mConfirmButton?.setOnClickListener { confirm() }
    }
}