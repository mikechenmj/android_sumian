package com.sumian.sleepdoctor.chat.contract;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

import java.util.List;

/**
 * Created by sm
 * on 2018/1/29.
 * desc:
 */

public interface ChatContract {


    interface Presenter {

        void registerMsgHandler();

        void unRegisterMsgHandler();

        void joinChatGroup(String conversationId);

        void loginImServer();

        void logoutImServer();

        void sendMsg(AVIMMessage msg);

        AVIMConversation getAVIMConversation();

        List<AVIMMessage> getHistoryMsg();

    }
}
