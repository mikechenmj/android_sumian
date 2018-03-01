package com.sumian.sleepdoctor.chat.contract;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
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

        void onSendingMsg(AVIMTypedMessage msg);

        void onSendMsgSuccess(AVIMTypedMessage msg);

        void onSendMsgFailed(AVIMTypedMessage msg);

        void onSyncMsgHistorySuccess(List<AVIMTypedMessage> messages);

        void onSyncPreMsgHistorySuccess(List<AVIMTypedMessage> messages);

        void onSyncMsgHistoryFailed();

        void onNoHaveMsg();

        void onNoHaveMoreMsg();
    }

    interface Presenter extends BasePresenter {

        void sendTextMsg(String content, boolean isQuestion, AVIMTypedMessage replyMsg);

        void sendPicMsg(AppCompatActivity activity, int type, AVIMTypedMessage replyMsg);

        void senAudioMsg(String audioFilePath, int duration, AVIMTypedMessage replyMsg);

        void syncMsgHistory(String conversationId);

        void syncPreMsgHistory();

        void resultCodeDelegate(int requestCode, int resultCode, Intent data);

    }

}
