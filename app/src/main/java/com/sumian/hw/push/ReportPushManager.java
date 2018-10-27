package com.sumian.hw.push;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.sumian.hw.log.LogManager;
import com.sumian.hw.utils.NotificationUtil;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/4/27 15:00
 *     desc   : 睡眠报告推送管理类
 *     version: 1.0
 * </pre>
 */
public class ReportPushManager {
    public static final String ROUTER_PATH_DAY_REPORT = "/day_report";
    public static final String ROUTER_PATH_WEEK_REPORT = "/week_report";
    public static final String ROUTER_PATH_MONTH_REPORT = "/month_report";

    private PushReport mPushReport;

    private static class InstanceHolder {
        private static final ReportPushManager INSTANCE = new ReportPushManager();
    }

    public static ReportPushManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * @param uriStr push数据中的scheme字段，如"http://www.sumian.com/day_report?date=1524585600"
     */
    public void setPushReportByUriStr(String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            return;
        }
        Uri uri = Uri.parse(uriStr);
        String path = uri.getPath();
        String dateString = uri.getQueryParameter("date");
        setPushReport(path, Integer.parseInt(dateString));
    }

    private void setPushReport(String routerPath, int pushDate) {
        PushReport pushReport = new PushReport();
        pushReport.setPushDate(pushDate);
        switch (routerPath) {
            case ROUTER_PATH_DAY_REPORT:
                pushReport.setPushType(PushReport.PUSH_TYPE_DAILY_REPORT);
                break;
            case ROUTER_PATH_WEEK_REPORT:
                pushReport.setPushType(PushReport.PUSH_TYPE_WEEKLY_REPORT);
                break;
            default:
                LogManager.appendPhoneLog("Wrong push report path");
                pushReport = null;
                break;
        }
        setPushReport(pushReport);
    }

    private PushReport getPushReportByType(@PushReport.PushType int pushType) {
        if (mPushReport != null && mPushReport.getPushType() == pushType) {
            return mPushReport;
        } else {
            return null;
        }
    }

    public PushReport getPushReport() {
        return mPushReport;
    }

    private void setPushReport(PushReport pushReport) {
        mPushReport = pushReport;
    }

    private void clearPushReport(){
        setPushReport(null);
    }

    /**
     * 检查是否有推送消息，如果有，则回调callback，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    private boolean checkPushReportAndRun(Context context, @PushReport.PushType int pushType, OnPushReportCallback callback) {
        PushReport dailyPushReport = getPushReportByType(pushType);
        if (dailyPushReport != null) {
            NotificationUtil.Companion.cancelAllNotification(context);
            callback.onPushReport(dailyPushReport);
            clearPushReport();
            return true;
        }
        return false;
    }

    /**
     * 检查是否有推送消息，如果有，则回调callback，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    public boolean checkDailyPushReportAndRun(Context context, OnPushReportCallback callback) {
        return checkPushReportAndRun(context, PushReport.PUSH_TYPE_DAILY_REPORT, callback);
    }

    /**
     * 检查是否有推送消息，如果有，则回调callback，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    public boolean checkWeeklyPushReportAndRun(Context context, OnPushReportCallback callback) {
        return checkPushReportAndRun(context, PushReport.PUSH_TYPE_WEEKLY_REPORT, callback);
    }

    public interface OnPushReportCallback {
        void onPushReport(PushReport pushReport);
    }
}
