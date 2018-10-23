package com.sumian.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.sumian.common.R
import com.sumian.common.utils.ColorCompatUtil
import kotlinx.android.synthetic.main.common_layout_sumian_dialog.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/23 17:52
 * desc   :
 * version: 1.0
 */
class SumianDialog(context: Context) : Dialog(context, R.style.SumianDialog) {
    private val mContext = context

    init {
        setContentView(R.layout.common_layout_sumian_dialog)
    }

    fun setTopIcon(imageRes: Int): SumianDialog {
        if (imageRes == 0) {
            iv_top.visibility = View.GONE
        } else {
            iv_top.visibility = View.VISIBLE
            iv_top.setImageResource(imageRes)
        }
        return this
    }

    fun setTitleText(textRes: Int): SumianDialog {
        if (textRes == 0) {
            tv_title.visibility = View.GONE
        } else {
            tv_title.text = context.getText(textRes)
            tv_title.visibility = View.VISIBLE
        }
        return this
    }

    fun setMessageText(textRes: Int): SumianDialog {
        if (textRes == 0) {
            tv_message.visibility = View.GONE
        } else {
            tv_message.text = context.getText(textRes)
            tv_message.visibility = View.VISIBLE
        }
        return this
    }

    fun setLeftBtn(textRes: Int, listener: View.OnClickListener?): SumianDialog {
        return setBtn(btn_left, textRes, listener)
    }

    fun setRightBtn(textRes: Int, listener: View.OnClickListener?): SumianDialog {
        return setBtn(btn_right, textRes, listener)
    }

    private fun setBtn(btn: TextView, textRes: Int, listener: View.OnClickListener?): SumianDialog {
        btn.setOnClickListener {
            dismiss()
            listener?.onClick(btn)
        }
        btn.setText(textRes)
        btn.visibility = View.VISIBLE
        return this
    }


    fun whitenLeft(): SumianDialog {
        return whitenBtn(btn_left)
    }

    fun whitenRight(): SumianDialog {
        return whitenBtn(btn_right)
    }

    private fun whitenBtn(btn: TextView): SumianDialog {
        btn.setBackgroundResource(R.drawable.bg_btn_white)
        btn.setTextColor(ColorCompatUtil.getColor(mContext, R.color.t5_color))
        return this
    }

    fun setCanceledOnTouchOutsideV2(cancel: Boolean): SumianDialog {
        super.setCanceledOnTouchOutside(cancel)
        return this
    }
}