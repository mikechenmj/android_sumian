package com.sumian.app.tab.report.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.network.response.SleepDetailReport;
import com.sumian.app.tab.report.bean.SleepData;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public interface DaySleepDetailContract {

    interface View extends BaseNetView<Presenter> {

        void onSyncDaySleepDetailReportSuccess(SleepDetailReport sleepDetailReport);

        void onSyncDaySleepDetailReportFailed(String error);

    }


    interface Presenter extends BasePresenter {

        void doSyncDaySleepDetailReport(long id);

        List<SleepData> transform2SleepData(SleepDetailReport sleepDetailReport);

        void release();
    }
}
