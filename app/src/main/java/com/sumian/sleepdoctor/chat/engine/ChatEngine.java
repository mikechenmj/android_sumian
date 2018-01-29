package com.sumian.sleepdoctor.chat.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMClientStatusCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.chat.contract.ChatContract;

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

    private Handler mHandler;

    private AVIMMessageHandler mMessageHandler;

    public ChatEngine() {
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
        AVOSCloud.initialize(App.Companion.getAppContext(), BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY);
    }

    @Override
    public void loginImServer() {
        AVIMClient client = AVIMClient.getInstance(AppManager.getAccountViewModel().getToken().user.leancloud_id);
        client.getClientStatus(new AVIMClientStatusCallback() {
            @Override
            public void done(AVIMClient.AVIMClientStatus AVIMClientStatus) {

                Log.e(TAG, "done: --------->" + AVIMClientStatus.getCode());

            }
        });
        client.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    mAVIMClient = avimClient;
                    Log.e(TAG, "done: -------登录 im server 成功 ---->");
                } else {
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_LOGIN, 1000);
                }
            }
        });
    }

    @Override
    public void registerMsgHandler() {
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, mMessageHandler = new AVIMMessageHandler() {
            @Override
            public void onMessage(AVIMMessage avimMessage, AVIMConversation avimConversation, AVIMClient avimClient) {
                Log.e(TAG, "onMessage: --------->" + avimMessage.toString());
            }

            @Override
            public void onMessageReceipt(AVIMMessage avimMessage, AVIMConversation avimConversation, AVIMClient avimClient) {

                Log.e(TAG, "onMessageReceipt: --------->" + avimMessage.toString());
            }
        });
    }

    @Override
    public void unRegisterMsgHandler() {
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, mMessageHandler);
    }

    @Override
    public void joinChatGroup(String conversationId) {
        this.mAVIMConversation = this.mAVIMClient.getConversation(conversationId, false, false);
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
        this.mAVIMConversation.sendMessage(msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: ------msg send success--->");
                } else {
                    Log.e(TAG, "done: ------msg  send failed---->");
                }
            }
        });
    }

    @Override
    public AVIMConversation getAVIMConversation() {
        return mAVIMConversation;
    }

    @Override
    public List<AVIMMessage> getHistoryMsg() {
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_LOGIN:
                loginImServer();
                break;
            default:
                break;
        }
        return true;
    }
}
