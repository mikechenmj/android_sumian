package com.sumian.hw.tab.report.presenter;

import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.command.BlueCmd;
import com.sumian.hw.constant.Constant;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.response.DaySleepReport;
import com.sumian.hw.network.response.ResultResponse;
import com.sumian.hw.tab.report.contract.DaySleepContract;
import com.sumian.blue.model.BluePeripheral;

import java.lang.ref.WeakReference;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class DaySleepPresenter implements DaySleepContract.Presenter {

    private WeakReference<DaySleepContract.View> mViewWeakReference;
    private SleepyApi mSleepyApi;
    private int mPagerIndex = 1;

    private DaySleepPresenter(DaySleepContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mSleepyApi = HwAppManager
            .getNetEngine()
            .getHttpService();
    }

    public static void init(DaySleepContract.View view) {
        new DaySleepPresenter(view);
    }

    @Override
    public void doSyncDaySleepReport() {
        requestDispatcher(1);
        HwAppManager.getJobScheduler().checkJobScheduler();
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral == null || !bluePeripheral.isConnected()) {
            return;
        }
        writeSyncSleepDataCmd(bluePeripheral);
    }

    private void writeSyncSleepDataCmd(BluePeripheral bluePeripheral) {
        bluePeripheral.write(BlueCmd.cSleepData());
    }

    @Override
    public void doSyncNextDaySleepReport() {
        requestDispatcher(mPagerIndex++);
    }

    @Override
    public void release() {
    }

    private void requestDispatcher(int pagerIndex) {
        DaySleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        view.onBegin();

        Call<ResultResponse<DaySleepReport>> call = this.mSleepyApi.syncDaySleepReport(pagerIndex, Constant.PAGE_COUNT);

        call.enqueue(new BaseResponseCallback<ResultResponse<DaySleepReport>>() {
            @Override
            protected void onSuccess(ResultResponse<DaySleepReport> response) {
                view.onSyncDaySleepReportSuccess(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onSyncDaySleepReportFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }
}
