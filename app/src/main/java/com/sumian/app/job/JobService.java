package com.sumian.app.job;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 */

public interface JobService {

    void addAndRunTask(JobTask jobTask);

    void commitTaskCache();

    JobTask getNextTask();

    void release(Context context);

    void runTask();

    void saveSleepData(ArrayList<String> sleepData, int type, String beginCmd, String endCmd, String monitorSn,String speedSleepSn,long receiveStartedTime, long receiveEndedTime);
}
