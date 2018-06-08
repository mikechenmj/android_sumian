package com.sumian.sleepdoctor.push

import android.os.Bundle
import org.junit.Test

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 10:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PushTest {

    @Test
    fun test() {
        val bundle = Bundle()
        bundle.putString("action", "push")
        bundle.putString("alert", "this is a message")
        bundle.putString("scheme", "http://www.google.com")
        val pushData = PushData.create(bundle)
        System.out.println(pushData)
    }

    @Test
    fun test2() {
        var pushData: PushData? = PushData(null)
        System.out.println(pushData)
        pushData = null
        val action = pushData?.action ?: "default_action"
        System.out.println(action)
    }
}