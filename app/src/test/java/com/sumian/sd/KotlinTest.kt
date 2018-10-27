package com.sumian.sd

import android.text.format.DateUtils
import com.google.gson.Gson
import com.sumian.common.utils.TimeUtilV2
import com.sumian.hw.utils.TimeUtil
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

    @Test
    fun test4() {
        val current = "1.3.0.0"
        val online = "1.2.2.0".split(".")

        var isHaveVersion = true
        current.split(".").forEachIndexed { index, version ->
            if (version < online[index]) {
                println("cc")
                isHaveVersion = false
                return@forEachIndexed
            } else {
                isHaveVersion = false
            }
        }
        println(isHaveVersion)
    }

    @Test
    fun t1() {
        val t1 = System.currentTimeMillis()
        System.out.println(TimeUtilV2.getDayDistance(t1, t1 - 3000))
        System.out.println(TimeUtilV2.getDayDistance(t1, t1 - DateUtils.DAY_IN_MILLIS))
        System.out.println(TimeUtilV2.getDayDistance(t1, t1 - DateUtils.DAY_IN_MILLIS - 3000))
        System.out.println(TimeUtilV2.getDayDistance(t1, t1 - DateUtils.DAY_IN_MILLIS * 10 - 3000))
    }

    @Test
    fun t2() {
        for (i in 0 until 3)
            System.out.println(i)
    }

}