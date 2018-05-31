package com.sumian.sleepdoctor.sleepRecord;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepData;
import com.sumian.sleepdoctor.utils.JsonUtil;

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

        Map<String, List<SleepData>> map = JsonUtil.fromJson(s, new TypeToken<Map<String, List<SleepData>>>() {
        }.getType());
        for (Map.Entry<String, List<SleepData>> entry : map.entrySet()) {
            String key = entry.getKey();
            long time = Integer.valueOf(key) * 1000L;
//            System.out.println(new Date(time));

            for (SleepData sleepData : entry.getValue()) {
                System.out.println(new Date(sleepData.getDateInMillis()));
            }
        }
    }
}
