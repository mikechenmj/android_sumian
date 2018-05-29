package com.sumian.sleepdoctor;

import com.sumian.sleepdoctor.utils.TimeUtil;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 20:24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LittleTest {

    @Test
    public void test() {
        long time = System.currentTimeMillis();
        Calendar calendar = TimeUtil.getCalendar(time);
        System.out.println(new Date());
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.roll(Calendar.DAY_OF_YEAR, -2);
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
    }
}
