package com.sumian.sd;

import com.google.gson.reflect.TypeToken;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.h5.bean.H5BaseResponse;
import com.sumian.sd.utils.JsonUtil;

import org.junit.Test;

import java.util.Map;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 20:24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LittleTest {

    @Test
    public void test() {
        String s = "{\"showNavigationBar\":false}";
        Map<String, Object> map = JsonUtil.fromJson(s, new TypeToken<Map<String, Object>>() {
        }.getType());
        System.out.println(map);
        if (map == null) {
            return;
        }
        Object value = map.get("123");
        System.out.println(value);
        System.out.println(value instanceof Boolean);
    }
}
