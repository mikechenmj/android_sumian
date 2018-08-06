package com.sumian.hw.tab.report.presenter;

import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.response.SleepDurationReport;
import com.sumian.hw.tab.report.contract.WeekSleepContract;
import com.sumian.sleepdoctor.app.AppManager;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class WeekSleepPresenter implements WeekSleepContract.Presenter {

    private WeakReference<WeekSleepContract.View> mViewWeakReference;
    private SleepyApi mSleepyApi;

    private Calendar mCurrentCalendar;
    private boolean mIsPre;
    private boolean mIsNext;

    private int mFromYear;
    private int mFromMonth;
    private int mFromDate;

    private int mToYear;
    private int mToMonth;
    private int mToDate;

    private int mFinalYear;
    private int mFinalMonth;
    private int mFinalDate;

    private WeekSleepPresenter(WeekSleepContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mSleepyApi = AppManager
                .getHwNetEngine()
                .getHttpService();

        Calendar calendar = Calendar.getInstance();

        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK);
        switch (weekIndex) {
            case 1://周日
                weekIndex = 6;
                break;
            case 2://周一
                weekIndex = 5;
                break;
            case 3://周二
                weekIndex = 4;
                break;
            case 4://周三
                weekIndex = 3;
                break;
            case 5://周四
                weekIndex = 2;
                break;
            case 6://周五
                weekIndex = 1;
                break;
            case 7://周六
                weekIndex = 0;
                break;
        }

        calendar.add(Calendar.DAY_OF_MONTH, weekIndex);

        this.mToYear = calendar.get(Calendar.YEAR);
        this.mToMonth = calendar.get(Calendar.MONTH);
        this.mToDate = calendar.get(Calendar.DATE);

        this.mFinalYear = mToYear;
        this.mFinalMonth = mToMonth;
        this.mFinalDate = mToDate;

        this.mCurrentCalendar = calendar;

        calendar.add(Calendar.DAY_OF_MONTH, -6);

        this.mFromYear = calendar.get(Calendar.YEAR);
        this.mFromMonth = calendar.get(Calendar.MONTH);
        this.mFromDate = calendar.get(Calendar.DATE);

        view.onSwitchWeekFinalDate(true);

    }

    public static void init(WeekSleepContract.View view) {
        new WeekSleepPresenter(view);
    }


    @Override
    public void doSyncWeekSleepReport() {
        WeekSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;
        view.onSwitchWeekCallback(mFromYear, mFromMonth + 1, mFromDate, mToYear, mToMonth + 1, mToDate);
        requestDispatcher(TimeUtil.formatLineToday(mCurrentCalendar.getTime()));
        AppManager.getJobScheduler().checkJobScheduler();
    }

    @Override
    public void doSyncPreWeekSleepReport() {

        WeekSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        Calendar calendar = this.mCurrentCalendar;

        if (!mIsPre) {
            this.mIsPre = true;
        }

        if (mIsNext) {
            this.mIsNext = false;
            calendar.add(Calendar.DAY_OF_MONTH, -6);
        }

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        int toYear = calendar.get(Calendar.YEAR);
        int toMonth = calendar.get(Calendar.MONTH);
        int toDate = calendar.get(Calendar.DATE);

        calendar.add(Calendar.DAY_OF_MONTH, -6);

        int fromYear = calendar.get(Calendar.YEAR);
        int fromMonth = calendar.get(Calendar.MONTH);
        int fromDate = calendar.get(Calendar.DATE);

        this.mCurrentCalendar = calendar;
        this.mFromYear = fromYear;
        this.mFromMonth = fromMonth;
        this.mFromDate = fromDate;

        this.mToYear = toYear;
        this.mToMonth = toMonth;
        this.mToDate = toDate;

        view.onSwitchWeekCallback(fromYear, fromMonth + 1, fromDate, toYear, toMonth + 1, toDate);

        requestDispatcher(TimeUtil.formatLineToday(calendar.getTime()));

    }

    @Override
    public void doSyncNextWeekSleepReport() {

        WeekSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        Calendar calendar = this.mCurrentCalendar;

        if (!mIsNext) {
            mIsNext = true;
        }

        if (mIsPre) {
            mIsPre = false;
            calendar.add(Calendar.DAY_OF_MONTH, +6);
        }

        calendar.add(Calendar.DAY_OF_MONTH, +1);

        int fromYear = calendar.get(Calendar.YEAR);
        int fromMonth = calendar.get(Calendar.MONTH);
        int fromDate = calendar.get(Calendar.DATE);

        calendar.add(Calendar.DAY_OF_MONTH, +6);

        int toYear = calendar.get(Calendar.YEAR);
        int toMonth = calendar.get(Calendar.MONTH);
        int toDate = calendar.get(Calendar.DATE);

        this.mCurrentCalendar = calendar;

        this.mFromYear = fromYear;
        this.mFromMonth = fromMonth;
        this.mFromDate = fromDate;

        this.mToYear = toYear;
        this.mToMonth = toMonth;
        this.mToDate = toDate;

        view.onSwitchWeekCallback(fromYear, fromMonth + 1, fromDate, toYear, toMonth + 1, toDate);

        requestDispatcher(TimeUtil.formatLineToday(calendar.getTime()));

    }

    @Override
    public void release() {

    }

    private void requestDispatcher(String today) {

        WeekSleepContract.View view = this.mViewWeakReference.get();
        if (view == null) return;

        view.onSwitchWeekFinalDate(mToYear < mFinalYear || mToMonth < mFinalMonth || mToDate < mFinalDate);
        view.onBegin();

        Call<SleepDurationReport> daySleepReportCall = this.mSleepyApi.syncWeekSleepReport(today);

        daySleepReportCall.enqueue(new BaseResponseCallback<SleepDurationReport>() {
            @Override
            protected void onSuccess(SleepDurationReport response) {
                view.onSyncWeekSleepReportSuccess(response);
            }

            @Override
            protected void onFailure(int code, String error) {
                view.onSyncWeekSleepReportFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }
}
