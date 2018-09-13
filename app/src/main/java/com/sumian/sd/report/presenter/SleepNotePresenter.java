package com.sumian.sd.report.presenter;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.report.contract.SleepNoteContract;
import com.sumian.sd.report.dailyreport.DailyReport;

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
        Call<JsonObject> call = AppManager.getHwNetEngine().getHttpService().syncSleepNoteOptions();

        view.onBegin();
        call.enqueue(new BaseResponseCallback<JsonObject>() {
            @Override
            protected void onSuccess(JsonObject response) {

                List<String> bedtimeStates = JSON.parseObject(response.toString()).getJSONArray("bedtime_state").toJavaList(String.class);

                view.onSyncSleepNoteOptionsSuccess(bedtimeStates);
            }

            @Override
            protected void onFailure(int code, String error) {
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

        Call<DailyReport> call = AppManager.getHwV1HttpService().writeDiary((int) id, map);
        mCalls.add(call);
        view.onBegin();
        call.enqueue(new BaseResponseCallback<DailyReport>() {
            @Override
            protected void onSuccess(DailyReport response) {
                view.onUploadDiarySuccess(response);
            }

            @Override
            protected void onFailure(int code, String error) {
                view.onUploadDiaryFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });
    }

}
