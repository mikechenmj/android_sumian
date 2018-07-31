package com.sumian.app.app.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;

import com.sumian.app.account.activity.LoginActivity;
import com.sumian.app.account.activity.SleepReminderActivity;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.improve.main.HomeActivity;
import com.sumian.app.improve.main.WelcomeActivity;
import com.sumian.app.tab.report.activity.DaySleepDetailReportActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class ApplicationDelegate implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ApplicationDelegate.class.getSimpleName();

    private static boolean mIsLoginActivity;
    private static boolean mIsHomeActivity;

    private static List<Activity> mActivities;

    private ApplicationDelegate() {
    }

    public static ApplicationDelegate init() {
        return new ApplicationDelegate();
    }

    public static boolean isIsLoginActivity() {
        return mIsLoginActivity;
    }

    public static void setIsLoginActivity(boolean isLoginActivity) {
        mIsLoginActivity = isLoginActivity;
    }

    public static void goHome(Context context) {

        if (!mIsHomeActivity) {
            HomeActivity.show(context);
        }

        List<Activity> activities = ApplicationDelegate.mActivities;

        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity instanceof HomeActivity) {
                continue;
            }
            activity.finish();
            activities.remove(activity);
        }

        mIsLoginActivity = false;
    }

    public static void exitApp() {
        List<Activity> activities = mActivities;
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity.isDestroyed() || activity.isFinishing()) {
                continue;
            }
            activity.finish();
        }
        activities.clear();
        Process.killProcess(Process.myPid());
        mActivities = null;
        mIsLoginActivity = false;
        mIsHomeActivity = false;
    }

    public ApplicationDelegate registerActivityLifecycleCallback(Application application) {
        application.registerActivityLifecycleCallbacks(this);

        return this;
    }

    public ApplicationDelegate unRegisterActivityLifecycleCallback(Application application) {
        application.unregisterActivityLifecycleCallbacks(this);
        return this;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //Log.e(TAG, "onActivityCreated: ------>" + activity.toString());
        if (mActivities == null) {
            mActivities = new ArrayList<>();
        }

        if (activity instanceof WelcomeActivity) {
            return;
        }

        if (activity instanceof LoginActivity) {
            mIsLoginActivity = true;
            //Log.e(TAG, "------------loginWithService  is top----->");
        }

        if (activity instanceof HomeActivity) {
            mIsHomeActivity = true;
        }

        mActivities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof HomeActivity ||
            activity instanceof DaySleepDetailReportActivity ||
            activity instanceof SleepReminderActivity) {

            HwAppManager.getOpenAnalytics().onResume(activity);

            if (activity instanceof DaySleepDetailReportActivity) {
                HwAppManager.getOpenAnalytics().onPageStart("DaySleepDetailReport");
            }

            if (activity instanceof SleepReminderActivity) {
                HwAppManager.getOpenAnalytics().onPageStart("SleepReminder");
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof HomeActivity ||
            activity instanceof DaySleepDetailReportActivity ||
            activity instanceof SleepReminderActivity) {

            HwAppManager.getOpenAnalytics().onPause(activity);

            if (activity instanceof DaySleepDetailReportActivity) {
                HwAppManager.getOpenAnalytics().onPageEnd("DaySleepDetailReport");
            }

            if (activity instanceof SleepReminderActivity) {
                HwAppManager.getOpenAnalytics().onPageEnd("SleepReminder");
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        List<Activity> activities = mActivities;
        if (activities == null || activities.isEmpty()) return;
        activities.remove(activity);
        if (activity instanceof LoginActivity) {
            mIsLoginActivity = false;
        }

        if (activity instanceof HomeActivity) {
            mIsHomeActivity = false;
        }
    }

}
