package com.sumian.sleepdoctor.util;

import android.text.format.DateUtils;

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
    private long currentTime() {
        return System.currentTimeMillis();
    }

    @Test
    public void test() {
        System.out.println(TimeUtil.formatDate("M月d日", currentTime() + DateUtils.DAY_IN_MILLIS * 12));
    }
}
