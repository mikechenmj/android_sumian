package com.sumian.hw.tab.report.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.response.SleepDetailReport;
import com.sumian.hw.tab.report.bean.SleepData;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface DaySleepDetailContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncDaySleepDetailReportSuccess(SleepDetailReport sleepDetailReport);

        void onSyncDaySleepDetailReportFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void doSyncDaySleepDetailReport(long id);

        List<SleepData> transform2SleepData(SleepDetailReport sleepDetailReport);

        @Override
        void release();
    }
}
