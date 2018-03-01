package com.sumian.sleepdoctor.chat.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMClientOpenOption;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMClientStatusCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.chat.contract.ChatContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sm
 * on 2018/1/29.
 * desc:
 */

public class ChatEngine implements ChatContract.Presenter, Handler.Callback {

    private static final String TAG = ChatEngine.class.getSimpleName();

    public static final String MSG_TYPE_ATTR = "type";
    public static final String MSG_QUESTION_TYPE = "question";
    public static final String MSG_REPLY_TYPE = "reply";
    public static final String MSG_SEND_TIMESTAMP = "send_timestamp";
    public static final String MSG_QUESTION_MSG_ID = "question_msg_id";

    private static final int MSG_WHAT_LOGIN = 0x01;

    private AVIMClient mAVIMClient;

    private Handler mHandler;

    private AVIMTypedMessageHandler<AVIMTypedMessage> mMessageHandler;

    private List<OnMsgCallback> mOnMsgCallbacks;
    private List<OnUpdateUnReadMsgCountCallback> mOnUpdateUnReadMsgCountCallbacks;


    public ChatEngine(Context context) {
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        AVOSCloud.initialize(context, BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY);
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
        AVIMClient.setAutoOpen(false);
        AVIMClient.setClientEventHandler(new AVIMClientEventHandler() {
            @Override
            public void onConnectionPaused(AVIMClient avimClient) {
                Log.e(TAG, "onConnectionPaused: ----------->");
            }

            @Override
            public void onConnectionResume(AVIMClient avimClient) {
                Log.e(TAG, "onConnectionResume: ------------->");
            }

            @Override
            public void onClientOffline(AVIMClient avimClient, int i) {
                Log.e(TAG, "onClientOffline: ------------------->");
            }
        });
        registerConversationHandler();
        registerMsgHandler();
    }

    public void addOnMsgCallback(OnMsgCallback onMsgCallback) {
        if (mOnMsgCallbacks == null) {
            mOnMsgCallbacks = new ArrayList<>();
        }
        if (mOnMsgCallbacks.contains(onMsgCallback)) return;
        mOnMsgCallbacks.add(onMsgCallback);
    }

    public void removeOnMsgCallback(OnMsgCallback onMsgCallback) {
        if (mOnMsgCallbacks == null || mOnMsgCallbacks.isEmpty()) return;
        mOnMsgCallbacks.remove(onMsgCallback);
    }

    public void addOnUnReadMsgCountCallback(OnUpdateUnReadMsgCountCallback updateUnReadMsgCountCallback) {
        if (mOnUpdateUnReadMsgCountCallbacks == null) {
            mOnUpdateUnReadMsgCountCallbacks = new ArrayList<>();
        }
        if (mOnUpdateUnReadMsgCountCallbacks.contains(updateUnReadMsgCountCallback)) return;
        mOnUpdateUnReadMsgCountCallbacks.add(updateUnReadMsgCountCallback);
    }

    public void removeOnUnReadMsgCountCallback(OnUpdateUnReadMsgCountCallback updateUnReadMsgCountCallback) {
        if (mOnUpdateUnReadMsgCountCallbacks == null || mOnUpdateUnReadMsgCountCallbacks.isEmpty())
            return;
        mOnUpdateUnReadMsgCountCallbacks.remove(updateUnReadMsgCountCallback);
    }

    @Override
    public void registerMsgHandler() {
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, mMessageHandler = new AVIMTypedMessageHandler<AVIMTypedMessage>() {

            @Override
            public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
                super.onMessage(message, conversation, client);
                // Log.e(TAG, "onMessage: --------->" + message.toString());
                if (mOnMsgCallbacks == null || mOnMsgCallbacks.isEmpty()) return;
                for (OnMsgCallback onMsgCallback : mOnMsgCallbacks) {
                    onMsgCallback.onReceiverMsgCallback(message);
                }
            }

            @Override
            public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
                super.onMessageReceipt(message, conversation, client);
                //  Log.e(TAG, "onMessageReceipt: --------->" + message.toString());
            }
        });
    }

    @Override
    public void registerConversationHandler() {
        AVIMClient.setUnreadNotificationEnabled(true);
        AVIMMessageManager.setConversationEventHandler(new AVIMConversationEventHandler() {
            @Override
            public void onMemberLeft(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {

            }

            @Override
            public void onMemberJoined(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {

            }

            @Override
            public void onKicked(AVIMClient avimClient, AVIMConversation avimConversation, String s) {

            }

            @Override
            public void onInvited(AVIMClient avimClient, AVIMConversation avimConversation, String s) {

            }

            @Override
            public void onUnreadMessagesCountUpdated(AVIMClient client, AVIMConversation conversation) {
                super.onUnreadMessagesCountUpdated(client, conversation);
                Log.e(TAG, "onUnreadMessagesCountUpdated: ------->" + conversation.getUnreadMessagesCount());
                if (mOnUpdateUnReadMsgCountCallbacks == null || mOnUpdateUnReadMsgCountCallbacks.isEmpty())
                    return;
                for (OnUpdateUnReadMsgCountCallback onUpdateUnReadMsgCountCallback : mOnUpdateUnReadMsgCountCallbacks) {
                    onUpdateUnReadMsgCountCallback.onUpdateUnReadMsgCount(client, conversation, conversation.getUnreadMessagesCount());
                }
            }
        });

    }

    @Override
    public AVIMClient getAVIMClient() {
        return mAVIMClient;
    }

    @Override
    public void unRegisterMsgHandler() {
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, mMessageHandler);
    }

    @Override
    public void loginImServer() {
        UserProfile userProfile = AppManager.getAccountViewModel().getUserProfile();
        if (userProfile == null) return;
        String leancloudId = userProfile.leancloud_id;
        if (TextUtils.isEmpty(leancloudId)) return;
        this.mAVIMClient = AVIMClient.getInstance(leancloudId);

        AVIMClientOpenOption options = new AVIMClientOpenOption();
        options.setForceSingleLogin(true);

        mAVIMClient.open(options, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: -------登录 im server 成功 ---->");
                } else {
                    Log.e(TAG, "done: ---------登录  im server  失败  1s 后尝试重新登录--->");
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_LOGIN, 2000);
                }
            }
        });

        mAVIMClient.getClientStatus(new AVIMClientStatusCallback() {
            @Override
            public void done(AVIMClient.AVIMClientStatus avimClientStatus) {
                Log.e(TAG, "done: -----AVIMClientStatus------->" + avimClientStatus);
            }
        });
    }

    @Override
    public void logoutImServer() {
        if (mAVIMClient != null) {
            mAVIMClient.close(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e == null) {
                        Log.e(TAG, "done: -------退出成功-->");
                    }
                }
            });
        }
    }

    @Override
    public void sendMsg(AVIMConversation avimConversation, AVIMMessage msg, AVIMConversationCallback conversationCallback) {
        msg.setTimestamp(System.currentTimeMillis());
        avimConversation.sendMessage(msg, conversationCallback);
    }

    @Override
    public AVIMConversation getAVIMConversation(String conversationId) {
        return mAVIMClient.getConversation(conversationId);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_LOGIN:
                Log.e(TAG, "handleMessage: -------------->登录失败,重试登录尝试,1s 一次");
                loginImServer();
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnMsgCallback {

        void onReceiverMsgCallback(AVIMTypedMessage msg);
    }

    public interface OnUpdateUnReadMsgCountCallback {

        void onUpdateUnReadMsgCount(AVIMClient client, AVIMConversation conversation, int unReadMsgCount);
    }
}
