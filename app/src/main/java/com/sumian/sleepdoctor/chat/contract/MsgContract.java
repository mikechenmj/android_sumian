package com.sumian.sleepdoctor.chat.contract;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/17.
 * desc:
 */

public interface MsgContract {

    interface View extends BaseView<Presenter> {

        void onSendingMsg(AVIMMessage msg);

        void onSendMsgSuccess(AVIMMessage msg);

        void onSendMsgFailed(AVIMMessage msg);

        void onSyncMsgHistorySuccess(List<AVIMMessage> messages);

        void onSyncPreMsgHistorySuccess(List<AVIMMessage> messages);

        void onSyncMsgHistoryFailed();

        void onReceiveMsg(AVIMMessage msg);

        void onNoHaveMsg();

        void onPrepareLogin();

        void onLoginSuccess();

        void onLoginFailed();

    }

    interface Presenter extends BasePresenter {

        void loginChatRoom();

        void getGroupDetail(int groupId);

        void doSendTextMsg(String content);

        void sendPic(Activity activity, int type);

        void sendVoice(String recordFilePath, int second);

        void syncMsgHistory();

        void syncPreMsgHistory(boolean isLoadPre);

        void resultCodeDelegate(int requestCode, int resultCode, Intent data);


    }

}
