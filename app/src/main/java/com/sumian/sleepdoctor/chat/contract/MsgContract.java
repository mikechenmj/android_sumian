package com.sumian.sleepdoctor.chat.contract;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

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

        void onPermissionsDenied();

    }

    interface Presenter extends BasePresenter {

        int CAMERA_PERM = 1;

        int RECORD_PERM = 2;

        void joinChatRoom(String conversationId);

        void getGroupDetail(int groupId);

        void sendTextMsg(String content, boolean isQuestion, AVIMTypedMessage replyMsg);

        void sendPicMsg(Activity activity, int type);

        void sendVoice(Activity activity, String recordFilePath, int second);

        void syncMsgHistory(String conversationId);

        void syncPreMsgHistory(boolean isLoadPre);

        void resultCodeDelegate(int requestCode, int resultCode, Intent data);

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);


    }

}
