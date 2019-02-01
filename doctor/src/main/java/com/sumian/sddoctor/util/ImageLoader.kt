package com.sumian.sddoctor.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/13 19:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ImageLoader {
    companion object {
        fun load(context: Context, path: String?, imageView: ImageView) {
            Glide.with(context)
                    .load(path)
                    .into(imageView)
        }

        fun load(context: Context, uri: Uri?, imageView: ImageView) {
            Glide.with(context)
                    .load(uri)
                    .into(imageView)
        }

        fun load(context: Context, resId: Int, imageView: ImageView) {
            Glide.with(context)
                    .load(resId)
                    .into(imageView)
        }

        fun load(context: Context, errorId: Int, path: String?, iv: ImageView) {
            Glide.with(context).load(path).apply(RequestOptions.errorOf(errorId).placeholder(errorId)).into(iv)
        }
    }
}