package com.sumian.sleepdoctor

import org.junit.Test

import org.junit.Assert.*
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
        for (i in 1..500) {
            if (i % 2 == 1) {
                continue
            }
            val format = String.format(Locale.getDefault(), "<dimen name=\"space_$i\">${i}dp</dimen>")
            System.out.println(format)
        }

    }
}
