package com.sumian.hw.account.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.response.Reminder;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/2.
 * <p>
 * desc:
 */

public interface SleepReminderContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncReminderSuccess(List<Reminder> reminders);

        void onSyncReminderFailed(String error);

        void onModifyReminderSuccess(Reminder reminder);

        void onModifyReminderFailed(String error);

        void onAddReminderSuccess();

        void onAddReminderFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void syncReminder();

        void modifyReminder(long unixTime, int isEnable);

        void addReminder(long unixTime, int isEnable);

        long formatUnixTime(String hour, String min);

    }
}
