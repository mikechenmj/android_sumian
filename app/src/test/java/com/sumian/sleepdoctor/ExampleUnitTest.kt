package com.sumian.sleepdoctor

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
        for (i in 10 downTo 1) {
            val color = (25.5 * i).toInt()
            val hex = Integer.toHexString(color).toUpperCase()
            System.out.println(String.format(Locale.getDefault(),
                    "<color name=\"%s_%d\">#%s%s</color>",
                    "black",
                    i * 10,
                    hex,
                    "000000"))
        }
    }
}
