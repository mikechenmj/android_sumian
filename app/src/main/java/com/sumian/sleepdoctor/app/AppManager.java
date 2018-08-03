package com.sumian.sleepdoctor.app;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.view.Gravity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.social.OpenEngine;
import com.sumian.common.social.analytics.OpenAnalytics;
import com.sumian.common.social.login.OpenLogin;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.app.HwApp;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.sleepdoctor.advisory.model.AdvisoryViewModel;
import com.sumian.sleepdoctor.doctor.model.DoctorViewModel;
import com.sumian.sleepdoctor.leancloud.LeanCloudManager;
import com.sumian.sleepdoctor.network.api.DoctorApi;
import com.sumian.sleepdoctor.network.engine.NetEngine;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public final class AppManager {

    private DoctorApi mDoctorApi;
    private AccountViewModel mAccountViewModel;
    private AdvisoryViewModel mAdvisoryViewModel;
    private DoctorViewModel mDoctorViewModel;
    private OpenEngine mOpenEngine;

    private AppManager() {
    }

    public static AppManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static volatile AppManager INSTANCE = new AppManager();
    }

    public static synchronized AccountViewModel getAccountViewModel() {
        return Holder.INSTANCE.mAccountViewModel;
    }

    public static synchronized AdvisoryViewModel getAdvisoryViewModel() {
        return Holder.INSTANCE.mAdvisoryViewModel == null ? Holder.INSTANCE.mAdvisoryViewModel = new AdvisoryViewModel() : Holder.INSTANCE.mAdvisoryViewModel;
    }

    public static synchronized DoctorViewModel getDoctorViewModel() {
        return Holder.INSTANCE.mDoctorViewModel == null ? Holder.INSTANCE.mDoctorViewModel = new DoctorViewModel() : Holder.INSTANCE.mDoctorViewModel;
    }

    public static synchronized DoctorApi getHttpService() {
        return Holder.INSTANCE.mDoctorApi == null ? Holder.INSTANCE.mDoctorApi = new NetEngine().httpRequest() : Holder.INSTANCE.mDoctorApi;
    }

    public static synchronized OpenLogin getOpenLogin() {
        return Holder.INSTANCE.mOpenEngine.getOpenLogin();
    }

    public static synchronized OpenAnalytics getOpenAnalytics() {
        return Holder.INSTANCE.mOpenEngine.getOpenAnalytics();
    }

    public void init(@NonNull Context context) {
        initUtils(context);
        initEmojiCompat(context);
        initAccountViewModel((Application) context);
        initLeanCloud(context);
        initOpenEngine(context);
    }

    private void initLeanCloud(Context context) {
        LeanCloudManager.init(context);
    }

    private void initEmojiCompat(Context context) {
        EmojiCompat.init(new BundledEmojiCompatConfig(context));
    }

    private void initOpenEngine(Context context) {
        if (mOpenEngine == null) {
            OpenEngine.init(context, BuildConfig.DEBUG, BuildConfig.UMENG_APP_KEY, BuildConfig.UMENG_CHANNEL, BuildConfig.UMENG_PUSH_SECRET);
            mOpenEngine = new OpenEngine().create(context, BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
        }
    }

    private void initAccountViewModel(Application context) {
        if (mAccountViewModel == null) {
            mAccountViewModel = new AccountViewModel(context);
            mAccountViewModel.loadTokenFromSp();
        }
        mAccountViewModel.getLiveDataTokenInvalidState().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                ActivityUtils.startActivity(HwLoginActivity.class);
            }
        });
    }

    private void initUtils(Context context) {
        ToastHelper.init(context);
        Utils.init(context);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
    }
}
