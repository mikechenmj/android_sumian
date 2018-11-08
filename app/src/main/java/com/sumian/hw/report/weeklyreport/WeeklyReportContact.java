package com.sumian.hw.report.weeklyreport;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.report.weeklyreport.bean.SleepDurationReport;

import java.util.List;

public interface WeeklyReportContact {

    interface View extends HwBaseNetView<Presenter> {

        void setReportsData(List<SleepDurationReport> reports);

        void updateReportData(SleepDurationReport reports);

        void insertReportDataAtHead(List<SleepDurationReport> reports);

        void showReportAtTime(long time);
    }


    interface Presenter extends HwBasePresenter {

        void getInitReports(long todayUnixTime);

        void getReportsAndGotoTargetTime(long startDayUnixTime, int pageSize, boolean include, long targetDayUnixTime);

        void refreshReport(long unixTime);

        void getPreloadReports(long unixTime);
    }
}
