package com.sumian.sd.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.sumian.sd.R
import kotlinx.android.synthetic.main.layout_sumian_alert_dialog_v2.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/4/15 14:55
 * desc   :
 * version: 1.0
 */
class SumianAlertDialogV2(context: Context) : Dialog(context, R.style.SumianDialog) {

    init {
        setContentView(R.layout.layout_sumian_alert_dialog_v2)
    }

    fun setTitleText(text: CharSequence): SumianAlertDialogV2 {
        tv_title.text = text
        return this
    }

    fun setTitleText(textRes: Int): SumianAlertDialogV2 {
        if (textRes != 0) {
            setTitleText(context.getText(textRes))
        }
        return this
    }

    fun setMessageText(text: CharSequence): SumianAlertDialogV2 {
        tv_message.text = text
        return this
    }

    fun setMessageText(textRes: Int): SumianAlertDialogV2 {
        if (textRes != 0) {
            setMessageText(context.getText(textRes))
        }
        return this
    }

    fun setLeftBtnOnClickListener(textRes: Int, listener: View.OnClickListener?): SumianAlertDialogV2 {
        btn_left.setOnClickListener {
            listener?.onClick(btn_left)
            dismiss()
        }
        btn_left.setText(textRes)
        return this
    }

    fun setRightBtnOnClickListener(textRes: Int, listener: View.OnClickListener?): SumianAlertDialogV2 {
        btn_right.setOnClickListener {
            listener?.onClick(btn_right)
            dismiss()
        }
        btn_right.setText(textRes)
        return this
    }

    fun whitenLeft(): SumianAlertDialogV2 {
        whiten(btn_left)
        return this
    }

    fun whitenRight(): SumianAlertDialogV2 {
        whiten(btn_right)
        return this
    }

    private fun whiten(textView: TextView) {
        textView.setBackgroundResource(R.drawable.bg_btn_white)
        textView.setTextColor(context.resources.getColor(R.color.t5_color))
    }

}