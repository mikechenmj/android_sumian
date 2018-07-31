package com.sumian.hw.tab.report.contract;

import com.sumian.hw.base.BaseNetView;
import com.sumian.hw.base.BasePresenter;
import com.sumian.hw.network.response.DaySleepReport;
import com.sumian.hw.network.response.ResultResponse;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public interface DaySleepContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncDaySleepReportSuccess(ResultResponse<DaySleepReport> daySleepReport);

        void onSyncDaySleepReportFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doSyncDaySleepReport();

        void doSyncNextDaySleepReport();

        void release();
    }
}
