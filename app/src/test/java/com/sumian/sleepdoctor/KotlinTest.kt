package com.sumian.sleepdoctor

import org.junit.Test
import java.util.*

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
        val list = ArrayList<Int>()
        list.add(1)
        list.forEach {
            System.out.println(it)
        }
    }
}