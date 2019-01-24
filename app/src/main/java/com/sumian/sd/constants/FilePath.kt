package com.sumian.sd.constants

import android.os.Environment
import java.io.File

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/24 14:15
 * desc   :
 * version: 1.0
 */
object FilePath {
    const val EXTERNAL_ROOT_DIR = "com.sumian"
    const val EXTERNAL_CACHE_DIR = "cache"

    fun getExternalRootDir(): File {
        val dir = File(Environment.getExternalStorageDirectory(), EXTERNAL_ROOT_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getExternalCacheDir(): File {
        val dir = File(getExternalRootDir(), EXTERNAL_CACHE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}