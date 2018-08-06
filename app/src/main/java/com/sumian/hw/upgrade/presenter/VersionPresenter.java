package com.sumian.hw.upgrade.presenter;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.sumian.blue.model.BluePeripheral;
import com.sumian.hw.common.util.NumberUtil;
import com.sumian.hw.common.util.UiUtil;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.callback.ErrorCode;
import com.sumian.hw.network.response.AppUpgradeInfo;
import com.sumian.hw.network.response.FirmwareInfo;
import com.sumian.hw.upgrade.bean.VersionInfo;
import com.sumian.hw.upgrade.contract.VersionContract;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.response.Error;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public class VersionPresenter implements VersionContract.Presenter {

    private WeakReference<VersionContract.View> mViewWeakReference;

    private static final int MONITOR_VERSION_TYPE = 0x01;
    private static final int SLEEPY_VERSION_TYPE = 0x02;

    private VersionPresenter() {
    }

    private VersionPresenter(VersionContract.View view) {
        this();
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static VersionPresenter init(VersionContract.View view) {
        return new VersionPresenter(view);
    }

    public static VersionPresenter init() {
        return new VersionPresenter();
    }

    @Override
    public void syncMonitorVersionInfo() {

        VersionContract.View view = null;
        if (mViewWeakReference != null) {
            view = this.mViewWeakReference.get();
        }
        SleepyApi sleepyApi = AppManager.getHwNetEngine().getHttpService();
        if (sleepyApi == null) return;

        if (view != null) {
            view.onBegin();
        }

        Call<FirmwareInfo> call = sleepyApi.syncFirmwareInfo();

        VersionContract.View finalView = view;

        call.enqueue(new BaseResponseCallback<FirmwareInfo>() {
            @Override
            protected void onSuccess(FirmwareInfo response) {

                VersionInfo monitorVersionInfo = response.monitor;
                AppManager.getVersionModel().setMonitorVersion(monitorVersionInfo);
                //clone()  注意这里用的是深拷贝
                try {
                    if (monitorVersionInfo != null) {
                        monitorVersionInfo = monitorVersionInfo.clone();
                    }
                    checkVersionInfo(finalView, MONITOR_VERSION_TYPE, monitorVersionInfo, AppManager.getDeviceModel().getMonitorVersion());

                    VersionInfo sleeperVersionInfo = response.sleeper;
                    if (sleeperVersionInfo != null) {
                        sleeperVersionInfo = sleeperVersionInfo.clone();
                    }
                    AppManager.getVersionModel().setSleepyVersion(sleeperVersionInfo);
                    checkVersionInfo(finalView, SLEEPY_VERSION_TYPE, sleeperVersionInfo, AppManager.getDeviceModel().getSleepyVersion());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailure(int code, String error) {
                if (finalView != null) {
                    finalView.onFailure(error);
                }
            }

            @Override
            protected void onFinish() {
                if (finalView != null) {
                    finalView.onFinish();
                }
            }
        });

    }

    private void checkVersionInfo(VersionContract.View view, int versionType, VersionInfo versionInfo, String currentVersionInfo) {
        if (view == null) return;
        boolean isConnected;
        if (versionType == MONITOR_VERSION_TYPE) {
            BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
            isConnected = bluePeripheral != null && bluePeripheral.isConnected();
        } else {
            isConnected = AppManager.getDeviceModel().sleepyIsConnected();
        }
        if (isConnected) {
            if (versionInfo != null) {//服务器有固件版本信息
                if (versionInfo.getVersionCode() > NumberUtil.formatVersionCode(currentVersionInfo)) {//有新版本
                    versionInfo.setVersion(TextUtils.isEmpty(currentVersionInfo) ?
                            App.Companion.getAppContext().getString(R.string.connected_state_hint) : currentVersionInfo);
                    if (versionType == MONITOR_VERSION_TYPE) {
                        AppManager.getVersionModel().notifyMonitorDot(true);
                    } else {
                        AppManager.getVersionModel().notifySleepyDot(true);
                    }
                } else {
                    versionInfo.setVersion(currentVersionInfo);
                    notifyVersionDot(versionType);
                }
            } else {
                versionInfo = new VersionInfo().setVersion(TextUtils.isEmpty(currentVersionInfo) ?
                        App.Companion.getAppContext().getString(R.string.connected_state_hint) : currentVersionInfo);
                notifyVersionDot(versionType);
            }
        } else {
            if (versionInfo == null) {
                versionInfo = new VersionInfo();
            }
            versionInfo.setVersion(App.Companion.getAppContext().getString(R.string.none_connected_state_hint));
            notifyVersionDot(versionType);
        }

        if (versionType == MONITOR_VERSION_TYPE) {
            view.onSyncMonitorCallback(versionInfo);
        } else {
            view.onSyncSleepyCallback(versionInfo);
        }
    }

    private void notifyVersionDot(int versionType) {
        if (versionType == MONITOR_VERSION_TYPE) {
            AppManager.getVersionModel().notifyMonitorDot(false);
        } else {
            AppManager.getVersionModel().notifySleepyDot(false);
        }
    }

    @Override
    public void syncAppVersionInfo() {
        VersionContract.View view = null;

        if (mViewWeakReference != null) {
            view = mViewWeakReference.get();
        }

        SleepyApi sleepyApi = AppManager.getHwNetEngine().getHttpService();
        if (sleepyApi == null) return;

        Map<String, String> map = new HashMap<>();

        PackageInfo packageInfo = UiUtil.getPackageInfo(App.Companion.getAppContext());

        map.put("type", "1");
        map.put("current_version", packageInfo.versionName);

        Call<AppUpgradeInfo> call = sleepyApi.syncUpgradeAppInfo(map);

        VersionContract.View finalView = view;

        call.enqueue(new BaseResponseCallback<AppUpgradeInfo>() {
            @Override
            protected void onSuccess(AppUpgradeInfo response) {

                PackageInfo packageInfo = UiUtil.getPackageInfo(App.Companion.getAppContext());

                AppUpgradeInfo appUpgradeInfo = response;
                if (appUpgradeInfo == null) {//相同版本或没有新版本
                    appUpgradeInfo = new AppUpgradeInfo();
                    appUpgradeInfo.version = packageInfo.versionName;
                    AppManager.getVersionModel().notifyAppDot(false);

                } else {
                    AppManager.getVersionModel().notifyAppDot(NumberUtil.formatVersionCode(response.version) > packageInfo.versionCode);
                }

                try {
                    AppUpgradeInfo copyAppUpgradeInfo = appUpgradeInfo.clone();
                    copyAppUpgradeInfo.version = packageInfo.versionName;
                    if (finalView != null) {
                        finalView.onSyncAppVersionCallback(copyAppUpgradeInfo);
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                AppManager.getVersionModel().setAppUpgradeInfo(appUpgradeInfo);
            }

            @Override
            protected void onFailure(int code, String error) {
                if (code == ErrorCode.NOT_FOUND) {
                    AppUpgradeInfo appUpgradeInfo = new AppUpgradeInfo();
                    appUpgradeInfo.version = packageInfo.versionName;
                    if (finalView != null)
                        finalView.onSyncAppVersionCallback(appUpgradeInfo);
                    AppManager.getVersionModel().notifyAppDot(false);
                } else if (finalView != null) {
                    finalView.onFailure(error);
                }
            }

            @Override
            protected void onFinish() {
                if (finalView != null) {
                    finalView.onFinish();
                }
            }
        });
    }

    @Override
    public void release() {

    }
}
