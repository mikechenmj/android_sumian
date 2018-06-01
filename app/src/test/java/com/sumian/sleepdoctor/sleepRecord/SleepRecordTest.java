package com.sumian.sleepdoctor.sleepRecord;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.sleepRecord.bean.FillSleepRecordResponse;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecord;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 21:27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordTest {
    @org.junit.Test
    public void testQuerySleepDataResponse() {
        String s = "{\"1525104000\":[{\"id\":258,\"date\":1525363200,\"is_today\":false,\"has_doctors_evaluation\":false},{\"id\":307,\"date\":1525622400,\"is_today\":false,\"has_doctors_evaluation\":false},{\"id\":447,\"date\":1526313600,\"is_today\":false,\"has_doctors_evaluation\":false},{\"id\":536,\"date\":1526832000,\"is_today\":false,\"has_doctors_evaluation\":false}],\"1522512000\":[{\"id\":1,\"date\":1523721600,\"is_today\":false,\"has_doctors_evaluation\":false},{\"id\":18,\"date\":1524672000,\"is_today\":false,\"has_doctors_evaluation\":false},{\"id\":118,\"date\":1524758400,\"is_today\":false,\"has_doctors_evaluation\":false},{\"id\":149,\"date\":1524844800,\"is_today\":false,\"has_doctors_evaluation\":false}],\"1519833600\":[],\"1517414400\":[],\"1514736000\":[],\"1512057600\":[],\"1509465600\":[],\"1506787200\":[],\"1504195200\":[],\"1501516800\":[]}";

        Map<String, List<SleepRecordSummary>> map = JsonUtil.fromJson(s, new TypeToken<Map<String, List<SleepRecordSummary>>>() {
        }.getType());
        for (Map.Entry<String, List<SleepRecordSummary>> entry : map.entrySet()) {
            String key = entry.getKey();
            long time = Integer.valueOf(key) * 1000L;
//            System.out.println(new Date(time));

            for (SleepRecordSummary sleepRecordSummary : entry.getValue()) {
                System.out.println(new Date(sleepRecordSummary.getDateInMillis()));
            }
        }
    }

    @Test
    public void testSleepData() {
        String response = "{\"code\":0,\"result\":{\"id\":838,\"date\":1527782400,\"answer\":{\"bed_at\":\"23:00\",\"sleep_at\":\"00:00\",\"wake_up_at\":\"01:00\",\"get_up_at\":\"01:00\",\"wake_times\":2,\"wake_minutes\":30,\"energetic\":0,\"sleepless_factor\":[\"饮酒\",\"喝茶\\/咖啡\",\"身体不适\",\"吃太饱\",\"有心事\",\"睡前运动过量\",\"陌生床\",\"今天太累\",\"天气太冷\",\"天气太热\"],\"other_sleep_times\":1,\"other_sleep_total_minutes\":15,\"sleep_pills\":[{\"name\":\"唑吡坦\",\"amount\":\"1片\",\"time\":\"早饭前／后\"},{\"name\":\"唑吡坦\",\"amount\":\"1片\",\"time\":\"午饭前／后\"},{\"name\":\"咪达唑仑\",\"amount\":\"1.75片\",\"time\":\"午饭前／后\"},{\"name\":\"硝西泮\",\"amount\":\"2.75片\",\"time\":\"午饭前／后\"},{\"name\":\"艾司唑仑\",\"amount\":\"2.75片\",\"time\":\"午饭前／后\"},{\"name\":\"艾司唑仑\",\"amount\":\"2.75片\",\"time\":\"午饭前／后\"},{\"name\":\"艾司唑仑\",\"amount\":\"2.75片\",\"time\":\"午饭前／后\"}],\"remark\":\"我昨晚睡得很好^_^\"},\"sleep_duration\":1800,\"fall_asleep_duration\":3600,\"sleep_efficiency\":25,\"doctor_evaluation\":\"\",\"created_at\":1527815172,\"updated_at\":1527821883}}";

        FillSleepRecordResponse fillSleepRecordResponse = JsonUtil.fromJson(response, FillSleepRecordResponse.class);
        System.out.println(fillSleepRecordResponse);
    }

    @Test
    public void testQueryDiaryDetail() {
        String json = "{\"id\":838,\"date\":1527782400,\"answer\":{\"bed_at\":\"23:00\",\"sleep_at\":\"00:00\",\"wake_up_at\":\"01:00\",\"get_up_at\":\"01:00\",\"wake_times\":2,\"wake_minutes\":30,\"energetic\":0,\"sleepless_factor\":[\"\\u996e\\u9152\",\"\\u559d\\u8336\\/\\u5496\\u5561\",\"\\u8eab\\u4f53\\u4e0d\\u9002\",\"\\u5403\\u592a\\u9971\",\"\\u6709\\u5fc3\\u4e8b\",\"\\u7761\\u524d\\u8fd0\\u52a8\\u8fc7\\u91cf\",\"\\u964c\\u751f\\u5e8a\",\"\\u4eca\\u5929\\u592a\\u7d2f\",\"\\u5929\\u6c14\\u592a\\u51b7\",\"\\u5929\\u6c14\\u592a\\u70ed\"],\"other_sleep_times\":1,\"other_sleep_total_minutes\":15,\"sleep_pills\":[{\"name\":\"\\u5511\\u5421\\u5766\",\"amount\":\"1\\u7247\",\"time\":\"\\u65e9\\u996d\\u524d\\uff0f\\u540e\"},{\"name\":\"\\u5511\\u5421\\u5766\",\"amount\":\"1\\u7247\",\"time\":\"\\u5348\\u996d\\u524d\\uff0f\\u540e\"},{\"name\":\"\\u54aa\\u8fbe\\u5511\\u4ed1\",\"amount\":\"1.75\\u7247\",\"time\":\"\\u5348\\u996d\\u524d\\uff0f\\u540e\"},{\"name\":\"\\u785d\\u897f\\u6cee\",\"amount\":\"2.75\\u7247\",\"time\":\"\\u5348\\u996d\\u524d\\uff0f\\u540e\"},{\"name\":\"\\u827e\\u53f8\\u5511\\u4ed1\",\"amount\":\"2.75\\u7247\",\"time\":\"\\u5348\\u996d\\u524d\\uff0f\\u540e\"},{\"name\":\"\\u827e\\u53f8\\u5511\\u4ed1\",\"amount\":\"2.75\\u7247\",\"time\":\"\\u5348\\u996d\\u524d\\uff0f\\u540e\"},{\"name\":\"\\u827e\\u53f8\\u5511\\u4ed1\",\"amount\":\"2.75\\u7247\",\"time\":\"\\u5348\\u996d\\u524d\\uff0f\\u540e\"}],\"remark\":\"\\u6211\\u6628\\u665a\\u7761\\u5f97\\u5f88\\u597d^_^\"},\"sleep_duration\":1800,\"fall_asleep_duration\":3600,\"sleep_efficiency\":25,\"doctor_evaluation\":\"\",\"created_at\":1527815172,\"updated_at\":1527821883}";

        SleepRecord sleepRecord = JsonUtil.fromJson(json, SleepRecord.class);
        System.out.println(sleepRecord);
    }
}
