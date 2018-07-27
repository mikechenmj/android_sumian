package com.sumian.app.tab.report.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.DaySleepReport;
import com.sumian.app.network.response.ResultResponse;

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
