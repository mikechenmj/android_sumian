package com.sumian.hw.tab.report.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.network.response.DaySleepReport;
import com.sumian.hw.network.response.ResultResponse;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface DaySleepContract {

    interface View extends HwBaseNetView<Presenter> {

        void onSyncDaySleepReportSuccess(ResultResponse<DaySleepReport> daySleepReport);

        void onSyncDaySleepReportFailed(String error);

    }


    interface Presenter extends HwBasePresenter {

        void doSyncDaySleepReport();

        void doSyncNextDaySleepReport();

        @Override
        void release();
    }
}
