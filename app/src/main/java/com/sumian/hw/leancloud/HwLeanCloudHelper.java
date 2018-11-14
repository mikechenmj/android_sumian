package com.sumian.hw.leancloud;

import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.VisitorInfo;
import com.sumian.common.image.ImageLoader;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/10/16.
 * desc:
 */

public final class HwLeanCloudHelper {

    private static volatile HwLeanCloudHelper INSTANCE;
    private List<OnShowMsgDotCallback> mOnShowMsgDotCallbacks = new ArrayList<>(0);
    private List<AVIMMessage> mCustomerMessages = new ArrayList<>(0);//线上客服聊天消息
    private boolean mHaveCuostomrMsg;

    private HwLeanCloudHelper() {
    }

    public static HwLeanCloudHelper init() {
        if (INSTANCE == null) {
            synchronized (HwLeanCloudHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HwLeanCloudHelper();
                }
            }
        }
        return INSTANCE;
    }

    public static void addOnAdminMsgCallback(OnShowMsgDotCallback onShowMsgDotCallback) {
        if (init().mOnShowMsgDotCallbacks.contains(onShowMsgDotCallback)) {
            return;
        }
        INSTANCE.mOnShowMsgDotCallbacks.add(onShowMsgDotCallback);
    }

    public static void removeOnAdminMsgCallback(OnShowMsgDotCallback onShowMsgDotCallback) {
        if (init().mOnShowMsgDotCallbacks == null || init().mOnShowMsgDotCallbacks.isEmpty()) {
            return;
        }
        INSTANCE.mOnShowMsgDotCallbacks.remove(onShowMsgDotCallback);
    }

    public static void loginEasemob(Runnable run) {
        //未登录，需要登录后，再进入会话界面
        UserInfo userInfo = AppManager.getAccountViewModel().getUserInfo();
        if (userInfo == null) {
            return;
        }

        String imId = userInfo.getIm_id();
        String md5Pwd = userInfo.getIm_password();
        if (TextUtils.isEmpty(imId) || TextUtils.isEmpty(md5Pwd)) {
            return;
        }

        AppManager.initKefu(App.getAppContext());
        ChatClient.getInstance().login(imId, md5Pwd, new Callback() {

            @Override
            public void onSuccess() {
                if (run != null) {
                    run.run();
                }
            }

            @Override
            public void onError(int code, String error) {
                if (code == Error.USER_ALREADY_LOGIN) {
                    if (run != null) {
                        run.run();
                    }
                }
                LogUtils.d(error);
            }

            @Override
            public void onProgress(int progress, String status) {
                LogUtils.d(progress);
            }

        });
    }

    public static void startEasemobChatRoom() {
        HwLeanCloudHelper.clearMsgNotification();
        ActivityUtils.startActivity(getChatRoomLaunchIntent());
    }

    private static Intent getChatRoomLaunchIntent() {
        VisitorInfo visitorInfo = ContentFactory.createVisitorInfo(null)
                .nickName(AppManager.getAccountViewModel().getUserInfo().getNickname())
                .name(AppManager.getAccountViewModel().getUserInfo().getNickname())
                .phone(AppManager.getAccountViewModel().getUserInfo().getMobile());

        UIProvider.getInstance().setUserProfileProvider((context, message, userAvatarView, usernickView) -> {
            if (Message.Direct.SEND == message.direct()) {
                ImageLoader.loadImage(AppManager.getAccountViewModel().getUserInfo().getAvatar(), userAvatarView, R.mipmap.ic_chat_right_default, R.mipmap.ic_chat_right_default);
            }
        });

        return new IntentBuilder(App.getAppContext())
                .setServiceIMNumber(BuildConfig.EASEMOB_CUSTOMER_SERVICE_ID)
                .setShowUserNick(false)
                .setVisitorInfo(visitorInfo).build();
    }

    public static boolean isHaveCustomerMsg() {
        return init().mHaveCuostomrMsg;
    }

    public static void haveCustomerMsg(int msgLength) {
        init().mHaveCuostomrMsg = msgLength > 0;
        List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
        if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
            return;
        }
        for (OnShowMsgDotCallback onShowMsgDotCallback : onShowMsgDotCallbacks) {
            onShowMsgDotCallback.onShowMsgDotCallback(0, 0, msgLength);
        }
    }

    private static void clearMsgNotification() {
        List<AVIMMessage> customerMessages = INSTANCE.mCustomerMessages;
        INSTANCE.mHaveCuostomrMsg = false;
        if (customerMessages == null) {
            return;
        }
        customerMessages.clear();
        List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
        if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
            return;
        }
        for (OnShowMsgDotCallback onShowMsgDotCallback : onShowMsgDotCallbacks) {
            onShowMsgDotCallback.onHideMsgCallback(0, 0, INSTANCE.mCustomerMessages.size());
        }
    }

    //app站内信,固件消息更新，客服消息提醒callback
    public interface OnShowMsgDotCallback {

        void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen);

        void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen);
    }

}
