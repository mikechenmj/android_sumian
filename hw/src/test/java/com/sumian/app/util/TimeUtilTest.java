package com.sumian.app.util;

import org.junit.Test;

import java.util.Date;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/2 9:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TimeUtilTest {
    @Test
    public void testTimeUtil() {
        System.out.println(new Date(1523721600 * 1000L).toString());
        System.out.println(new Date(1524240000 * 1000L).toString());
    }

}
