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

import com.sumian.common.helper.ToastHelper;
import com.sumian.open.OpenEngine;
import com.sumian.open.login.OpenLogin;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.account.activity.LoginActivity;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.chat.player.VoicePlayer;
import com.sumian.sleepdoctor.network.api.DoctorApi;
import com.sumian.sleepdoctor.network.engine.NetEngine;
import com.sumian.sleepdoctor.tab.model.GroupViewModel;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public final class AppManager implements Observer<Boolean>, QbSdk.PreInitCallback {

    private static final String TAG = AppManager.class.getSimpleName();

    private DoctorApi mDoctorApi;
    private AccountViewModel mAccountViewModel;
    private GroupViewModel mGroupViewModel;

    private ChatEngine mChatEngine;

    private VoicePlayer mVoicePlayer;
    private LiveData<Boolean> mTokenInvalidStateLiveData;
    private OpenEngine mOpenEngine;

    private AppManager() {
    }

    public static AppManager init() {
        return Holder.INSTANCE;
    }

    public static synchronized ChatEngine getChatEngine() {
        return Holder.INSTANCE.mChatEngine;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onChanged(@Nullable Boolean tokenIsInvalid) {
        Log.e(TAG, "onChanged: -------token  is invalid------->" + tokenIsInvalid);

        if (tokenIsInvalid) {
            LoginActivity.show(App.Companion.getAppContext(), LoginActivity.class);
        }
    }

    private static class Holder {
        private static volatile AppManager INSTANCE = new AppManager();
    }

    public static synchronized AccountViewModel getAccountViewModel() {
        return Holder.INSTANCE.mAccountViewModel;
    }

    public static synchronized GroupViewModel getGroupViewModel() {
        if (Holder.INSTANCE.mGroupViewModel == null) {
            Holder.INSTANCE.mGroupViewModel = new GroupViewModel();
        }
        return Holder.INSTANCE.mGroupViewModel;
    }

    public static synchronized VoicePlayer getVoicePlayer() {
        return Holder.INSTANCE.mVoicePlayer == null ? Holder.INSTANCE.mVoicePlayer = new VoicePlayer() : Holder.INSTANCE.mVoicePlayer;
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

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //x5内核初始化接口
        QbSdk.initX5Environment(context, this);

        if (Holder.INSTANCE.mAccountViewModel == null) {
            Holder.INSTANCE.mAccountViewModel = new AccountViewModel((Application) context);
            Holder.INSTANCE.mAccountViewModel.LoadToken();
        }

        mTokenInvalidStateLiveData = Holder.INSTANCE.mAccountViewModel.getLiveDataTokenInvalidState();
        mTokenInvalidStateLiveData.observeForever(this);

        if (mChatEngine == null) {
            this.mChatEngine = new ChatEngine(context);
        }

        if (mOpenEngine == null) {
            this.mOpenEngine = new OpenEngine().register(context, BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
        }

    }

    public void release() {
        if (mTokenInvalidStateLiveData != null && (mTokenInvalidStateLiveData.hasActiveObservers() || mTokenInvalidStateLiveData.hasObservers())) {
            mTokenInvalidStateLiveData.removeObserver(this);
        }
        Holder.INSTANCE = null;
    }

    @Override
    public void onCoreInitFinished() {
        Log.e(TAG, "onCoreInitFinished: -----x5 webview' core  初始化完成----->");
    }

    @Override
    public void onViewInitFinished(boolean isX5Core) {
        //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
        Log.e(TAG, " onViewInitFinished is " + isX5Core);

    }
}
