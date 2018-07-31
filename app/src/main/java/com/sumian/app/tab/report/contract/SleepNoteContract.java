package com.sumian.app.tab.report.contract;

import com.sumian.app.base.BaseNetView;
import com.sumian.app.base.BasePresenter;
import com.sumian.app.improve.report.dailyreport.DailyReport;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:
 */

public interface SleepNoteContract {


    interface View extends BaseNetView<Presenter> {

        void onSyncSleepNoteOptionsSuccess(List<String> bedtimeState);

        void onSyncSleepNoteOptionsFailed(String error);

        void onUploadDiarySuccess(DailyReport dailyReport);

        void onUploadDiaryFailed(String error);

    }

    interface Presenter extends BasePresenter {

        void syncSleepNoteOptions();

        void uploadDiary(long id, int WakeUpMood, List<String> bedtimeState, String remark);
    }
}
