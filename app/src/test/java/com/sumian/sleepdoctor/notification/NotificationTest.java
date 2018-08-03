package com.sumian.sleepdoctor.notification;

import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.notification.bean.QueryNotificationResponse;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 14:40
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationTest {
    @Test
    public void test() {
        String json = "{\"id\":\"6a1b932e-fb04-4aee-b1e9-5632ba0e8dca\",\"type\":\"HwApp\\\\Notifications\\\\DiaryEvaluated\",\"data\":{\"id\":838,\"date\":1527782400,\"tittle\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"title\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u5bf9\\u60a86\\u67081\\u65e5\\u7684\\u65e5\\u8bb0\\u8fdb\\u884c\\u53cd\\u9988\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"scheme\":\"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3D6a1b932e-fb04-4aee-b1e9-5632ba0e8dca%26user_id%3D2102\"},\"read_at\":null,\"created_at\":1528178108}";

        Notification notification = JsonUtil.fromJson(json, Notification.class);
        System.out.println(notification);
    }

    @Test
    public void test2() {
        String json = "{\"data\":[{\"id\":\"cce3a3b8-40cd-4003-b8f5-4cbdcf4c8d28\",\"type\":\"HwApp\\\\Notifications\\\\OnlineReportGet\",\"data\":{\"id\":3,\"title\":\"\\u7535\\u5b50\\u62a5\\u544a\\u66f4\\u65b0\",\"content\":\"\\u60a8\\u7684\\u7535\\u5b50\\u62a5\\u544a\\u66f4\\u65b0\\u4e86\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"report_url\":\"https:\\/\\/sleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com\\/doctors\\/online_report\\/1\\/fc9c9a81-4aa4-4d4e-afc0-f1da37d5f7c4.pdf\",\"scheme\":\"sleepdoctor%3A%2F%2Fonline-reports%3Fid%3D3%26url%3Dhttps%3A%2F%2Fsleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com%2Fdoctors%2Fonline_report%2F1%2Ffc9c9a81-4aa4-4d4e-afc0-f1da37d5f7c4.pdf%26notification_id%3Dcce3a3b8-40cd-4003-b8f5-4cbdcf4c8d28%26user_id%3D2102\"},\"read_at\":1528187515,\"created_at\":1528185484},{\"id\":\"9c9e16e4-3239-4fc2-a4ec-2f7420e42c2b\",\"type\":\"HwApp\\\\Notifications\\\\OnlineReportGet\",\"data\":{\"id\":2,\"title\":\"\\u7535\\u5b50\\u62a5\\u544a\\u66f4\\u65b0\",\"content\":\"\\u60a8\\u7684\\u7535\\u5b50\\u62a5\\u544a\\u66f4\\u65b0\\u4e86\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"report_url\":\"https:\\/\\/sleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com\\/doctors\\/online_report\\/1\\/31aafa00-f569-4e0f-85b0-34033bc1e3c9.pdf\",\"scheme\":\"sleepdoctor%3A%2F%2Fonline-reports%3Fid%3D2%26url%3Dhttps%3A%2F%2Fsleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com%2Fdoctors%2Fonline_report%2F1%2F31aafa00-f569-4e0f-85b0-34033bc1e3c9.pdf%26notification_id%3D9c9e16e4-3239-4fc2-a4ec-2f7420e42c2b%26user_id%3D2102\"},\"read_at\":1528248367,\"created_at\":1528185456},{\"id\":\"a8b8410a-8e4d-464c-a76f-18c3a958e6a6\",\"type\":\"HwApp\\\\Notifications\\\\DiaryEvaluated\",\"data\":{\"id\":840,\"date\":1525449600,\"tittle\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"title\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u5bf9\\u60a85\\u67085\\u65e5\\u7684\\u65e5\\u8bb0\\u8fdb\\u884c\\u53cd\\u9988\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"scheme\":\"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1525449600%26notification_id%3Da8b8410a-8e4d-464c-a76f-18c3a958e6a6%26user_id%3D2102\"},\"read_at\":1528194969,\"created_at\":1528184418},{\"id\":\"08c87fa2-13e5-4aad-9cbf-3829d0bd7f9e\",\"type\":\"HwApp\\\\Notifications\\\\ScaleDistribution\",\"data\":{\"id\":234,\"title\":\"\\u533b\\u751f\\u53d1\\u9001\\u4e86\\u65b0\\u7684\\u91cf\\u8868\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u7ed9\\u60a8\\u53d1\\u9001\\u4e86\\u5e7f\\u6cdb\\u6027\\u7126\\u8651\\u91cf\\u8868\\uff08GAD-7\\uff09\\u91cf\\u8868\\uff0c\\u70b9\\u51fb\\u53bb\\u6d4b\\u8bc4\",\"scheme\":\"sleepdoctor%3A%2F%2Fscale-distributions%3Fid%3D234%26notification_id%3D08c87fa2-13e5-4aad-9cbf-3829d0bd7f9e%26user_id%3D2102\"},\"read_at\":1528180526,\"created_at\":1528178359},{\"id\":\"6a1b932e-fb04-4aee-b1e9-5632ba0e8dca\",\"type\":\"HwApp\\\\Notifications\\\\DiaryEvaluated\",\"data\":{\"id\":838,\"date\":1527782400,\"tittle\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"title\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u5bf9\\u60a86\\u67081\\u65e5\\u7684\\u65e5\\u8bb0\\u8fdb\\u884c\\u53cd\\u9988\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"scheme\":\"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3D6a1b932e-fb04-4aee-b1e9-5632ba0e8dca%26user_id%3D2102\"},\"read_at\":1528180526,\"created_at\":1528178108},{\"id\":\"50bd53d2-02d0-4e10-91ae-17a91eedd730\",\"type\":\"HwApp\\\\Notifications\\\\DiaryEvaluated\",\"data\":{\"id\":843,\"date\":1527955200,\"tittle\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"title\":\"\\u533b\\u751f\\u5efa\\u8bae\\u66f4\\u65b0\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u5bf9\\u60a86\\u67083\\u65e5\\u7684\\u65e5\\u8bb0\\u8fdb\\u884c\\u53cd\\u9988\\uff0c\\u70b9\\u51fb\\u67e5\\u770b\\u3002\",\"scheme\":\"sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527955200%26notification_id%3D50bd53d2-02d0-4e10-91ae-17a91eedd730%26user_id%3D2102\"},\"read_at\":1528180526,\"created_at\":1528178011},{\"id\":\"2bfceac5-4ad1-4eb1-8edd-75be146639fb\",\"type\":\"HwApp\\\\Notifications\\\\ScaleDistribution\",\"data\":{\"id\":223,\"title\":\"\\u533b\\u751f\\u53d1\\u9001\\u4e86\\u65b0\\u7684\\u91cf\\u8868\",\"content\":\"\\u901f\\u7720\\u533b\\u751f\\u533b\\u751f\\u7ed9\\u60a8\\u53d1\\u9001\\u4e86\\u5e7f\\u6cdb\\u6027\\u7126\\u8651\\u91cf\\u8868\\uff08GAD-7\\uff09\\u91cf\\u8868\\uff0c\\u70b9\\u51fb\\u53bb\\u6d4b\\u8bc4\",\"scheme\":\"sleepdoctor%3A%2F%2Fscale-distributions%3Fid%3D223%26notification_id%3D2bfceac5-4ad1-4eb1-8edd-75be146639fb%26user_id%3D103\"},\"read_at\":1528180526,\"created_at\":1527645963}],\"meta\":{\"pagination\":{\"total\":7,\"count\":7,\"per_page\":\"15\",\"current_page\":1,\"total_pages\":1,\"links\":{\"previous\":null,\"next\":null}},\"unread_num\":0}}";

        QueryNotificationResponse data = JsonUtil.fromJson(json, QueryNotificationResponse.class);
        System.out.println(data);
    }
}
