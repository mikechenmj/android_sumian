package com.sumian.sd.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.sumian.sd.R
import kotlinx.android.synthetic.main.layout_sumian_title_message_dialog.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/19 14:31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SumianTitleMessageDialog(context: Context) : Dialog(context, R.style.SumianDialog) {

    init {
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_sumian_title_message_dialog, null, false);
        setContentView(inflate)
    }

    fun setTitle(title: String): SumianTitleMessageDialog {
        tv_title.text = title
        return this
    }

    fun setMessage(message: String): SumianTitleMessageDialog {
        tv_message.text = message
        return this
    }

    fun showCloseIv(show: Boolean): SumianTitleMessageDialog {
        iv_close.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }
}