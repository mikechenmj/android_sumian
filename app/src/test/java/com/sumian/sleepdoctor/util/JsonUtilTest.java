package com.sumian.sleepdoctor.util;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 21:17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class JsonUtilTest {

    @Test
    public void test() {
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", 2);
        map.put("key3", 3);
        String s = JsonUtil.toJson(map);
        System.out.println(s);
        Map<String, Integer> m2 = JsonUtil.fromJson(s, new TypeToken<Map<String, Integer>>() {
        }.getType());
        assert m2 != null;
        Integer value1 = m2.get("key1");
        System.out.println(value1);
        System.out.println(1);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        String listStr = JsonUtil.toJson(list);
        System.out.println(listStr);
        List<Integer> list2 = JsonUtil.fromJson(listStr, new TypeToken<List<Integer>>() {
        }.getType());
        System.out.println(list2);
    }
}
