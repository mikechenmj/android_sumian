package com.sumian.sd.service.advisory.utils

import android.text.TextUtils
import com.sumian.common.utils.StreamUtil
import com.sumian.sd.app.App
import java.io.*
import java.lang.StringBuilder

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 18:18
 *
 *     version: 1.0
 *
 *     desc:图文咨询缓存,目前主要用来缓存未提交的图文咨询文本信息
 *
 * </pre>
 */
class AdvisoryContentCacheUtils {

    companion object {

        private const val cacheFileName = "advisoryCacheContent.log"

        private val CACHE_PARENT_PATH = App.getAppContext().cacheDir

        fun saveContent2Cache(advisoryId: Int, content: String) {
            val cacheContent = File(CACHE_PARENT_PATH, "$advisoryId" + cacheFileName)
            if (cacheContent.exists()) {
                writeCache(cacheContent, content)
            } else {
                val createNewFile = cacheContent.createNewFile()
                if (createNewFile) {
                    writeCache(cacheContent, content)
                }
            }
        }

        private fun writeCache(cacheContentFile: File, content: String) {
            if (TextUtils.isEmpty(content)) return
            val bw = BufferedWriter(OutputStreamWriter(FileOutputStream(cacheContentFile)))
            try {
                bw.write(content)
                bw.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                StreamUtil.close(bw)
            }
        }

        fun checkAndLoadCacheContent(advisoryId: Int): String? {
            val cacheContent = File(CACHE_PARENT_PATH, "$advisoryId" + cacheFileName)
            if (cacheContent.exists() && cacheContent.length() > 0) {
                val br = BufferedReader(InputStreamReader(FileInputStream(cacheContent)))
                var line: String?
                val buff = StringBuilder()
                try {
                    line = br.readLine()
                    while (line != null) {
                        buff.append(line)
                        line = br.readLine()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    StreamUtil.close(br)
                }
                return buff.toString()
            } else {
                return null
            }
        }

        fun clearCache(advisoryId: Int) {
            val cacheContent = File(CACHE_PARENT_PATH, "$advisoryId" + cacheFileName)
            if (cacheContent.exists()) {
                cacheContent.delete()
            }
        }
    }
}