package com.sumian.sleepdoctor.chat.contract;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

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

        void onPermissionsDenied();

    }

    interface Presenter extends BasePresenter {

        int CAMERA_PERM = 1;

        int RECORD_PERM = 2;

        void joinChatRoom(String conversationId);

        void getGroupDetail(int groupId);

        void doSendTextMsg(String content);

        void sendPic(Activity activity, int type);

        void sendVoice(Activity activity, String recordFilePath, int second);

        void syncMsgHistory();

        void syncPreMsgHistory(boolean isLoadPre);

        void resultCodeDelegate(int requestCode, int resultCode, Intent data);

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);


    }

}
