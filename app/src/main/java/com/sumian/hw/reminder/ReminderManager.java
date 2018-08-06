package com.sumian.hw.reminder;

import com.sumian.hw.common.config.SumianConfig;
import com.sumian.hw.event.ReminderChangeEvent;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.response.Reminder;
import com.sumian.hw.network.response.ResultResponse;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.event.EventBusUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/1 18:55
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ReminderManager {
    public static void updateReminder(Reminder reminder) {
        EventBusUtil.postStickyEvent(new ReminderChangeEvent(reminder));
        SumianConfig.updateReminder(reminder);
    }

    public static void getReminder() {
        Map<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("per_page", 20);
        map.put("type", 1);

        Call<ResultResponse<Reminder>> call = AppManager
                .getHwNetEngine()
                .getHttpService().getReminder(map);

        call.enqueue(new BaseResponseCallback<ResultResponse<Reminder>>() {
            @Override
            protected void onSuccess(ResultResponse<Reminder> response) {
                List<Reminder> data = response.getData();
                ReminderManager.updateReminder(data == null || data.isEmpty() ? null : (data.get(0)));
            }

            @Override
            protected void onFailure(int code, String error) {
            }

            @Override
            protected void onFinish() {
            }
        });
    }
}
