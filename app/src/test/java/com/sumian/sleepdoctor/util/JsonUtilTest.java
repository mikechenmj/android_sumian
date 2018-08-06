package com.sumian.sleepdoctor.util;

import com.sumian.hw.network.response.ErrorResponse;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 21:17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class JsonUtilTest {

    @Test
    public void test() {
        String json = "{\"message\":\"Not Found\",\"status_code\":404}";
        ErrorResponse errorResponse = JsonUtil.fromJson(json, ErrorResponse.class);
        System.out.println(errorResponse);
    }
}
