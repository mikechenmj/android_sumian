package com.sumian.sd.buz.upgrade.presenter;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.network.error.ErrorCode;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.device.DeviceManager;
import com.sumian.sd.buz.device.bean.BlueDevice;
import com.sumian.sd.buz.upgrade.bean.VersionInfo;
import com.sumian.sd.buz.upgrade.contract.VersionContract;
import com.sumian.sd.common.network.callback.BaseSdResponseCallback;
import com.sumian.sd.common.network.response.AppUpgradeInfo;
import com.sumian.sd.common.network.response.FirmwareInfo;
import com.sumian.sd.common.utils.NumberUtil;
import com.sumian.sd.common.utils.UiUtil;

import org.jetbrains.annotations.NotNull;

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

public class DeviceVersionNoticePresenter implements VersionContract.Presenter {

    private static final int MONITOR_VERSION_TYPE = 0x01;
    private static final int SLEEPY_VERSION_TYPE = 0x02;
    private WeakReference<VersionContract.View> mViewWeakReference;

    private DeviceVersionNoticePresenter() {
    }

    private DeviceVersionNoticePresenter(VersionContract.View view) {
        this();
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static DeviceVersionNoticePresenter init(VersionContract.View view) {
        return new DeviceVersionNoticePresenter(view);
    }

    public static DeviceVersionNoticePresenter init() {
        return new DeviceVersionNoticePresenter();
    }

    @Override
    public void syncMonitorVersionInfo() {

        VersionContract.View view = null;
        if (mViewWeakReference != null) {
            view = this.mViewWeakReference.get();
        }
        if (view != null) {
            view.onBegin();
        }

        Call<FirmwareInfo> call = AppManager.getSdHttpService().syncFirmwareInfo();

        VersionContract.View finalView = view;

        call.enqueue(new BaseSdResponseCallback<FirmwareInfo>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                if (finalView != null) {
                    finalView.onFailure(errorResponse.getMessage());
                }
            }

            @Override
            protected void onSuccess(FirmwareInfo response) {

                VersionInfo monitorVersionInfo = response.monitor;
                AppManager.getVersionModel().setMonitorVersion(monitorVersionInfo);
                //clone()  注意这里用的是深拷贝
                try {
                    if (monitorVersionInfo != null) {
                        monitorVersionInfo = monitorVersionInfo.clone();
                    }
                    checkVersionInfo(finalView, MONITOR_VERSION_TYPE, monitorVersionInfo, DeviceManager.INSTANCE.getMonitorVersion());

                    VersionInfo sleeperVersionInfo = response.sleeper;
                    AppManager.getVersionModel().setSleepyVersion(sleeperVersionInfo);

                    if (sleeperVersionInfo != null) {
                        sleeperVersionInfo = sleeperVersionInfo.clone();
                    }
                    checkVersionInfo(finalView, SLEEPY_VERSION_TYPE, sleeperVersionInfo, DeviceManager.INSTANCE.getSleeperVersion());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
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
        boolean isConnected;
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (versionType == MONITOR_VERSION_TYPE) {
            isConnected = bluePeripheral != null && bluePeripheral.isConnected();
        } else {
            isConnected = (bluePeripheral != null && bluePeripheral.isConnected()) && DeviceManager.INSTANCE.getSleeperStatus() == BlueDevice.STATUS_CONNECTED;
        }
        if (isConnected) {
            if (versionInfo != null) {//服务器有固件版本信息
                //if (VersionUtil.hasNewVersion(Arrays.asList(versionInfo.getVersion().split(".")), Arrays.asList(currentVersionInfo.split(".")))) {
                if (versionInfo.getVersionCode() > NumberUtil.formatVersionCode(currentVersionInfo)) {//有新版本

                    versionInfo.setVersion(TextUtils.isEmpty(currentVersionInfo) ? App.getAppContext().getString(R.string.connected_state_hint) : currentVersionInfo);

                    notifyVersionDot(versionType, true);
                } else {
                    versionInfo.setVersion(currentVersionInfo);
                    notifyVersionDot(versionType, false);
                }
            } else {
                versionInfo = new VersionInfo().setVersion(TextUtils.isEmpty(currentVersionInfo) ?
                        App.getAppContext().getString(R.string.connected_state_hint) : currentVersionInfo);
                notifyVersionDot(versionType, false);
            }
        } else {
            if (versionInfo == null) {
                versionInfo = new VersionInfo();
            }
            versionInfo.setVersion(App.getAppContext().getString(R.string.none_connected_state_hint));
            notifyVersionDot(versionType, false);
        }

        if (versionType == MONITOR_VERSION_TYPE) {
            if (view != null) {
                view.onSyncMonitorCallback(versionInfo);
            }
        } else {
            if (view != null) {
                view.onSyncSleepyCallback(versionInfo);
            }
        }
    }

    @Override
    public void syncAppVersionInfo() {
        VersionContract.View view = null;

        if (mViewWeakReference != null) {
            view = mViewWeakReference.get();
        }

        Map<String, String> map = new HashMap<>();

        PackageInfo packageInfo = UiUtil.getPackageInfo(App.getAppContext());

        map.put("type", "1");
        map.put("current_version", packageInfo.versionName);

        Call<AppUpgradeInfo> call = AppManager.getSdHttpService().syncUpgradeAppInfo(map);

        VersionContract.View finalView = view;

        call.enqueue(new BaseSdResponseCallback<AppUpgradeInfo>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                if (errorResponse.getCode() == ErrorCode.NOT_FOUND) {
                    AppUpgradeInfo appUpgradeInfo = new AppUpgradeInfo();
                    appUpgradeInfo.version = packageInfo.versionName;
                    if (finalView != null)
                        finalView.onSyncAppVersionCallback(appUpgradeInfo);
                    AppManager.getVersionModel().notifyAppDot(false);
                } else if (finalView != null) {
                    finalView.onFailure(errorResponse.getMessage());
                }
            }

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
            protected void onFinish() {
                if (finalView != null) {
                    finalView.onFinish();
                }
            }
        });
    }

    private void notifyVersionDot(int versionType, boolean isShowDot) {
        if (versionType == MONITOR_VERSION_TYPE) {
            AppManager.getVersionModel().notifyMonitorDot(isShowDot);
        } else {
            AppManager.getVersionModel().notifySleepyDot(isShowDot);
        }
    }
}
