package com.sumian.sleepdoctor.widget.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sleepdoctor.R

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/7/24 15:08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SumianImageTextToast {
    companion object {
        fun showToast(context: Context) {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_image_text_toast, null)
            val toast = Toast(context)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = view
            toast.show()
        }
    }
}