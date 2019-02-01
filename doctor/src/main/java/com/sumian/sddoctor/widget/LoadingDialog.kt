package com.sumian.sddoctor.widget

import android.content.Context
import androidx.appcompat.app.AppCompatDialog
import com.sumian.sddoctor.R

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