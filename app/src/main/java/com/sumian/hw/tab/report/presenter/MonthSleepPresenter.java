package com.sumian.hw.tab.report.presenter;

import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.response.SleepDurationReport;
import com.sumian.hw.tab.report.contract.MonthSleepContract;
import com.sumian.sleepdoctor.app.AppManager;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class MonthSleepPresenter implements MonthSleepContract.Presenter {

    private static final String TAG = MonthSleepPresenter.class.getSimpleName();

    private WeakReference<MonthSleepContract.View> mViewWeakReference;

    private Calendar mCurrentCalendar;

    private int mFinalYear;
    private int mFinalMonth;

    private int mMaxDate;

    private MonthSleepPresenter(MonthSleepContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);

        Calendar calendar = Calendar.getInstance();
        this.mFinalYear = calendar.get(Calendar.YEAR);
        this.mFinalMonth = calendar.get(Calendar.MONTH);
        this.mCurrentCalendar = calendar;
    }

    public static void init(MonthSleepContract.View view) {
        new MonthSleepPresenter(view);
    }


    @Override
    public void doSyncMonthSleepReport() {
        MonthSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        Calendar calendar = this.mCurrentCalendar;

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        //this.mCurrentCalendar = calendar;

        view.onSwitchMonthCallback(year, month + 1);
        requestDispatcher(TimeUtil.formatLineToday(calendar.getTime()), year, month);
        AppManager.getJobScheduler().checkJobScheduler();
    }

    @Override
    public void doSyncPreMonthSleepReport() {

        MonthSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        Calendar calendar = this.mCurrentCalendar;

        calendar.add(Calendar.MONTH, -1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // this.mCurrentCalendar = calendar;

        view.onSwitchMonthCallback(year, month + 1);

        requestDispatcher(TimeUtil.formatLineToday(calendar.getTime()), year, month);

    }

    @Override
    public void doSyncNextMonthSleepReport() {

        MonthSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        Calendar calendar = this.mCurrentCalendar;
        calendar.add(Calendar.MONTH, +1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // this.mCurrentCalendar = calendar;

        view.onSwitchMonthCallback(year, month + 1);

        requestDispatcher(TimeUtil.formatLineToday(calendar.getTime()), year, month);

    }

    @Override
    public void release() {

    }

    private void requestDispatcher(String today, int year, int month) {

        MonthSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        this.mMaxDate = this.mCurrentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        view.returnMaxDays(mMaxDate);
        view.onSwitchWeekFinalMonth(year < mFinalYear || month < mFinalMonth);

        view.onBegin();

        Call<SleepDurationReport> daySleepReportCall = AppManager.getHwNetEngine().getHttpService().syncMonthSleepReport(today);

        daySleepReportCall.enqueue(new BaseResponseCallback<SleepDurationReport>() {
            @Override
            protected void onSuccess(SleepDurationReport response) {
                view.onSyncMonthSleepReportSuccess(response);
            }

            @Override
            protected void onFailure(int code, String error) {
                view.onSyncMonthSleepReportFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }
}
