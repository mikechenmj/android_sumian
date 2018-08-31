package com.sumian.common.dialog

import android.content.Context
import android.support.v7.app.AppCompatDialog
import com.sumian.common.R

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
        setContentView(R.layout.lay_loading_dialog)
    }
}