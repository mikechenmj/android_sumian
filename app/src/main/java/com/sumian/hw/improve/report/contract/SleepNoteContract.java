package com.sumian.hw.improve.report.contract;

import com.sumian.hw.base.HwBaseNetView;
import com.sumian.hw.base.HwBasePresenter;
import com.sumian.hw.improve.report.dailyreport.DailyReport;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:
 */

public interface SleepNoteContract {


    interface View extends HwBaseNetView<Presenter> {

        void onSyncSleepNoteOptionsSuccess(List<String> bedtimeState);

        void onSyncSleepNoteOptionsFailed(String error);

        void onUploadDiarySuccess(DailyReport dailyReport);

        void onUploadDiaryFailed(String error);

    }

    interface Presenter extends HwBasePresenter {

        void syncSleepNoteOptions();

        void uploadDiary(long id, int WakeUpMood, List<String> bedtimeState, String remark);
    }
}
