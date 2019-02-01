package com.sumian.sd

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun printDimen() {
        val list = ArrayList<Int>()
        list.add(1)
        list.forEach {
            System.out.println(it)
        }

    }
}
