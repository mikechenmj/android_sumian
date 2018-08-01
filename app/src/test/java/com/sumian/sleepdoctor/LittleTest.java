package com.sumian.sleepdoctor;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.h5.bean.H5BaseResponse;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

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
        String data = "";
        H5BaseResponse<UserInfo> o = JsonUtil.fromJson(data, new TypeToken<H5BaseResponse<UserInfo>>() {
        }.getType());
    }
}
