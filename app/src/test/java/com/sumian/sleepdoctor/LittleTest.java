package com.sumian.sleepdoctor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
//        Long l1 = 10000000L;
//        Long l2 = 10000000L;
//        Long l3 = 1L;
//        Long l4 = 1L;
//        System.out.println(l1 ==  l2);
//        System.out.println(l3 ==  l4);

//        for (long i = -200; i < 200; i++) {
//            Long l1 = i;
//            Long l2 = i;
//            String format = String.format(Locale.getDefault(), "%d: %b", i, l1 == l2);
//            System.out.println(format);
//        }

        List<Long> list = new ArrayList<>();
        list.add(1000L);
        System.out.println(list.contains(1000L));
    }
}
