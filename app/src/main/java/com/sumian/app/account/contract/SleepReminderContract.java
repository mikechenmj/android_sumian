package com.sumian.app.account.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.Reminder;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/2.
 * <p>
 * desc:
 */

public interface SleepReminderContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncReminderSuccess(List<Reminder> reminders);

        void onSyncReminderFailed(String error);

        void onModifyReminderSuccess(Reminder reminder);

        void onModifyReminderFailed(String error);

        void onAddReminderSuccess();

        void onAddReminderFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void syncReminder();

        void modifyReminder(long unixTime, int isEnable);

        void addReminder(long unixTime, int isEnable);

        long formatUnixTime(String hour, String min);

    }
}
