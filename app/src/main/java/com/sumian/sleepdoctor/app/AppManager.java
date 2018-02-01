package com.sumian.sleepdoctor.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.chat.player.VoicePlayer;
import com.sumian.sleepdoctor.network.api.DoctorApi;
import com.sumian.sleepdoctor.network.engine.NetEngine;
import com.sumian.sleepdoctor.tab.model.GroupViewModel;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public final class AppManager {

    private static final String TAG = AppManager.class.getSimpleName();

    private DoctorApi mDoctorApi;
    private AccountViewModel mAccountViewModel;
    private GroupViewModel mGroupViewModel;

    private ChatEngine mChatEngine;

    private VoicePlayer mVoicePlayer;

    private AppManager() {
    }

    public static AppManager init() {
        return Holder.INSTANCE;
    }

    public static synchronized ChatEngine getChatEngine() {
        return Holder.INSTANCE.mChatEngine;
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

    public void with(@NonNull Context context) {
        init(context);
    }

    private void init(Context context) {//初始化第三方平台
        ToastHelper.init(context);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(context);
        EmojiCompat.init(config);
        if (Holder.INSTANCE.mAccountViewModel == null) {
            Holder.INSTANCE.mAccountViewModel = new AccountViewModel((Application) context);
            Holder.INSTANCE.mAccountViewModel.LoadToken();
        }

        if (mChatEngine == null) {
            this.mChatEngine = new ChatEngine(context);
        }
    }

}
