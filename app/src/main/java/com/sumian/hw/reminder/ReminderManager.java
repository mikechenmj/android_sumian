package com.sumian.hw.reminder;

import com.sumian.hw.common.config.SumianConfig;
import com.sumian.hw.event.ReminderChangeEvent;
import com.sumian.hw.network.response.Reminder;
import com.sumian.sleepdoctor.event.EventBusUtil;

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
}
