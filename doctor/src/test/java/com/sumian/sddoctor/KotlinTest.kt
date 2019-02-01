package com.sumian.sddoctor

import org.junit.Test
import java.math.BigDecimal

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 16:57
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class KotlinTest {

    @Test
    fun testFloat() {
        var s = "1234567890.123"
        s = "1234567890.123"
        System.out.println(s.toFloat())
        System.out.println(BigDecimal(s))
        System.out.println(BigDecimal(s).toLong())
    }
}