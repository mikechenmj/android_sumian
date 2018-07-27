package com.sumian.app.improve.report.viewModel;

import com.sumian.app.improve.report.dailyreport.DailyReport;

import java.util.Calendar;

/**
 * Created by sm
 * on 2018/5/17 13:53
 * desc:  睡眠特征同步状态
 **/
public class ReportModel {

    private volatile OnSyncCallback mOnSyncCallback;
    private volatile int mIsSyncSleepReport;//0x03  睡特征正在同步中  0xff  睡眠特征上传失败,即该睡眠特征数据,有可能采集时间重合
    private volatile boolean mIsUploadFailed;
    private volatile DailyReport mCacheDailyReport;

    public ReportModel setOnSyncCallback(OnSyncCallback onSyncCallback) {
        mOnSyncCallback = onSyncCallback;
        return this;
    }

    public void notifySyncStatus(int isSyncSleepReport) {
        if (mIsSyncSleepReport == 0x03) {
            switch (isSyncSleepReport) {
                case 0x03:
                    if (mOnSyncCallback != null)
                        mOnSyncCallback.onSyncingCallback();
                    break;
                case 0xff://同步事件优先级大于 error 优先级.因为可能在上传多段数据
                    mIsUploadFailed = true;
                    break;
                default:
                    mIsSyncSleepReport = isSyncSleepReport;
                    notifyErrorOrFinished();
                    break;
            }
        } else {
            mIsSyncSleepReport = isSyncSleepReport;
            if (isSyncSleepReport == 0xff) {
                mIsUploadFailed = true;
            }
            checkSyncStatus();
        }
    }

    public void checkSyncStatus() {
        switch (mIsSyncSleepReport) {
            case 0x03:
                if (mOnSyncCallback != null)
                    mOnSyncCallback.onSyncingCallback();
                break;
            case 0xff:
                if (mIsUploadFailed) {
                    if (mOnSyncCallback != null)
                        mOnSyncCallback.onSyncingErrorCallback();
                    mIsUploadFailed = false;
                }
                break;
            default:
                notifyErrorOrFinished();
                break;
        }
    }

    private void notifyErrorOrFinished() {
        if (mIsUploadFailed) {
            if (mOnSyncCallback != null)
                mOnSyncCallback.onSyncingErrorCallback();
            mIsUploadFailed = false;
        } else {
            if (mOnSyncCallback != null)
                mOnSyncCallback.onSyncFinishedCallback();
        }
    }

    public DailyReport getCacheDailyReport() {
        return mCacheDailyReport;
    }

    public void setCacheDailyReport(DailyReport cacheDailyReport) {
        mCacheDailyReport = cacheDailyReport;
    }

    public boolean isHaveTodayCache() {
        if (mCacheDailyReport == null) return false;
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.set(tmpCalendar.get(Calendar.YEAR), tmpCalendar.get(Calendar.MONTH), tmpCalendar.get(Calendar.DATE), 0, 0, 0);
        return mCacheDailyReport.date == tmpCalendar.getTimeInMillis() / 1000L;
    }

    public interface OnSyncCallback {

        void onSyncingCallback();

        void onSyncingErrorCallback();

        void onSyncFinishedCallback();
    }
}
