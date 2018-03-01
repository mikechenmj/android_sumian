package com.sumian.sleepdoctor.chat.contract;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;

/**
 * Created by sm
 * on 2018/1/29.
 * desc:
 */

public interface ChatContract {


    interface Presenter {

        void registerMsgHandler();

        void registerConversationHandler();

        void unRegisterMsgHandler();

        AVIMClient getAVIMClient();

        void loginImServer();

        void logoutImServer();

        void sendMsg(AVIMConversation avimConversation, AVIMMessage msg, AVIMConversationCallback conversationCallback);

        AVIMConversation getAVIMConversation(String conversationId);

    }
}
