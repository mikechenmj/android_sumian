package com.sumian.hw.account.presenter;

import com.sumian.common.operator.AppOperator;
import com.sumian.hw.account.contract.SleepReminderContract;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.response.Reminder;
import com.sumian.hw.network.response.ResultResponse;
import com.sumian.hw.reminder.ReminderManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/11/2.
 * <p>
 * desc:
 */

public class SleepReminderPresenter implements SleepReminderContract.Presenter {

    private static final String TAG = SleepReminderPresenter.class.getSimpleName();

    private WeakReference<SleepReminderContract.View> mViewWeakReference;
    private WeakReference<SleepyApi> mApiWeakReference;
    private List<Call> mCalls;
    private Reminder mCurrentReminder;

    private SleepReminderPresenter(SleepReminderContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        this.mApiWeakReference = new WeakReference<>(HwAppManager
                .getNetEngine()
                .getHttpService());
        this.mCalls = new ArrayList<>();
    }

    public static void init(SleepReminderContract.View view) {
        new SleepReminderPresenter(view);
    }


    @Override
    public void release() {
        List<Call> calls = this.mCalls;
        if (calls == null || calls.isEmpty()) return;
        for (Call call : calls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        this.mCalls = null;
    }

    @Override
    public void syncReminder() {

        WeakReference<SleepReminderContract.View> viewWeakReference = this.mViewWeakReference;
        SleepReminderContract.View view = viewWeakReference.get();
        if (view == null) return;

        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) return;

        view.onBegin();

        Map<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("per_page", 15);
        map.put("type", 1);

        Call<ResultResponse<Reminder>> call = sleepyApi.getReminder(map);
        this.mCalls.add(call);
        call.enqueue(new BaseResponseCallback<ResultResponse<Reminder>>() {
            @Override
            protected void onSuccess(ResultResponse<Reminder> response) {
                List<Reminder> data = response.getData();
                view.onSyncReminderSuccess(data);
                ReminderManager.updateReminder(data == null || data.isEmpty() ? null : (mCurrentReminder = data.get(0)));
            }

            @Override
            protected void onFailure(String error) {
                view.onAddReminderFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

    @Override
    public void modifyReminder(long unixTime, int isEnable) {

        WeakReference<SleepReminderContract.View> viewWeakReference = this.mViewWeakReference;
        SleepReminderContract.View view = viewWeakReference.get();
        if (view == null) return;

        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) return;

        view.onBegin();

        Reminder currentReminder = this.mCurrentReminder;
        if (currentReminder == null) {
            addReminder(unixTime, isEnable);
        } else {
            modifyCurrentReminder(unixTime, isEnable, view, sleepyApi, currentReminder);
        }
    }

    @Override
    public void addReminder(long unixTime, int isEnable) {

        WeakReference<SleepReminderContract.View> viewWeakReference = this.mViewWeakReference;
        SleepReminderContract.View view = viewWeakReference.get();
        if (view == null) return;

        WeakReference<SleepyApi> apiWeakReference = this.mApiWeakReference;
        SleepyApi sleepyApi = apiWeakReference.get();
        if (sleepyApi == null) return;

        view.onBegin();

        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("remind_at", unixTime);
        map.put("enable", isEnable);

        Call<Reminder> call = sleepyApi.addReminder(map);
        call.enqueue(new BaseResponseCallback<Reminder>() {
            @Override
            protected void onSuccess(Reminder response) {
                mCurrentReminder = response;
                view.onAddReminderSuccess();
                AppOperator.runOnThread(() -> ReminderManager.updateReminder(response));
            }

            @Override
            protected void onFailure(String error) {
                view.onAddReminderFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
        this.mCalls.add(call);

    }

    @Override
    public long formatUnixTime(String hour, String min) {
        int tempHour = Integer.parseInt(hour);
        int tempMin = Integer.parseInt(min);
        Calendar calendar = Calendar.getInstance();
        //int year = calendar.get(Calendar.YEAR);
        //int month = calendar.get(Calendar.MONTH);
        //int date = calendar.get(Calendar.DATE);
        //int second = calendar.get(Calendar.SECOND);
        // calendar.set(year, month, date, tempHour, tempMin, second);
        calendar.set(Calendar.HOUR_OF_DAY, tempHour);
        calendar.set(Calendar.MINUTE, tempMin);
        return calendar.getTimeInMillis() / 1000L;
    }

    private void modifyCurrentReminder(long unixTime, int isEnable, SleepReminderContract.View view, SleepyApi sleepyApi, Reminder currentReminder) {
        Map<String, Object> map = new HashMap<>();
        map.put("remind_at", unixTime);
        map.put("enable", isEnable);
        Call<Reminder> call = sleepyApi.modifyReminder(currentReminder.getId(), map);
        call.enqueue(new BaseResponseCallback<Reminder>() {
            @Override
            protected void onSuccess(Reminder response) {
                mCurrentReminder = response;
                view.onModifyReminderSuccess(response);
                ReminderManager.updateReminder(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onModifyReminderFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
        mCalls.add(call);
    }
}
