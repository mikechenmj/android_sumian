package com.sumian.sd

import com.sumian.sd.buz.sleepertalk.SleeperTalkDataUtil
import com.sumian.sd.buz.sleepertalk.bean.SleeperTalkData
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
        var list = ArrayList<SleeperTalkData>()
        list.add(SleeperTalkData("", "", "", 1, 1, "", true, 0, "", 1))
        list.add(SleeperTalkData("", "", "", 1, 2, "", true, 0, "", 1))
        list.add(SleeperTalkData("", "", "", 1, 3, "", true, 1, "", 1))
        list.add(SleeperTalkData("", "", "", 1, 4, "", true, 0, "", 1))

        val sortedList = SleeperTalkDataUtil.sortData(list)!!

        for (item in sortedList) {
            println(item.id)
        }
    }

}