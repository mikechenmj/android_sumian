package com.sumian.sleepdoctor.widget.dialog

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.WindowManager.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
        fun showToast(context: Context, imageResId: Int, textResId: Int, showShort: Boolean) {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_image_text_toast, null)
            val iv = view.findViewById<ImageView>(R.id.iv)
            val tv = view.findViewById<TextView>(R.id.tv_desc)
            iv.setImageResource(imageResId)
            tv.setText(textResId)
            val toast = Toast(context)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.duration = if (showShort) Toast.LENGTH_SHORT else Toast.LENGTH_SHORT
            toast.view = view
            toast.show()
        }

        fun showWindow(context: Context, imageResId: Int, textResId: Int, showShort: Boolean) {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_image_text_toast, null)
            val iv = view.findViewById<ImageView>(R.id.iv)
            val tv = view.findViewById<TextView>(R.id.tv_desc)
            iv.setImageResource(imageResId)
            tv.setText(textResId)
            val windowManger: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params: WindowManager.LayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.type = LayoutParams.TYPE_APPLICATION
            params.flags = LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCHABLE
            params.format = PixelFormat.TRANSLUCENT
            params.gravity = Gravity.CENTER_VERTICAL
            windowManger.addView(view, params)
        }
    }
}