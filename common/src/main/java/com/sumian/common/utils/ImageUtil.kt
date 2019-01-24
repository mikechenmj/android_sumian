package com.sumian.common.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/23 18:07
 * desc   :
 * version: 1.0
 */

@SuppressLint("CheckResult")
fun viewToImageFile(view: View, file: File, quality: Int = 50, listener: ViewToImageFileListener) {
    view.postDelayed({
        Flowable.fromCallable {
            if (!file.exists()) {
                file.createNewFile()
            }
            @Suppress("DEPRECATION")
            view.isDrawingCacheEnabled = true
            val bitmap = getBitmapFromView(view)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(file))
            file
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listener.onComplete(file)
                }, {
                    listener.onError(it)
                })
    }, 0)
}

private fun getBitmapFromView(view: View): Bitmap {
    val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable = view.background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return returnedBitmap
}

interface ViewToImageFileListener {
    fun onComplete(file: File)
    fun onError(t: Throwable)
}