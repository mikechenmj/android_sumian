package com.sumian.sd

import com.google.gson.Gson
import com.sumian.hw.command.BlueCmd
import com.sumian.hw.command.Cmd
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
        val m1 = HashMap<String, String>()
        m1.put("123123","1234123")
        m1.put("222","222")
        val m2 = HashMap<String, String>()
        m2.put("123123","1234123")
        System.out.println(m1 == m2)
    }
}