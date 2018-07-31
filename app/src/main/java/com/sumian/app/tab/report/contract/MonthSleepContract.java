package com.sumian.app.tab.report.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.SleepDurationReport;

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

        void release();
    }
}
