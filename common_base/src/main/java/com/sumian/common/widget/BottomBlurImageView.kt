package com.sumian.common.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.alterac.blurkit.BlurKit

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/19 20:07
 * desc   :
 * version: 1.0
 */
class BottomBlurImageView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet) {

    fun setImageBitmap(bitmap: Bitmap?, blurHeight: Int, blurRadius: Int = 10) {
        if (bitmap == null) {
            super.setImageBitmap(bitmap)
            return
        }
        BlurKit.init(context)
        val blurTop = height - blurHeight
        val cropBitmap = Bitmap.createBitmap(bitmap, 0, blurTop, width, blurHeight, null, false)
        val blurBitmap = BlurKit.getInstance().blur(cropBitmap, blurRadius)
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.drawBitmap(blurBitmap, 0f, blurTop.toFloat(), null)
        super.setImageBitmap(result)
    }

    fun loadUrl(url: String, blurHeight: Int, blurRadius: Int = 10) {
        post {
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(object : SimpleTarget<Bitmap>(measuredWidth, measuredHeight) {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            setImageBitmap(bitmap, blurHeight, blurRadius)
                        }
                    })
        }
    }
}