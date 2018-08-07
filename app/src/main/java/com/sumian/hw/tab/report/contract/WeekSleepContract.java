package com.sumian.hw.tab.report.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.response.SleepDurationReport;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface WeekSleepContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncWeekSleepReportSuccess(SleepDurationReport sleepDurationReport);

        void onSyncWeekSleepReportFailed(String error);

        void onSwitchWeekCallback(int preYear, int preMonth, int preDate, int nextYear, int nextMonth, int nextDate);

        void onSwitchWeekFinalDate(boolean isFinalDate);

    }


    interface Presenter extends HwBasePresenter {

        void doSyncWeekSleepReport();

        void doSyncPreWeekSleepReport();

        void doSyncNextWeekSleepReport();

        @Override
        void release();
    }
}
