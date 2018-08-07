package com.sumian.hw.improve.report.dailyreport;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;

import java.util.List;

/**
 * Created by sm
 * on 2018/3/9.
 * desc:
 */

public interface DailyReportContract {

    interface View extends HwBaseNetView<Presenter> {

        void setReportsData(List<DailyReport> dailyReports);

        void updateReportData(DailyReport dailyReport);

        void insertReportDataAtHead(List<DailyReport> dailyReports);

        void showReportAtTime(long unixTime);
    }

    interface Presenter extends HwBasePresenter {

        void getInitReports(long todayUnixTime);

        void getReportsAndGotoTargetTime(long startDayUnixTime, int pageSize, boolean include, long targetDayUnixTime);

        void refreshReport(long unixTime);

        void getPreloadReports(long unixTime);
    }
}
