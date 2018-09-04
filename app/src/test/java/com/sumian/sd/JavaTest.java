package com.sumian.sd;

import org.junit.Test;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/4 21:26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class JavaTest {
    @Test
    public void test() {
        Object o = "1";
        System.out.println(o instanceof String);
        o = 'c';
        System.out.println(o instanceof Character);
        o = 1;
        System.out.println(o instanceof Integer);
        o = true;
        System.out.println(o instanceof Boolean);
    }
}
