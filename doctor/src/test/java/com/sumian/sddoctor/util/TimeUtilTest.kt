package com.sumian.sddoctor.util

import org.junit.Test

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 20:26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class TimeUtilTest {
    @Test
    fun test() {
        System.out.println(TimeUtil.formatDate("yyyy-MM-dd", 1529510400000L))
    }
}