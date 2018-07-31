package com.sumian.hw.improve.report.weeklyreport;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.SleepDurationReport;

import java.util.List;

public interface WeeklyReportContact {

    interface View extends BaseNetView<Presenter> {

        void setReportsData(List<SleepDurationReport> reports);

        void updateReportData(SleepDurationReport reports);

        void insertReportDataAtHead(List<SleepDurationReport> reports);

        void showReportAtTime(long time);
    }


    interface Presenter extends BasePresenter {

        void getInitReports(long todayUnixTime);

        void getReportsAndGotoTargetTime(long startDayUnixTime, int pageSize, boolean include, long targetDayUnixTime);

        void refreshReport(long unixTime);

        void getPreloadReports(long unixTime);
    }
}
