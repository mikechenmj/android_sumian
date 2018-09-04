package com.sumian.sd

import com.google.gson.Gson
import com.sumian.sd.account.config.SumianConfig
import com.sumian.sd.bean.ClassA
import com.sumian.sd.onlinereport.OnlineReport
import com.sumian.sd.utils.JsonUtil
import org.junit.Test

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 14:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class KotlinTest {
    @Test
    fun test() {
        val regex = String.format(".{%d,%d}", SumianConfig.PASSWORD_LENGTH_MIN, SumianConfig.PASSWORD_LENGTH_MAX)
        System.out.println(regex)
        System.out.println("12313".matches(regex.toRegex()))
    }

    @Test
    fun test2() {
        val gson = Gson()
        val json = "{\"enable\":true, \"age\":null}"
        val a = gson.fromJson<ClassA>(json, ClassA::class.java)
        println(a)
    }

    @Test
    fun test3() {
        var json =
                "{\n" +
                        "            \"id\":22,\n" +
                        "            \"title\":\"CBTI初期报告\",\n" +
                        "            \"type\":1,\n" +
                        "            \"data\":{\n" +
                        "                \"scale_id\":\"1037,1038,1039\",\n" +
                        "                \"chapter_id\":\"1\"\n" +
                        "            },\n" +
                        "            \"conversion_status\":1,\n" +
                        "            \"task_id\":\"\",\n" +
                        "            \"report_url\":\"http://sd-dev.sumian.com/scale-details/scales?scale_id=1037,1038,1039&chapter_id=1\",\n" +
                        "            \"deleted_at\":null,\n" +
                        "            \"created_at\":1536061737,\n" +
                        "            \"updated_at\":1536061737\n" +
                        "        }"
//        json = "{\"id\":22,\"title\":\"CBTI初期报告\",\"type\":1,\"data\":\"data\",\"conversion_status\":1,\"task_id\":\"\",\"report_url\":\"http://sd-dev.sumian.com/scale-details/scales?scale_id\\u003d1037,1038,1039\\u0026chapter_id\\u003d1\",\"created_at\":1536061737,\"updated_at\":1536061737}"
        System.out.println(json)
//        val data = JsonUtil.fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
        val data = JsonUtil.fromJson<OnlineReport>(json, OnlineReport::class.java)
        System.out.println(JsonUtil.toJson(data))
    }
}