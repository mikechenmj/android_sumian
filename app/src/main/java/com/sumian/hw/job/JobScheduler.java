package com.sumian.hw.job;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 */

public class JobScheduler {

    private JobSchedulerDelegate mJobScheduler;

    public JobScheduler(Context context) {
        mJobScheduler = JobSchedulerDelegate.getInstance(context);
    }

    public void release() {
        mJobScheduler.release();
    }

    public void checkJobScheduler() {
        mJobScheduler.checkPendingTaskAndRun();
    }

    public void saveSleepData(ArrayList<String> sleepData, int type, String beginCmd, String endCmd, String monitorSn, String speedSleepSn, long receiveStartedTime, long receiveEndedTime) {
        mJobScheduler.saveSleepData(sleepData, type, beginCmd, endCmd, monitorSn, speedSleepSn, receiveStartedTime, receiveEndedTime);
    }
}
