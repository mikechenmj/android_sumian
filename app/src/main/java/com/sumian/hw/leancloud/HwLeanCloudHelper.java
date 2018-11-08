package com.sumian.hw.leancloud;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMMessageOption;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
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
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.utils.AppUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/16.
 * desc:
 */

public final class HwLeanCloudHelper {

    public static final String TAG = HwLeanCloudHelper.class.getSimpleName();

    public static final int SERVICE_TYPE_ONLINE_DOCTOR = 0x01;//速眠医生
    public static final int SERVICE_TYPE_ONLINE_CUSTOMER = 0x02;//线上客服
    public static final int SERVICE_TYPE_MAIL = 0x03;//站内信

    private static final int MSG_TYPE_TEXT = 0x21;
    private static final int MSG_TYPE_IMAGE = 0x22;
    private static final int MSG_TYPE_VOICE = 0x23;

    private static volatile HwLeanCloudHelper INSTANCE;
    private static ReentrantLock mLock = new ReentrantLock();

    private List<OnMsgCallback> mOnMsgCallbacks;

    private OnConversationCallback mOnConversationCallback;

    private List<OnShowMsgDotCallback> mOnShowMsgDotCallbacks;

    private AVIMConversation mCustomerConversations;
    private AVIMConversation mDoctorConversations;

    private List<AVIMMessage> mCustomerMessages;//线上客服聊天消息
    private List<AVIMMessage> mDoctorMessages;//速眠医生聊天消息
    private List<AVIMMessage> mSysMessages;//系统消息

    private AVIMClient mAVIMClient;

    private boolean mHaveCuostomrMsg;

    private HwLeanCloudHelper(Context context) {
        // 初始化参数依次为 context, AppId, AppKey
        PushService.setDefaultChannelId(context, "push_channel");
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
//        AVOSCloud.initialize(context, BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY);
        this.mCustomerMessages = new ArrayList<>();
        this.mDoctorMessages = new ArrayList<>();
        this.mSysMessages = new ArrayList<>();
        register();
    }

