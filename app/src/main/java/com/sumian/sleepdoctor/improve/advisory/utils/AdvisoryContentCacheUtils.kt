package com.sumian.sleepdoctor.improve.advisory.utils

import com.sumian.common.utils.StreamUtil
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.utils.JsonUtil.toJson
import org.json.JSONObject
import java.io.*

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

        private const val cacheFileName = "cacheContent.tmp"

        fun saveContent2Cache(advisoryId: Int, content: String) {
            synchronized(this) {
                val cacheContent = File(App.getAppContext().cacheDir, "$advisoryId" + cacheFileName)
                if (cacheContent.exists()) {
                    if (cacheContent.length() > 0) {
                        writeCache(advisoryId, cacheContent, content)
                    } else {

                    }
                } else {
                    val createNewFile = cacheContent.createNewFile()
                    if (createNewFile) {
                        writeCache(advisoryId, cacheContent, content)
                    }
                }
            }
        }

        private fun writeCache(advisoryId: Int, cacheContentFile: File, content: String) {
            val tmpMap = mutableMapOf<Int, String>()
            tmpMap[advisoryId] = content
            val bw = BufferedWriter(OutputStreamWriter(FileOutputStream(cacheContentFile)))
            try {
                bw.write(toJson(tmpMap))
                bw.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                StreamUtil.close(bw)
            }
        }

        fun checkAndLoadCacheContent(advisoryId: Int): String? {
            val cacheContent = File(App.getAppContext().cacheDir, "$advisoryId" + cacheFileName)
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
                val json = buff.toString()
                val jsonObject = JSONObject(json)
                return jsonObject.getString(advisoryId.toString())
            } else {
                return null
            }
        }

        fun clearCache(advisoryId: Int) {
            synchronized(this) {
                val cacheContent = File(App.getAppContext().cacheDir, "$advisoryId" + cacheFileName)
                if (cacheContent.exists()) {
                    cacheContent.delete()
                }
            }
        }
    }
}