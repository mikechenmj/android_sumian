package com.sumian.hw.upgrade.model;

import com.sumian.hw.upgrade.bean.VersionInfo;
import com.sumian.sd.network.response.AppUpgradeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/11/24.
 * <p>
 * desc:版本信息 model
 */

public class VersionModel {

    private static final String TAG = VersionModel.class.getSimpleName();

    private List<ShowDotCallback> mShowDotCallbacks;

    private boolean mIsShowAppVersionDot;
    private boolean mIsShowMonitorVersionDot;
    private boolean mIsShowSleepyVersionDot;

    private volatile VersionInfo mMonitorVersion;
    private volatile VersionInfo mSleepyVersion;

    private AppUpgradeInfo mAppUpgradeInfo;

    public void syncAppVersion() {
        showVersionDot();
    }

    public boolean isShowAppVersionDot() {
        return mIsShowAppVersionDot;
    }

    public boolean isShowMonitorVersionDot() {
        return mIsShowMonitorVersionDot;
    }

    public boolean isShowSleepyVersionDot() {
        return mIsShowSleepyVersionDot;
    }

    public VersionInfo getMonitorVersion() {
        return mMonitorVersion;
    }

    public void setMonitorVersion(VersionInfo monitorVersion) {
        mMonitorVersion = monitorVersion;
    }

    public VersionInfo getSleepyVersion() {
        return mSleepyVersion;
    }

    public void setSleepyVersion(VersionInfo sleepyVersion) {
        mSleepyVersion = sleepyVersion;
    }

    public AppUpgradeInfo getAppUpgradeInfo() {
        return mAppUpgradeInfo;
    }

    public void setAppUpgradeInfo(AppUpgradeInfo appUpgradeInfo) {
        mAppUpgradeInfo = appUpgradeInfo;
    }

    public void registerShowDotCallback(ShowDotCallback showDotCallback) {
        if (this.mShowDotCallbacks == null)
            this.mShowDotCallbacks = new ArrayList<>();

        if (this.mShowDotCallbacks.contains(showDotCallback)) return;
        this.mShowDotCallbacks.add(showDotCallback);
    }

    public void unRegisterShowDotCallback(ShowDotCallback showDotCallback) {
        List<ShowDotCallback> showDotCallbacks = this.mShowDotCallbacks;
        if (showDotCallbacks == null || showDotCallbacks.isEmpty()) return;
        showDotCallbacks.remove(showDotCallback);
        this.mShowDotCallbacks = showDotCallbacks;
    }

    public void notifyAppDot(boolean isShowAppVersionDot) {
        this.mIsShowAppVersionDot = isShowAppVersionDot;
        showVersionDot();
    }

    public void notifyMonitorDot(boolean isShowMonitorVersionDot) {
        this.mIsShowMonitorVersionDot = isShowMonitorVersionDot;
        showVersionDot();
    }

    public void notifySleepyDot(boolean isShowSleepyVersionDot) {
        this.mIsShowSleepyVersionDot = isShowSleepyVersionDot;
        showVersionDot();
    }

    private void showVersionDot() {
        List<ShowDotCallback> showDotCallbacks = this.mShowDotCallbacks;
        if (showDotCallbacks == null || showDotCallbacks.isEmpty()) return;
        for (ShowDotCallback showDotCallback : showDotCallbacks) {
            showDotCallback.showDot(mIsShowAppVersionDot, mIsShowMonitorVersionDot, mIsShowSleepyVersionDot);
        }
    }

    public interface ShowDotCallback {

        void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot);
    }
}
