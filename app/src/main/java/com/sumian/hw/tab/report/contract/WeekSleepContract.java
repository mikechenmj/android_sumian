package com.sumian.hw.tab.report.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.SleepDurationReport;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface WeekSleepContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncWeekSleepReportSuccess(SleepDurationReport sleepDurationReport);

        void onSyncWeekSleepReportFailed(String error);

        void onSwitchWeekCallback(int preYear, int preMonth, int preDate, int nextYear, int nextMonth, int nextDate);

        void onSwitchWeekFinalDate(boolean isFinalDate);

    }


    interface Presenter extends BasePresenter {

        void doSyncWeekSleepReport();

        void doSyncPreWeekSleepReport();

        void doSyncNextWeekSleepReport();

        void release();
    }
}
