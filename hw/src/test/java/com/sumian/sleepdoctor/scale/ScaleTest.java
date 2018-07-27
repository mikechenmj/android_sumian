package com.sumian.sleepdoctor.scale;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.network.response.PaginationResponse;
import com.sumian.sleepdoctor.scale.bean.Scale;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 9:41
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ScaleTest {
    @Test
    public void test() {
        String s = "{\"data\":[{\"id\":339,\"doctor_id\":0,\"scale_result_id\":0,\"created_at\":1528265455,\"scale\":{\"id\":2,\"doctor_id\":0,\"score_type\":1,\"title\":\"Epworth\\u55dc\\u7761\\u91cf\\u8868\",\"description\":\"\\u8be5\\u91cf\\u8868\\u662f\\u7531\\u6fb3\\u5927\\u5229\\u4e9a\\u58a8\\u5c14\\u672c\\u7684Epworth\\u533b\\u9662\\u8bbe\\u8ba1\\u7684\\u3002\\u4e34\\u5e8a\\u5e94\\u7528\\u7ed3\\u679c\\u8868\\u660e\\uff0cESS\\u662f\\u4e00\\u79cd\\u5341\\u5206\\u7b80\\u4fbf\\u7684\\u60a3\\u8005\\u81ea\\u6211\\u8bc4\\u4f30\\u767d\\u5929\\u55dc\\u7761\\u7a0b\\u5ea6\\u7684\\u95ee\\u5377\\u8868\\uff0c\\u8bf7\\u6839\\u636e\\u6700\\u8fd1\\u51e0\\u4e2a\\u6708\\u7684\\u4e00\\u822c\\u72b6\\u51b5\\u8fdb\\u884c\\u9009\\u62e9\\u3002\",\"final_words\":\"\\u60a8\\u5df2\\u5b8c\\u6210\\u91cf\\u8868\\u7684\\u586b\\u5199\"},\"result\":null,\"doctor\":null},{\"id\":234,\"doctor_id\":1,\"scale_result_id\":0,\"created_at\":1528178357,\"scale\":{\"id\":1,\"doctor_id\":0,\"score_type\":1,\"title\":\"\\u5e7f\\u6cdb\\u6027\\u7126\\u8651\\u91cf\\u8868\\uff08GAD-7\\uff09\",\"description\":\"\\u5728\\u8fc7\\u53bb\\u4e24\\u4e2a\\u661f\\u671f\\uff0c\\u6709\\u591a\\u5c11\\u65f6\\u5019\\u60a8\\u4f1a\\u53d7\\u5230\\u5982\\u4e0b\\u51e0\\u4e2a\\u95ee\\u9898\\u7684\\u56f0\\u6270\\uff1f\",\"final_words\":\"\\u60a8\\u5df2\\u5b8c\\u6210\\u91cf\\u8868\\u7684\\u586b\\u5199\"},\"result\":null,\"doctor\":{\"id\":1,\"name\":\"\\u901f\\u7720\\u533b\\u751f\"}},{\"id\":223,\"doctor_id\":1,\"scale_result_id\":27,\"created_at\":1527645962,\"scale\":{\"id\":1,\"doctor_id\":0,\"score_type\":1,\"title\":\"\\u5e7f\\u6cdb\\u6027\\u7126\\u8651\\u91cf\\u8868\\uff08GAD-7\\uff09\",\"description\":\"\\u5728\\u8fc7\\u53bb\\u4e24\\u4e2a\\u661f\\u671f\\uff0c\\u6709\\u591a\\u5c11\\u65f6\\u5019\\u60a8\\u4f1a\\u53d7\\u5230\\u5982\\u4e0b\\u51e0\\u4e2a\\u95ee\\u9898\\u7684\\u56f0\\u6270\\uff1f\",\"final_words\":\"\\u60a8\\u5df2\\u5b8c\\u6210\\u91cf\\u8868\\u7684\\u586b\\u5199\"},\"result\":{\"id\":27,\"score\":0,\"result\":\"\\u6b63\\u5e38\",\"comment\":\"\\u60a8\\u7684\\u60c5\\u51b5\\u6b63\\u5e38\\u3002\",\"created_at\":1527645977,\"updated_at\":1527645977},\"doctor\":{\"id\":1,\"name\":\"\\u901f\\u7720\\u533b\\u751f\"}},{\"id\":222,\"doctor_id\":0,\"scale_result_id\":0,\"created_at\":1527645924,\"scale\":{\"id\":1,\"doctor_id\":0,\"score_type\":1,\"title\":\"\\u5e7f\\u6cdb\\u6027\\u7126\\u8651\\u91cf\\u8868\\uff08GAD-7\\uff09\",\"description\":\"\\u5728\\u8fc7\\u53bb\\u4e24\\u4e2a\\u661f\\u671f\\uff0c\\u6709\\u591a\\u5c11\\u65f6\\u5019\\u60a8\\u4f1a\\u53d7\\u5230\\u5982\\u4e0b\\u51e0\\u4e2a\\u95ee\\u9898\\u7684\\u56f0\\u6270\\uff1f\",\"final_words\":\"\\u60a8\\u5df2\\u5b8c\\u6210\\u91cf\\u8868\\u7684\\u586b\\u5199\"},\"result\":null,\"doctor\":null}],\"meta\":{\"pagination\":{\"total\":4,\"count\":4,\"per_page\":15,\"current_page\":1,\"total_pages\":1,\"links\":[]}}}";

        PaginationResponse<List<Scale>> data = JsonUtil.fromJson(s, new TypeToken<PaginationResponse<Scale>>() {
        }.getType());
        System.out.println(data);
    }
}
