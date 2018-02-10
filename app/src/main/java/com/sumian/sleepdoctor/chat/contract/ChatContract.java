package com.sumian.sleepdoctor.chat.contract;

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

        void unRegisterMsgHandler();

        void loginImServer();

        void logoutImServer();

        void sendMsg(AVIMMessage msg, AVIMConversationCallback conversationCallback);

        AVIMConversation getAVIMConversation(String conversationId);

    }
}
