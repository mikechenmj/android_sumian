package com.sumian.sleepdoctor.improve.advisory.utils

import com.sumian.common.utils.StreamUtil
import com.sumian.sleepdoctor.app.App
import java.io.*

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 18:18
 *
 *     version: 1.0
 *
 *     desc:图文咨询缓存,目前主要用来缓存未提交的文本信息
 *
 * </pre>
 */
class AdvisoryContentCacheUtils {

    companion object {

        fun saveContent2Cache(content: String) {
            synchronized(this) {
                val cacheContent = File(App.getAppContext().cacheDir, "cacheContent.tmp")
                if (cacheContent.exists()) {
                    writeCache(cacheContent, content)
                } else {
                    val createNewFile = cacheContent.createNewFile()
                    if (createNewFile) {
                        writeCache(cacheContent, content)
                    }
                }
            }
        }

        private fun writeCache(cacheContent: File, content: String) {
            val bw = BufferedWriter(OutputStreamWriter(FileOutputStream(cacheContent)))
            try {
                bw.write(content)
                bw.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                StreamUtil.close(bw)
            }
        }

        fun checkAndLoadCacheContent(): String? {
            val cacheContent = File(App.getAppContext().cacheDir, "cacheContent.tmp")
            if (cacheContent.exists() && cacheContent.length() > 0) {
                val br = BufferedReader(InputStreamReader(FileInputStream(cacheContent)))
                var line: String?
                val buff = StringBuffer()
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

        fun clearCache() {
            synchronized(this) {
                val cacheContent = File(App.getAppContext().cacheDir, "cacheContent.tmp")
                if (cacheContent.exists()) {
                    cacheContent.delete()
                }
            }
        }
    }
}