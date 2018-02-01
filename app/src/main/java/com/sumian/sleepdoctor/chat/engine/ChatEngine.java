package com.sumian.sleepdoctor.chat.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMClientStatusCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.sumian.sleepdoctor.BuildConfig;
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

    private static final int MSG_WHAT_LOGIN = 0x01;

    private AVIMClient mAVIMClient;

    private AVIMConversation mAVIMConversation;

    private String mConversationId;

    private Handler mHandler;

    private AVIMTypedMessageHandler<AVIMTypedMessage> mMessageHandler;

    private List<OnMsgCallback> mOnMsgCallbacks;


    public ChatEngine(Context context) {
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        AVOSCloud.initialize(context, BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY);
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
        registerMsgHandler();
    }

    public ChatEngine setOnMsgCallback(OnMsgCallback onMsgCallback) {
        if (mOnMsgCallbacks == null) {
            mOnMsgCallbacks = new ArrayList<>();
        }
        if (mOnMsgCallbacks.contains(onMsgCallback)) return this;
        mOnMsgCallbacks.add(onMsgCallback);
        return this;
    }

    public void removeOnMsgCallback(OnMsgCallback onMsgCallback) {
        if (mOnMsgCallbacks == null || mOnMsgCallbacks.isEmpty()) return;
        mOnMsgCallbacks.remove(onMsgCallback);
    }

    @Override
    public void registerMsgHandler() {

        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, mMessageHandler = new AVIMTypedMessageHandler<AVIMTypedMessage>() {

            @Override
            public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
                super.onMessage(message, conversation, client);
                Log.e(TAG, "onMessage: --------->" + message.toString());
                if (mOnMsgCallbacks == null || mOnMsgCallbacks.isEmpty()) return;
                for (OnMsgCallback onMsgCallback : mOnMsgCallbacks) {
                    onMsgCallback.onMsgCallback(message);
                }
            }

            @Override
            public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
                super.onMessageReceipt(message, conversation, client);
                Log.e(TAG, "onMessageReceipt: --------->" + message.toString());
            }
        });
    }

    @Override
    public void unRegisterMsgHandler() {
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, mMessageHandler);
    }

    @Override
    public void joinChatGroup(String conversationId) {
        this.mConversationId = conversationId;
        this.mAVIMClient = AVIMClient.getInstance(AppManager.getAccountViewModel().getToken().user.leancloud_id);
        //AVIMClient.setAutoOpen(true);
        mAVIMClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    mAVIMConversation = avimClient.getConversation(conversationId);
                    Log.e(TAG, "done: -------登录 im server 成功 ---->");
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_LOGIN, 1000);
                }
            }
        });

        mAVIMClient.getClientStatus(new AVIMClientStatusCallback() {
            @Override
            public void done(AVIMClient.AVIMClientStatus avimClientStatus) {
                Log.e(TAG, "done: --------->" + avimClientStatus);
            }
        });
    }

    @Override
    public void loginImServer() {
        String leancloudId = AppManager.getAccountViewModel().getToken().user.leancloud_id;
        if (TextUtils.isEmpty(leancloudId)) return;
        this.mAVIMClient = AVIMClient.getInstance(leancloudId);
        //AVIMClient.setAutoOpen(true);
        mAVIMClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: -------登录 im server 成功 ---->");
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_LOGIN, 1000);
                }
            }
        });

        mAVIMClient.getClientStatus(new AVIMClientStatusCallback() {
            @Override
            public void done(AVIMClient.AVIMClientStatus avimClientStatus) {
                Log.e(TAG, "done: --------->" + avimClientStatus);
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
    public void sendMsg(AVIMMessage msg) {
        msg.setTimestamp(System.currentTimeMillis());

        this.mAVIMConversation.sendMessage(msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: ------msg send success--->");
                    mAVIMConversation.addToLocalCache(msg);
                } else {
                    Log.e(TAG, "done: ------msg  send failed---->" + e.toString());
                }
            }
        });
    }

    @Override
    public AVIMConversation getAVIMConversation() {
        return mAVIMConversation;
    }

    @Override
    public AVIMConversation getAVIMConversation(String conversationId) {
        return this.mAVIMConversation = mAVIMClient.getConversation(conversationId);
    }


    @Override
    public List<AVIMMessage> getHistoryMsg() {
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_LOGIN:
                joinChatGroup(mConversationId);
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnMsgCallback {

        void onMsgCallback(AVIMTypedMessage msg);
    }
}
