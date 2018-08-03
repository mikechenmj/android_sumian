package com.sumian.hw.tab.report.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.SleepDurationReport;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface MonthSleepContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncMonthSleepReportSuccess(SleepDurationReport sleepDurationReport);

        void onSyncMonthSleepReportFailed(String error);

        void onSwitchMonthCallback(int year, int month);

        void onSwitchWeekFinalMonth(boolean isFinalMonth);

        void returnMaxDays(int maxDate);

    }


    interface Presenter extends BasePresenter {

        void doSyncMonthSleepReport();

        void doSyncPreMonthSleepReport();

        void doSyncNextMonthSleepReport();

        @Override
        void release();
    }
}
