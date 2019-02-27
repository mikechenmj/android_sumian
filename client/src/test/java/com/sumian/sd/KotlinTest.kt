package com.sumian.sd

import com.sumian.sd.buz.diary.fillsleepdiary.bean.SleepMedicine
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
        var list = ArrayList<SleepMedicine>()
        list.add(SleepMedicine("1", 1, 1, "1", 1))
        list.add(SleepMedicine("3", 2, 2, "3", 3))
        list.add(SleepMedicine("2", 0, 2, "2", 2))

        val sortedList = list.sortedWith(compareBy(SleepMedicine::weight, SleepMedicine::weight)).reversed().filter { it.enable != 0 }
        println(sortedList)
//        val sortedList = list.sortWith(compareBy(SleepMedicine::weight, SleepMedicine::weight))
//        println(list)
    }

}