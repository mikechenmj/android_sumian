package com.sumian.sleepdoctor.util;

import com.sumian.hw.network.response.ErrorResponse;
import com.sumian.sleepdoctor.account.bean.Answers;
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
        String json = "{\"id\":93,\"answers\":\"22:00,3,07:00,07:00,3,3,1\",\"score\":11,\"level\":2,\"created_at\":1533544680}";
        Answers errorResponse = JsonUtil.fromJson(json, Answers.class);
        System.out.println(errorResponse);
    }
}
