package com.sumian.sd

import com.google.gson.Gson
import com.sumian.hw.common.util.TimeUtil
import com.sumian.sd.account.config.SumianConfig
import com.sumian.sd.bean.ClassA
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
        var s = 3660
        System.out.println(TimeUtil.secondToHHMM(s))
        s = 60
        System.out.println(TimeUtil.secondToHHMM(s))
        s = 3600
        System.out.println(TimeUtil.secondToHHMM(s))
    }
}