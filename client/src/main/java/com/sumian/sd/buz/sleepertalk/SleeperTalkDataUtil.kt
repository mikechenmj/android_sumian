package com.sumian.sd.buz.sleepertalk

import com.sumian.sd.buz.sleepertalk.bean.SleeperTalkData

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/18 15:03
 * desc   :
 * version: 1.0
 */
object SleeperTalkDataUtil {
    fun sortData(list: List<SleeperTalkData>): List<SleeperTalkData> {
        var header: SleeperTalkData? = null
        val mutableList = list.toMutableList()
        val iterator = mutableList.listIterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.isTop == 1) {
                header = item
                iterator.remove()
            }
        }
        if (header != null) {
            mutableList.add(0, header)
        }
        return mutableList
    }
}