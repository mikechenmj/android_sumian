package com.sumian.sleepdoctor;

import org.junit.Test;

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


        String s1 = "{\"country\":\"中国\",\"unionid\":\"oQT0F0rlH7sbHKmpfWFNg-0xYB34\",\"gender\":\"男\",\"city\":\"深圳\",\"openid\":\"ouV7E1SWfPLo3pzHtflGSXpS3Xl4\",\"language\":\"zh_CN\",\"profile_image_url\":\"[图片]http://thirdwx.qlogo.cn/mmopen/vi_32/rBdyicTIUVrNUC8ov8AkYTrcmJtLyrZ4l3ade0QcZy9mFZ7THfYj6ZoSTnxxJtibFHBaGAMpgribQ9j5ZN3xojsvg/132\",\"accessToken\":\"10_guvcdl7JIG2he-MdCJn0g5OoWCj48uRvCwTn8EbJUsXa8tf-mxdO7oVeJlUQaJUg4pqVh_tIZ4E74t5hSGOZWQ\",\"access_token\":\"10_guvcdl7JIG2he-MdCJn0g5OoWCj48uRvCwTn8EbJUsXa8tf-mxdO7oVeJlUQaJUg4pqVh_tIZ4E74t5hSGOZWQ\",\"uid\":\"oQT0F0rlH7sbHKmpfWFNg-0xYB34\",\"province\":\"广东\",\"screen_name\":\"詹徐照\",\"name\":\"詹徐照\",\"iconurl\":\"[图片]http://thirdwx.qlogo.cn/mmopen/vi_32/rBdyicTIUVrNUC8ov8AkYTrcmJtLyrZ4l3ade0QcZy9mFZ7THfYj6ZoSTnxxJtibFHBaGAMpgribQ9j5ZN3xojsvg/132\",\"nickname\":\"詹徐照\",\"expiration\":\"1527522430658\",\"expires_in\":\"1527522430658\",\"refreshToken\":\"10_2tAwf9idGaMdfpI4Fm0JMhMqQhp6_EAHBpFGD_sWM3FQuY9HOjA0Fy_7TKKSJqqMswxnXjkv5jif93QGsAwxYQ\"}";

        s1 = s1.replace(",", ",\n");
        System.out.println(s1);
    }
}
