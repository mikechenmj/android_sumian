package com.sumian.common.image

import androidx.annotation.DrawableRes
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * Created by sm
 *
 *
 * on 2018/8/1
 *
 *
 * desc:图片加载,因为有各种默认值,因此可以重载参数列表
 */
object ImageLoader {

    @JvmOverloads
    @JvmStatic
    fun loadImage(url: String, iv: ImageView, @DrawableRes placeHolderId: Int = -1, @DrawableRes errorId: Int = -1, isCenterCrop: Boolean = false) {

        var options = RequestOptions.placeholderOf(placeHolderId).error(errorId).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

        if (isCenterCrop) {
            options = options.centerCrop()
        }

        if (iv.visibility != View.VISIBLE) {
            iv.visibility = View.VISIBLE
        }

        Glide.with(iv.context).load(url).apply(options).into(iv)
    }

    @JvmOverloads
    @JvmStatic
    fun loadImage(@DrawableRes drawableId: Int = -1, iv: ImageView, @DrawableRes placeHolderId: Int = -1, @DrawableRes errorId: Int = -1, isCenterCrop: Boolean = false) {

        var options = RequestOptions.placeholderOf(placeHolderId).error(errorId).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

        if (isCenterCrop) {
            options = options.centerCrop()
        }

        if (iv.visibility != View.VISIBLE) {
            iv.visibility = View.VISIBLE
        }

        Glide.with(iv.context).load(drawableId).apply(options).into(iv)
    }
}
