package com.sumian.sd

import com.google.gson.Gson
import com.sumian.hw.command.BlueCmd
import com.sumian.hw.command.Cmd
import com.sumian.sd.account.config.SumianConfig
import com.sumian.sd.bean.ClassA
import org.junit.Test
import java.math.BigInteger

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
        val data = "10013f0f1f210013f0f1f210013f0f1f2f00"
        System.out.println(data.length)
        val cmd = BlueCmd.makeCmd(Cmd.CMD_SET_PATTERN, data)
        System.out.println(BlueCmd.bytes2HexString(cmd))
    }
}