package com.sumian.sleepdoctor.util;

import com.sumian.sleepdoctor.utils.TimeUtil;

import org.junit.Test;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/1 10:47
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TimeUtilTest {
    @Test
    public void test() {
        int second = 3600 * 0 + 60 * 2;
        System.out.println(TimeUtil.getHourMinuteStringFromSecondInZh(second));
        System.out.println(TimeUtil.getHourMinuteStringFromSecondInEn(second));
    }
}
