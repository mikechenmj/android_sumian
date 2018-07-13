package com.sumian.sleepdoctor.widget.dialog

import android.app.Dialog
import android.content.Context
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_loading_dialog.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/15 10:51
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.SumianLoadingDialog) {
    init {
        setContentView(R.layout.lay_loading_dialog)
        progress_bar.indeterminateDrawable.setTint(context.resources.getColor(R.color.colorPrimary))
    }
}