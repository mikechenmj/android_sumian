package com.sumian.hw.upgrade.service;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.hw.upgrade.activity.VersionUpgradeActivity;

/**
 * Created by jzz
 * on 2017/9/21
 * <p>
 * desc: 固件升级 service
 */

public class DfuService extends DfuBaseService {


    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return VersionUpgradeActivity.class;
    }

    @Override
    protected boolean isDebug() {
        // Here return true if you want the service to print more logs in LogCat.
        // Library's BuildConfig in current version of Android Studio is always set to DEBUG=false, so
        // make sure you return true or your.app.BuildConfig.DEBUG here.
        return BuildConfig.DEBUG;
    }
}
