package com.sumian.sleepdoctor.app;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.util.Log;
import android.view.Gravity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.social.OpenEngine;
import com.sumian.common.social.login.OpenLogin;
import com.sumian.hw.account.activity.HwLoginActivity;
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

public final class AppManager implements Observer<Boolean> {

    private static final String TAG = AppManager.class.getSimpleName();

    private DoctorApi mDoctorApi;
    private AccountViewModel mAccountViewModel;
    private AdvisoryViewModel mAdvisoryViewModel;
    private DoctorViewModel mDoctorViewModel;

    private LiveData<Boolean> mTokenInvalidStateLiveData;
    private OpenEngine mOpenEngine;

    private AppManager() {
    }

    public static AppManager init() {
        return Holder.INSTANCE;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onChanged(@Nullable Boolean tokenIsInvalid) {
        Log.e(TAG, "onChanged: -------token  is invalid------->" + tokenIsInvalid);

        if (tokenIsInvalid) {
//            LoginActivity.show(HwApp.Companion.getAppContext(), LoginActivity.class);
            ActivityUtils.startActivity(HwLoginActivity.class);
        }
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

    public void with(@NonNull Context context) {
        init(context);
    }

    private void init(Context context) {//初始化第三方平台
        ToastHelper.init(context);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(context);
        EmojiCompat.init(config);

        initUtils(context);

        if (Holder.INSTANCE.mAccountViewModel == null) {
            Holder.INSTANCE.mAccountViewModel = new AccountViewModel((Application) context);
            Holder.INSTANCE.mAccountViewModel.loadTokenFromSp();
        }

        mTokenInvalidStateLiveData = Holder.INSTANCE.mAccountViewModel.getLiveDataTokenInvalidState();
        mTokenInvalidStateLiveData.observeForever(this);

        LeanCloudManager.registerPushService(context);

        if (mOpenEngine == null) {
            this.mOpenEngine = new OpenEngine().create(context, BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
        }

    }

    private void initUtils(Context context) {
        Utils.init(context);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
    }

    public void release() {
        if (mTokenInvalidStateLiveData != null && (mTokenInvalidStateLiveData.hasActiveObservers() || mTokenInvalidStateLiveData.hasObservers())) {
            mTokenInvalidStateLiveData.removeObserver(this);
        }
        Holder.INSTANCE = null;
    }
}
