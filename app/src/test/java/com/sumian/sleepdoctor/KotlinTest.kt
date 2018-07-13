package com.sumian.sleepdoctor

import com.sumian.sleepdoctor.account.bean.UserProfile
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
//        val u: UserProfile? = UserProfile()
        val u: UserProfile? = null
        val isMale = u?.isMale
        System.out.println(isMale)
        System.out.println(isMale == false)
        System.out.println(isMale == true)
    }
}