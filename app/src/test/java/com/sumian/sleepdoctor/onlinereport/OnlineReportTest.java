package com.sumian.sleepdoctor.onlineReport;

import com.google.gson.reflect.TypeToken;
import com.sumian.sleepdoctor.network.response.PaginationResponse;
import com.sumian.sleepdoctor.utils.JsonUtil;

import org.junit.Test;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:42
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReportTest {
    @Test
    public void test() {
        String s = "{\"data\":[{\"id\":3,\"title\":\"\\u7761\\u7720\\u76d1\\u6d4b\\u62a5\\u544a\",\"conversion_status\":1,\"task_id\":\"\",\"report_url\":\"https:\\/\\/sleep-doctor-imm-test.oss-cn-shanghai.aliyuncs.com\\/doctors\\/online_report\\/3\\/2c2523ef-7115-4dd8-9adc-02d075b25ecf.pdf\",\"deleted_at\":null,\"created_at\":1528078773,\"updated_at\":1528078773},{\"id\":2,\"title\":\"\\u7761\\u7720\\u76d1\\u6d4b\\u62a5\\u544a\",\"conversion_status\":1,\"task_id\":\"\",\"report_url\":\"https:\\/\\/sleep-doctor-imm-test.oss-cn-shanghai.aliyuncs.com\\/doctors\\/online_report\\/3\\/34d5fcfe-6785-43ee-bbe1-b86a3f71b0bf.pdf\",\"deleted_at\":null,\"created_at\":1528078554,\"updated_at\":1528078554}],\"meta\":{\"pagination\":{\"total\":2,\"count\":2,\"per_page\":15,\"current_page\":1,\"total_pages\":1,\"links\":[]}}}";

        PaginationResponse<List<OnlineReport>> data = JsonUtil.fromJson(s, new TypeToken<PaginationResponse<List<OnlineReport>>>() {

        }.getType());

        System.out.println(data);
    }
}
