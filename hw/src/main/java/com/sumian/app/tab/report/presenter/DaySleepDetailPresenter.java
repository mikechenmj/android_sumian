package com.sumian.app.tab.report.presenter;

import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.network.api.SleepyApi;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.SleepDetailReport;
import com.sumian.app.tab.report.bean.SleepData;
import com.sumian.app.tab.report.contract.DaySleepDetailContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class DaySleepDetailPresenter implements DaySleepDetailContract.Presenter {

    private WeakReference<DaySleepDetailContract.View> mViewWeakReference;
    private SleepyApi mSleepyApi;

    private DaySleepDetailPresenter(DaySleepDetailContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mSleepyApi = AppManager
            .getNetEngine()
            .getHttpService();

    }

    public static void init(DaySleepDetailContract.View view) {
        new DaySleepDetailPresenter(view);
    }


    @Override
    public void doSyncDaySleepDetailReport(long id) {
        DaySleepDetailContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        Call<SleepDetailReport> daySleepReportCall = this.mSleepyApi.syncSleepDetail(id);

        daySleepReportCall.enqueue(new BaseResponseCallback<SleepDetailReport>() {
            @Override
            protected void onSuccess(SleepDetailReport response) {
                view.onSyncDaySleepDetailReportSuccess(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onSyncDaySleepDetailReportFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

    @Override
    public List<SleepData> transform2SleepData(SleepDetailReport sleepDetailReport) {

        List<SleepData> sleepDataList = new ArrayList<>();

        SleepDetailReport.SleepPackage packages = sleepDetailReport.getPackages();

        List<SleepDetailReport.SleepItem> sleepItems = packages.getData();
        if (sleepItems == null || sleepItems.isEmpty()) {
            ToastHelper.show(R.string.none_sleep_hint);
            return null;
        }

        for (SleepDetailReport.SleepItem sleepItem : sleepItems) {

            int fromTime = sleepItem.getFrom_time();
            int toTime = sleepItem.getTo_time();
            int timeQuantum = (int) ((toTime - fromTime) / 60.0f + 0.5f);

            SleepData sleepData = new SleepData()
                .setSleepItem(sleepItem)
                .setTimeQuantum(timeQuantum);

            sleepDataList.add(sleepData);
        }

        return sleepDataList;
    }

    @Override
    public void release() {

    }
}
