package com.sumian.hw.upgrade.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

import com.sumian.hw.widget.BaseDialogFragment
import com.sumian.sd.R


/**
 * DFU 升级时,监测仪断开,速眠仪连接阶段的 dialog
 */
class Version2ConnectingDialog : BaseDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, R.style.SumianDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val rootView = inflater.inflate(layout, container, false)
        initView(rootView)
        return rootView
    }

    override fun getLayout(): Int {
        isCancelable = false
        return R.layout.hw_lay_dialog_version_connectting
    }

    companion object {

        fun newInstance(): Version2ConnectingDialog {
            return Version2ConnectingDialog()
        }
    }
}
