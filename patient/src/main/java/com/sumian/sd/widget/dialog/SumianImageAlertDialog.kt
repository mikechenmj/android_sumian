package com.sumian.sd.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.sumian.sd.R
import kotlinx.android.synthetic.main.layout_sumian_image_alert_dialog.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/22 15:34
 * desc   :
 * version: 1.0
 */
class SumianImageAlertDialog(context: Context) : Dialog(context, R.style.SumianDialog) {

    init {
        setContentView(R.layout.layout_sumian_image_alert_dialog)
    }

    fun setMessageText(textRes: Int): SumianImageAlertDialog {
        if (textRes == 0) {
            return this
        }
        tv_message.text = context.getText(textRes)
        tv_message.visibility = View.VISIBLE
        return this
    }

    fun setTopIcon(imageRes: Int): SumianImageAlertDialog {
        iv_top.setImageResource(imageRes)
        return this
    }

    fun setOnBtnClickListener(textRes: Int, listener: View.OnClickListener?): SumianImageAlertDialog {
        btn.setOnClickListener {
            dismiss()
            listener?.onClick(btn)
        }
        btn.setText(textRes)
        return this
    }
}