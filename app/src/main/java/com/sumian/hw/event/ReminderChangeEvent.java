package com.sumian.hw.event;

import com.sumian.hw.network.response.Reminder;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/1 18:50
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ReminderChangeEvent {
    public Reminder mReminder;

    public ReminderChangeEvent(Reminder reminder) {
        mReminder = reminder;
    }
}
