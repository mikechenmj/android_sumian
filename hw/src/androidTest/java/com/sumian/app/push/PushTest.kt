package com.sumian.app.push

import com.sumian.app.utils.JsonUtil
import org.junit.Assert
import org.junit.Test

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/27 11:20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PushTest {
    @Test
    fun test() {
        val json = "{\"alert\":\"【医生评价】医生对您的睡眠日记报进行了评价，点击通知查看评价详情。\",\"action\":\"com.tech.sumian.action.PUSH\",\"scheme\":\"http://www.sumian.com/day_report?date=1526313600&user_id=2102\",\"extraInfo\":[]}"
        val pushData = JsonUtil.fromJson(json, PushData::class.java)
        val userId = SchemeUtil.getUserIdFromScheme(pushData!!.scheme!!)
        Assert.assertEquals("2102", userId)
    }
}