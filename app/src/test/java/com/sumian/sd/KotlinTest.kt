package com.sumian.sd

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.account.config.SumianConfig
import com.sumian.sd.bean.ClassA
import com.sumian.sd.h5.bean.H5BaseResponse
import com.sumian.sd.utils.JsonUtil
import org.junit.Test
import java.util.ArrayList

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
        val s = "{\n" +
                "\"showNavigationBar\": true,\n" +
                "\"showTitle\": true,\n" +
                "\"showBackArrow\": true,\n" +
                "\"name\":\"jack\"\n" +
                "}"
        val map = JsonUtil.fromJson<Map<String, Any>>(s, object : TypeToken<Map<String, Any>>() {

        }.type)
        for ((key, value) in map ?: return) {
            System.out.println("$key, $value")
            when (key) {
                "showNavigationBar" -> System.out.println(" ${value as? Boolean}")
                "showTitle" -> System.out.println(" ${value as? Boolean}")
                "showBackArrow" -> System.out.println(" ${value as? Boolean}")
                "name" -> System.out.println(" ${value as? String}")
            }
        }

    }

    fun getReminderHHmm(remindAt: String): String {
        val endIndex = remindAt.lastIndexOf(":")
        return remindAt.substring(-0, endIndex)
    }
}