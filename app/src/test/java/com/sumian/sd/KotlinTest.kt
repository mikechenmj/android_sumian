package com.sumian.sd

import com.sumian.common.utils.VersionUtil
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
        println(VersionUtil.isVersionZero("0.0.0"))
        print(String.format("%02d", 100))
    }


    @Test
    fun testMaxValue() {

        val value = Int.MAX_VALUE

        val modDefaultIndex = modDefaultIndex(value, 4, 0)

        println("totalCount=$value   mod=$modDefaultIndex")

    }


    private fun modDefaultIndex(totalCount: Int, bannerCount: Int, offset: Int = 0): Int {
        return if (bannerCount == 1) {
            0
        } else {
            var shr = totalCount.shr(1)
            shr += offset
            val startIndex = shr % bannerCount
            if (startIndex != 0) {
                modDefaultIndex(totalCount, bannerCount, (offset + 1))
            } else {
                shr
            }
        }
    }

}