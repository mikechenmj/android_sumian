package com.sumian.hw.job;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 */

public class JobScheduler {

    private JobService mJobService;

    public JobScheduler(Context context) {
        mJobService = JobServiceImpl.getInstance(context);
    }

    public void release(Context context) {
        mJobService.release(context);
    }

    public void checkJobScheduler() {
        mJobService.runTask();
    }

    public void saveSleepData(ArrayList<String> sleepData, int type, String beginCmd, String endCmd,String monitorSn,String speedSleepSn, long receiveStartedTime, long receiveEndedTime) {
        mJobService.saveSleepData(sleepData, type, beginCmd, endCmd,monitorSn,speedSleepSn, receiveStartedTime, receiveEndedTime);
    }
}
