package com.sumian.common.image

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
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
class ImageLoader {

    companion object {

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
}

/**
 * 拓展函数  可以更加方便的使用并加载图片   用法为 kotlin: ImageView().load()
 *                                             java:  ImageLoaderKt.load()
 *
 * @receiver ImageView
 * @param drawableId Int
 * @param placeHolderId Int
 * @param errorId Int
 * @param isCenterCrop Boolean
 */
@JvmOverloads
fun ImageView.loadImage(@DrawableRes drawableId: Int = -1, @DrawableRes placeHolderId: Int = -1, @DrawableRes errorId: Int = -1, isCenterCrop: Boolean = false) {

    var options = RequestOptions.placeholderOf(placeHolderId).error(errorId).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    if (isCenterCrop) {
        options = options.centerCrop()
    }

    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    Glide.with(context).load(drawableId).apply(options).into(this)
}

/**
 * 拓展函数  可以更加方便的使用并加载图片   用法为 kotlin: ImageView().load()
 *                                             java:  ImageLoaderKt.load()
 * @receiver ImageView
 * @param url String
 * @param placeHolderId Int
 * @param errorId Int
 * @param isCenterCrop Boolean
 */
@JvmOverloads
fun ImageView.loadImage(url: String, @DrawableRes placeHolderId: Int = -1, @DrawableRes errorId: Int = -1, isCenterCrop: Boolean = false) {

    var options = RequestOptions.placeholderOf(placeHolderId).error(errorId).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    if (isCenterCrop) {
        options = options.centerCrop()
    }

    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    Glide.with(context).load(url).apply(options).into(this)
}
