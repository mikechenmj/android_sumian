package com.sumian.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import com.sumian.common.R
import kotlinx.android.synthetic.main.common_layout_sumian_artical_dialog.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/23 17:52
 * desc   :
 * version: 1.0
 */
class SumianArticleDialog(context: Context) : Dialog(context, R.style.SumianDialog) {

    init {
        setContentView(R.layout.common_layout_sumian_artical_dialog)
        iv_close.setOnClickListener { dismiss() }
    }


    fun setTitleText(textRes: Int): SumianArticleDialog {
        if (textRes == 0) {
            tv_title.visibility = View.GONE
        } else {
            tv_title.text = context.getText(textRes)
            tv_title.visibility = View.VISIBLE
        }
        return this
    }

    fun setTitleText(text: String): SumianArticleDialog {
        if (TextUtils.isEmpty(text)) {
            tv_title.visibility = View.GONE
        } else {
            tv_title.text = text
            tv_title.visibility = View.VISIBLE
        }
        return this
    }

    fun setMessageText(text: String): SumianArticleDialog {
        if (TextUtils.isEmpty(text)) {
            tv_message.visibility = View.GONE
        } else {
            tv_message.text = text
            tv_message.visibility = View.VISIBLE
        }
        return this
    }

    fun setMessageText(textRes: Int): SumianArticleDialog {
        if (textRes == 0) {
            tv_message.visibility = View.GONE
        } else {
            tv_message.text = context.getText(textRes)
            tv_message.visibility = View.VISIBLE
        }
        return this
    }


    fun setCanceledOnTouchOutsideV2(cancel: Boolean): SumianArticleDialog {
        super.setCanceledOnTouchOutside(cancel)
        return this
    }
}