package com.sumian.sd.util;

import android.text.format.DateUtils;

import com.sumian.sd.utils.TimeUtil;

import org.junit.Test;

import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 10:47
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TimeUtilTest {
    private long currentTime() {
        return System.currentTimeMillis();
    }

    @Test
    public void test() {
        System.out.println(TimeUtil.formatDate("M月d日", currentTime() + DateUtils.DAY_IN_MILLIS * 12));
    }

    @Test
    public void testRollMonth() {
        long startMonth = 1498838400000L;
        long l1 = startMonth % DateUtils.DAY_IN_MILLIS;
        System.out.println("DateUtils.DAY_IN_MILLIS:  " + DateUtils.DAY_IN_MILLIS);
        System.out.println("left:  " + l1);
        String pattern = "yyyy/MM/dd HH:mm:ss";
        System.out.println(TimeUtil.formatDate(pattern, 1498838400000L));
        System.out.println(TimeUtil.formatDate(pattern, TimeUtil.getStartTimeOfTheDay(startMonth)));
        System.out.println(TimeUtil.formatDate(pattern, TimeUtil.getStartDayOfMonth(startMonth).getTimeInMillis()));
        List<Long> monthTimes = TimeUtil.createMonthTimes(startMonth, 12, true);
        System.out.println(String.format(Locale.getDefault(), "count: %d", monthTimes.size()));
        for (Long l : monthTimes) {
            String s = TimeUtil.formatDate(pattern, l);
            System.out.println(s);
        }
    }

}
