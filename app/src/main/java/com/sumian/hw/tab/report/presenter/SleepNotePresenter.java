package com.sumian.hw.tab.report.presenter;

import com.alibaba.fastjson.JSON;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.improve.report.dailyreport.DailyReport;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.tab.report.contract.SleepNoteContract;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:
 */

public class SleepNotePresenter implements SleepNoteContract.Presenter {

    //private static final String TAG = SleepNotePresenter.class.getSimpleName();

    private WeakReference<SleepNoteContract.View> mViewWeakReference;

    private SleepNotePresenter(SleepNoteContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static void init(SleepNoteContract.View view) {
        new SleepNotePresenter(view);
    }

    @Override
    public void release() {

    }

    @Override
    public void syncSleepNoteOptions() {
        WeakReference<SleepNoteContract.View> viewWeakReference = this.mViewWeakReference;
        SleepNoteContract.View view = viewWeakReference.get();
        if (view == null) return;
        Call<String> call = HwAppManager.getNetEngine().getHttpService().syncSleepNoteOptions();

        view.onBegin();
        call.enqueue(new BaseResponseCallback<String>() {
            @Override
            protected void onSuccess(String response) {

                List<String> bedtimeStates = JSON.parseObject(response).getJSONArray("bedtime_state").toJavaList(String.class);

                view.onSyncSleepNoteOptionsSuccess(bedtimeStates);
            }

            @Override
            protected void onFailure(String error) {
                view.onSyncSleepNoteOptionsFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

    @Override
    public void uploadDiary(long id, int WakeUpMood, List<String> bedTimeState, String remark) {
        WeakReference<SleepNoteContract.View> viewWeakReference = this.mViewWeakReference;
        SleepNoteContract.View view = viewWeakReference.get();
        if (view == null) return;
        Map<String, Object> map = new HashMap<>();
        map.put("wake_up_mood", WakeUpMood);
        map.put("bedtime_state", JSON.toJSONString(bedTimeState));
        map.put("remark", remark);

        Call<DailyReport> call = HwAppManager.getV1HttpService().writeDiary((int) id, map);
        mCalls.add(call);
        view.onBegin();
        call.enqueue(new BaseResponseCallback<DailyReport>() {
            @Override
            protected void onSuccess(DailyReport response) {
                view.onUploadDiarySuccess(response);
            }

            @Override
            protected void onFailure(String error) {
                view.onUploadDiaryFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

}
