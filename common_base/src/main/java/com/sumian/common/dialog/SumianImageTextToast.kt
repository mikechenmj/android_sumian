package com.sumian.common.dialog

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sumian.common.R

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
        fun showToast(context: Context, imageResId: Int, textResId: Int, showLong: Boolean) {
            showToast(context, imageResId, context.resources.getString(textResId), showLong)
        }

        fun showToast(context: Context, imageResId: Int, text: String?, showLong: Boolean) {
            val view = LayoutInflater.from(context).inflate(R.layout.common_layout_image_text_toast, null)
            val iv = view.findViewById<ImageView>(R.id.iv)
            val tv = view.findViewById<TextView>(R.id.tv_desc)
            if (imageResId == 0) {
                iv.visibility = GONE
            } else {
                iv.visibility = VISIBLE
                iv.setImageResource(imageResId)
            }
            tv.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
            tv.setText(text)
            val toast = Toast(context)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.duration = if (showLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            toast.view = view
            toast.show()
        }
    }
}