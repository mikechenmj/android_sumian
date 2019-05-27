package com.sumian.devicedemo

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.sumian.devicedemo.sleepdata.data.CalendarItemSleepReport
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val json =
                "{\"1525104000\":[{\"id\":163,\"date\":1525449600,\"is_today\":false},{\"id\":168,\"date\":1525536000,\"is_today\":false},{\"id\":172,\"date\":1525622400,\"is_today\":false},{\"id\":195,\"date\":1526227200,\"is_today\":false},{\"id\":199,\"date\":1526313600,\"is_today\":false}],\"1522512000\":[{\"id\":109,\"date\":1524067200,\"is_today\":false},{\"id\":125,\"date\":1523635200,\"is_today\":false}],\"1519833600\":[],\"1517414400\":[],\"1514736000\":[],\"1512057600\":[],\"1509465600\":[],\"1506787200\":[],\"1504195200\":[],\"1501516800\":[],\"1498838400\":[],\"1496246400\":[],\"earliest_month\":1522512000}"
        val je = JsonParser().parse(json)
        val set = je.asJsonObject.entrySet()
        val gson = Gson()
        for ((key, value) in set) {
            println(value.toString())
            if (value.isJsonArray) {
                val list = gson.fromJson<List<CalendarItemSleepReport>>(
                        value.toString(),
                        object : TypeToken<List<CalendarItemSleepReport>>() {}.type
                )
                println(list!!.joinToString())
            }
        }
    }
}
