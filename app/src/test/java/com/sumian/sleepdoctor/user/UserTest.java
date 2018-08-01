package com.sumian.sleepdoctor.user;

import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/1 11:07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class UserTest {
    @Test
    public void test() {
        String ageJson = "{\"age\":null}";
        Age data = JsonUtil.fromJson(ageJson, Age.class);
        System.out.println(data);

    }

    class Age {
        public Double age;

        @Override
        public String toString() {
            return "Age{" +
                    "age=" + age +
                    '}';
        }
    }

    private String getJson() {
        return "{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NtYXBpLWRldi5zdW1pYW4uY29tL2F1dGhvcml6YXRpb25zIiwiaWF0IjoxNTMzMDkyNjgwLCJleHAiOjE1MzU2ODQ2ODAsIm5iZiI6MTUzMzA5MjY4MCwianRpIjoiSHY4WDhmbUMyaUJHMlNaMSIsInN1YiI6MjEwMiwicHJ2IjoiMjNiZDVjODk0OWY2MDBhZGIzOWU3MDFjNDAwODcyZGI3YTU5NzZmNyJ9.pqLkLztAfA9_1fsVexXWe7uYY2ievHeusEbIMgCIXH4\",\"expired_at\":1535684680,\"refresh_expired_at\":1538276680,\"user\":{\"id\":2102,\"mobile\":\"13570464488\",\"nickname\":\"\\u8a79\\u5f90\\u71671\",\"name\":\"111\",\"avatar\":\"https:\\/\\/sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com\\/avatar\\/2102\\/92f7056c-0cb3-4eaa-9ed3-bc8777ea28d5.jpg\",\"area\":\"\\u672a\\u8bbe\\u7f6e\",\"gender\":\"female\",\"birthday\":\"1990-06\",\"age\":28,\"height\":\"170.0\",\"weight\":\"52.0\",\"bmi\":\"18.0\",\"leancloud_id\":\"sumian-production-8aa1d5fa-af7a-4122-956a-7114bee94655\",\"doctor_id\":1,\"recommend_doctor_id\":0,\"bound_at\":1527645935,\"last_login_at\":\"2018-07-31 18:24:00\",\"device_info\":\"{\\\"app_version\\\":\\\"1.6.0-dev\\\",\\\"model\\\":\\\"Xiaomi MIX 2\\\",\\\"monitor_fw\\\":\\\"\\\",\\\"monitor_sn\\\":\\\"\\\",\\\"sleeper_fw\\\":\\\"\\\",\\\"sleeper_sn\\\":\\\"\\\",\\\"system\\\":\\\"Android 8.0.0\\\"}\",\"monitor_sn\":\"A88888888888\",\"sleeper_sn\":\"C99999999999\",\"career\":\"\",\"education\":\"\\u672c\\u79d1\\u6216\\u4ee5\\u4e0a\",\"created_at\":\"2018-06-04 15:41:17\",\"updated_at\":\"2018-07-31 18:24:00\",\"answers\":{\"id\":49,\"answers\":\"23:00,0,07:00,07:00,0,0,0\",\"score\":1,\"level\":0,\"created_at\":1525651696},\"socialites\":[],\"im_id\":\"develop8232e119d8f59aa83050a741631803a6\",\"im_password\":\"83d5b11837b0921f7ee745d5b5545cc1\"}}";
    }
}