    public static HwLeanCloudHelper init(Context context) {
        if (INSTANCE == null) {
            synchronized (HwLeanCloudHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HwLeanCloudHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public static void addOnMsgCallback(OnMsgCallback onMsgCallback) {
        List<OnMsgCallback> onMsgCallbacks = INSTANCE.mOnMsgCallbacks;
        if (onMsgCallbacks == null) {
            onMsgCallbacks = new ArrayList<>();
            INSTANCE.mOnMsgCallbacks = onMsgCallbacks;
        }
        if (onMsgCallbacks.contains(onMsgCallback)) {
            return;
        }
        onMsgCallbacks.add(onMsgCallback);
    }

    public static void removeOnMsgCallback(OnMsgCallback onMsgCallback) {
        List<OnMsgCallback> onMsgCallbacks = INSTANCE.mOnMsgCallbacks;
        if (onMsgCallbacks == null || onMsgCallbacks.isEmpty()) {
            return;
        }
        onMsgCallbacks.remove(onMsgCallback);
    }

    public static void addOnAdminMsgCallback(OnShowMsgDotCallback onShowMsgDotCallback) {
        List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
        if (onShowMsgDotCallbacks == null) {
            onShowMsgDotCallbacks = new ArrayList<>();
            INSTANCE.mOnShowMsgDotCallbacks = onShowMsgDotCallbacks;
        }
        if (onShowMsgDotCallbacks.contains(onShowMsgDotCallback)) {
            return;
        }
        onShowMsgDotCallbacks.add(onShowMsgDotCallback);
    }

    public static void removeOnAdminMsgCallback(OnShowMsgDotCallback onShowMsgDotCallback) {
        List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
        if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
            return;
        }
        onShowMsgDotCallbacks.remove(onShowMsgDotCallback);
    }

    public static void addOnConversationCallback(OnConversationCallback conversationCallback) {
        INSTANCE.mOnConversationCallback = conversationCallback;
    }

    public static void removeOnConversationCallback() {
        INSTANCE.mOnConversationCallback = null;
    }

    public static void loginLeanCloud() {
        loginEasemob(null);
        // Tom 用自己的名字作为clientId，获取AVIMClient对象实例
        String clientId = AppManager.getAccountViewModel().getLeanCloudId();
        if (TextUtils.isEmpty(clientId)) {
            return;
        }

        INSTANCE.mAVIMClient = AVIMClient.getInstance(clientId);
        // 与服务器连接
        INSTANCE.mAVIMClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: -----登录成功--->");

                    INSTANCE.mAVIMClient = client;

                } else {
                    Log.e(TAG, "done: -----登录失败--->");
                }
            }
        });
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
        HwLeanCloudHelper.clearMsgNotification(HwLeanCloudHelper.SERVICE_TYPE_ONLINE_CUSTOMER);
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

        return new IntentBuilder(App.Companion.getAppContext())
                .setServiceIMNumber(BuildConfig.EASEMOB_CUSTOMER_SERVICE_ID)
                .setShowUserNick(false)
                .setVisitorInfo(visitorInfo).build();
    }

    public static void establishConversationWithService(int serviceType) {

        String conversationMembers = serviceType == SERVICE_TYPE_ONLINE_DOCTOR ? BuildConfig.LEANCLOUD_DOCTOR_SERVICE_ID
                : BuildConfig.LEANCLOUD_ONLINE_SERVICE_ID;
        String conversationTag = AppManager.getAccountViewModel().getLeanCloudId() + " & " + conversationMembers;

        AVIMClient avimClient = INSTANCE.mAVIMClient;

        if (INSTANCE.mOnConversationCallback != null) {
            INSTANCE.mOnConversationCallback.onEstablishConversationCallback();
        }
        // 创建与server之间的对话
        avimClient.createConversation(Collections.singletonList(conversationMembers), conversationTag
                , null, false, true,
                new AVIMConversationCreatedCallback() {

                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            Log.d(TAG, "done: ------建立会话成功------>" + serviceType);
                            if (serviceType == SERVICE_TYPE_ONLINE_CUSTOMER) {
                                INSTANCE.mCustomerConversations = conversation;
                            } else {
                                INSTANCE.mDoctorConversations = conversation;
                            }

                            if (INSTANCE.mOnConversationCallback != null) {
                                INSTANCE.mOnConversationCallback.onEstablishConversationSuccessCallback();
                            }

                        } else {
                            Log.d(TAG, "done: ------建立会话失败----->" + serviceType);
                            if (INSTANCE.mOnConversationCallback != null) {
                                INSTANCE.mOnConversationCallback.onEstablishConversationFailedCallback();
                            }
                        }
                    }
                });
    }

    public static AVIMConversation getConversation(int serviceType) {
        switch (serviceType) {
            case SERVICE_TYPE_MAIL:
                return INSTANCE.mAVIMClient.getServiceConversation(BuildConfig.LEANCLOUD_BROADCAST_CONVERSATION_ID);
            case SERVICE_TYPE_ONLINE_CUSTOMER:
                return INSTANCE.mCustomerConversations;
            case SERVICE_TYPE_ONLINE_DOCTOR:
                return INSTANCE.mDoctorConversations;
            default:
                return INSTANCE.mCustomerConversations;
        }
    }

    public static void registerPushService() {
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                    uploadDeviceInfo(installationId);
                }
            }
        });

        //设置后台自动重启
        PushService.setAutoWakeUp(true);
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(App.Companion.getAppContext(), AppUtil.getMainClass());
    }

    private static void uploadDeviceInfo(String installationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("device_type", 0);
        map.put("device_token", installationId);
        map.put("system_version", Build.VERSION.SDK_INT);

        Call<Object> call = AppManager
                .getHwHttpService()
                .uploadDeviceInfo(map);

        call.enqueue(new BaseSdResponseCallback<Object>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {

            }

            @Override
            protected void onSuccess(Object response) {
            }

            @Override
            protected void onFinish() {

            }
        });
    }

    public static boolean isHaveCustomerMsg() {
        return INSTANCE.mHaveCuostomrMsg;
    }

    public static void haveCustomerMsg(int msgLength) {
        INSTANCE.mHaveCuostomrMsg = msgLength > 0;
        List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
        if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
            return;
        }
        for (OnShowMsgDotCallback onShowMsgDotCallback : onShowMsgDotCallbacks) {
            onShowMsgDotCallback.onShowMsgDotCallback(INSTANCE.mSysMessages.size(), INSTANCE.mDoctorMessages.size(), msgLength);
        }
    }

    public static void clearMsgNotification(int serviceType) {
        switch (serviceType) {
            case SERVICE_TYPE_MAIL:
                List<AVIMMessage> sysMessages = INSTANCE.mSysMessages;
                if (sysMessages == null || sysMessages.isEmpty()) {
                    return;
                }
                sysMessages.clear();
                break;
            case SERVICE_TYPE_ONLINE_CUSTOMER:
                List<AVIMMessage> customerMessages = INSTANCE.mCustomerMessages;
                INSTANCE.mHaveCuostomrMsg = false;
                if (customerMessages == null) {
                    return;
                }
                customerMessages.clear();
                break;
            case SERVICE_TYPE_ONLINE_DOCTOR:
                List<AVIMMessage> doctorMessages = INSTANCE.mDoctorMessages;
                if (doctorMessages == null || doctorMessages.isEmpty()) {
                    return;
                }
                doctorMessages.clear();
                break;
            default:
                break;
        }

        List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
        if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
            return;
        }
        for (OnShowMsgDotCallback onShowMsgDotCallback : onShowMsgDotCallbacks) {
            onShowMsgDotCallback.onHideMsgCallback(INSTANCE.mSysMessages.size(), INSTANCE.mDoctorMessages.size(), INSTANCE.mCustomerMessages.size());
        }
    }

    private static void sendMsg(int serviceType, int msgType, String content) {
        AVIMMessage avimMessage = null;
        switch (msgType) {
            case MSG_TYPE_TEXT:
                AVIMTextMessage textMsg = new AVIMTextMessage();
                textMsg.setText(content);
                avimMessage = textMsg;
                break;
            case MSG_TYPE_IMAGE:
                try {
                    avimMessage = new AVIMImageMessage(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MSG_TYPE_VOICE:
                try {
                    avimMessage = new AVIMAudioMessage(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        if (avimMessage != null) {
            avimMessage.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSending);
            avimMessage.setTimestamp(System.currentTimeMillis());

            // 发送消息
            List<OnMsgCallback> onMsgCallbacks = INSTANCE.mOnMsgCallbacks;
            if (onMsgCallbacks != null && !onMsgCallbacks.isEmpty()) {
                for (OnMsgCallback onMsgCallback : onMsgCallbacks) {
                    //Log.e(TAG, "sendTextMsg: ------------->msgStatus=" + msg.getMessageStatus() + "  timestamp=" + msg.getTimestamp());
                    onMsgCallback.onSendingMsgCallback(avimMessage);
                }
            }

            AVIMConversation avimConversation = serviceType == SERVICE_TYPE_ONLINE_CUSTOMER ? INSTANCE.mCustomerConversations : INSTANCE.mDoctorConversations;

            if (avimConversation != null) {
                //设置消息回执
                AVIMMessageOption messageOption = new AVIMMessageOption();
                messageOption.setReceipt(true);
                AVIMMessage finalMsg = avimMessage;
                avimConversation.sendMessage(avimMessage, messageOption, new AVIMConversationCallback() {

                    @Override
                    public void done(AVIMException e) {
                        if (onMsgCallbacks == null || onMsgCallbacks.isEmpty()) {
                            return;
                        }
                        for (OnMsgCallback onMsgCallback : onMsgCallbacks) {
                            if (e == null) {
                                Log.d(TAG, "done: ------->发送成功----->msgStatus=" + finalMsg.getMessageStatus() + "     timestamp=" + finalMsg.getTimestamp());
                                finalMsg.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSent);
                                onMsgCallback.onSendMsgSuccess(finalMsg);
                            } else {
                                Log.d(TAG, "done: ------>发送失败！");
                                finalMsg.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed);
                                onMsgCallback.onSendMsgFailed(finalMsg);
                            }
                            avimConversation.addToLocalCache(finalMsg);
                        }
                    }
                });
            }
        }
    }

    public static void sendTextMsg(int serviceType, String content) {
        sendMsg(serviceType, MSG_TYPE_TEXT, content);
    }

    public static void sendImageMsg(int serviceType, String localImagePath) {
        sendMsg(serviceType, MSG_TYPE_IMAGE, localImagePath);
    }

    public static void sendVoiceMsg(int serviceType, String localVoicePath) {
        sendMsg(serviceType, MSG_TYPE_VOICE, localVoicePath);
    }

    private void register() {
        //注册默认的消息处理逻辑
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new AVIMTypedMessageHandler<AVIMTypedMessage>() {
            @Override
            public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
                super.onMessage(message, conversation, client);

                //站内信,系统消息
                if (BuildConfig.LEANCLOUD_BROADCAST_CONVERSATION_ID.equals(conversation.getConversationId())) {
                    mSysMessages.add(message);
                    Log.d(TAG, "onMessage: ---系统消息,站内信下发的消息------>" + mSysMessages.toString());

                    List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
                    if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
                        return;
                    }
                    for (OnShowMsgDotCallback onShowMsgDotCallback : onShowMsgDotCallbacks) {
                        onShowMsgDotCallback.onShowMsgDotCallback(mSysMessages.size(), mDoctorMessages.size(),
                                mCustomerMessages.size());
                    }
                    return;
                }

                String name = conversation.getName();
                if (TextUtils.isEmpty(name)) {
                    return;
                }

                if (name.equals(AppManager.getAccountViewModel().getLeanCloudId() + " & " + BuildConfig.LEANCLOUD_DOCTOR_SERVICE_ID)) {
                    //医生会话
                    Log.d(TAG, "onMessage: ------医生会话下发的消息------->");
                    mDoctorMessages.add(message);
                } else {//客服会话
                    Log.d(TAG, "onMessage: ------线上客服会话下发的消息----->");
                    mCustomerMessages.add(message);
                }

                List<OnMsgCallback> onMsgCallbacks = INSTANCE.mOnMsgCallbacks;
                if (onMsgCallbacks != null && !onMsgCallbacks.isEmpty()) {
                    for (OnMsgCallback onMsgCallback : onMsgCallbacks) {
                        onMsgCallback.onReceiveMsg(message);
                    }
                }

                List<OnShowMsgDotCallback> onShowMsgDotCallbacks = INSTANCE.mOnShowMsgDotCallbacks;
                if (onShowMsgDotCallbacks == null || onShowMsgDotCallbacks.isEmpty()) {
                    return;
                }
                for (OnShowMsgDotCallback onShowMsgDotCallback : onShowMsgDotCallbacks) {
                    onShowMsgDotCallback.onShowMsgDotCallback(mSysMessages.size(), mDoctorMessages.size(), mCustomerMessages.size());
                }

            }

            @Override
            public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
                super.onMessageReceipt(message, conversation, client);
                Log.d(TAG, "onMessageReceipt: ---消息被成功接收-------->msgStatus=" + message.getMessageStatus());
                List<OnMsgCallback> onMsgCallbacks = INSTANCE.mOnMsgCallbacks;
                if (onMsgCallbacks == null || onMsgCallbacks.isEmpty()) {
                    return;
                }
                for (OnMsgCallback onMsgCallback : onMsgCallbacks) {
                    onMsgCallback.onRemoteReceipt(message);
                }
            }
        });
    }

    public interface OnConversationCallback {

        void onEstablishConversationCallback();

        void onEstablishConversationSuccessCallback();

        void onEstablishConversationFailedCallback();
    }

    public interface OnMsgCallback {

        void onSendingMsgCallback(AVIMMessage msg);

        void onSendMsgSuccess(AVIMMessage msg);

        void onRemoteReceipt(AVIMMessage msg);

        void onSendMsgFailed(AVIMMessage msg);

        void onReceiveMsg(AVIMMessage message);
    }

    //app站内信,固件消息更新，客服消息提醒callback
    public interface OnShowMsgDotCallback {

        void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen);

        void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen);
    }

}
