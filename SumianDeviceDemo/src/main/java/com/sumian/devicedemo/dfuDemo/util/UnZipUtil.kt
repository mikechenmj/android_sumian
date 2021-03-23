package com.sumian.devicedemo.dfuDemo.util

import com.leo618.zip.IZipCallback
import com.leo618.zip.ZipManager

object UnZipUtil {

    fun unzip(target: String, destination: String, callback: IZipCallback) {
        ZipManager.unzip(target, destination, callback)
    }
}