package com.sumian.sleepdoctor.notification;

import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 14:40
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationTest {
    @Test
    public void test() {
        String json = "{\"id\":\"6a1b932e-fb04-4aee-b1e9-5632ba0e8dca\",\"type\":\"App\\\\Notifications\\\\DiaryEvaluated\",\"data\":{\"id\":838,\"date\":1527782400,\"tittle\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"title\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u5bf9\\u60a86\\u67081\\u65e5\\u7684\\u65e5\\u8bb0\\u8fdb\\u884c\\u53cd\\u9988\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"scheme\":\"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3D6a1b932e-fb04-4aee-b1e9-5632ba0e8dca%26user_id%3D2102\"},\"read_at\":null,\"created_at\":1528178108}";

        Notification notification = JsonUtil.fromJson(json, Notification.class);
        System.out.println(notification);
    }
}
