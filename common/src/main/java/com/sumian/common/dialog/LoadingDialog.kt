package com.sumian.common.dialog

import android.content.Context
import android.support.v7.app.AppCompatDialog
import com.sumian.common.R
import kotlinx.android.synthetic.main.common_lay_loading_dialog.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/15 10:51
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LoadingDialog(ctx: Context) : AppCompatDialog(ctx, R.style.SumianLoadingDialog) {
    init {
        setContentView(R.layout.common_lay_loading_dialog)
        common_loading_progress_bar.indeterminateDrawable.setTint(context.resources.getColor(R.color.colorPrimary))
    }
}