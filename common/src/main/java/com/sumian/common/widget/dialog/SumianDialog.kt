package com.sumian.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
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
        iv_close.setOnClickListener { dismiss() }
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

    fun setTitleText(text: String): SumianDialog {
        if (TextUtils.isEmpty(text)) {
            tv_title.visibility = View.GONE
        } else {
            tv_title.text = text
            tv_title.visibility = View.VISIBLE
        }
        return this
    }

    fun setMessageText(text: String): SumianDialog {
        if (TextUtils.isEmpty(text)) {
            tv_message.visibility = View.GONE
        } else {
            tv_message.text = text
            tv_message.visibility = View.VISIBLE
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

    fun setLeftBtn(textRes: Int, listener: View.OnClickListener? = null, autoDismiss: Boolean = true): SumianDialog {
        return setBtn(btn_left, textRes, listener, autoDismiss)
    }

    fun setRightBtn(textRes: Int, listener: View.OnClickListener? = null, autoDismiss: Boolean = true): SumianDialog {
        return setBtn(btn_right, textRes, listener, autoDismiss)
    }

    private fun setBtn(btn: TextView, textRes: Int, listener: View.OnClickListener? = null, autoDismiss: Boolean = true): SumianDialog {
        btn.setOnClickListener {
            listener?.onClick(btn)
            if (autoDismiss) dismiss()
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

    fun setCanceledOnTouchOutsideWrap(cancel: Boolean): SumianDialog {
        super.setCanceledOnTouchOutside(cancel)
        return this
    }

    fun showCloseIcon(show: Boolean): SumianDialog {
        iv_close.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun setOnDismissListenerWrap(listener: DialogInterface.OnDismissListener): SumianDialog {
        setOnDismissListener(listener)
        return this
    }

    fun setOnKeyListenerWrap(listener: DialogInterface.OnKeyListener): SumianDialog {
        super.setOnKeyListener(listener)
        return this
    }
}