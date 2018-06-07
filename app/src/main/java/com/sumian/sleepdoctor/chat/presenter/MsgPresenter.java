package com.sumian.sleepdoctor.chat.presenter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.common.media.activity.ImageGalleryActivity;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.chat.contract.MsgContract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jzz
 * on 2017/10/17.
 * desc:
 */

public class MsgPresenter implements MsgContract.Presenter {

    private static final String TAG = MsgPresenter.class.getSimpleName();

    private final static String imagePathName = "/image/";

    public static final int PIC_REQUEST_CODE_LOCAL = 0x01;
    public static final int PIC_REQUEST_CODE_CAMERA = 0x02;

    private MsgContract.View mView;

    private File cameraFile = null;
    private File storageDir = null;

    private AVIMMessage mLastMsg;

    private AVIMConversation mAVIMConversation;

    private AVIMTypedMessage mReplyMsg;

    private MsgPresenter(MsgContract.View view) {
        view.setPresenter(this);
        this.mView = view;
    }

    public static void init(MsgContract.View view) {
        new MsgPresenter(view);
    }

    @Override
    public void syncPreMsgHistory() {
        ArrayList<AVIMTypedMessage> avimTypedMessages = new ArrayList<>();
        mAVIMConversation.queryMessages(mLastMsg.getMessageId(), mLastMsg.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        mLastMsg = list.get(0);
                        for (AVIMMessage message : list) {
                            avimTypedMessages.add((AVIMTypedMessage) message);
                        }
                        mView.onSyncPreMsgHistorySuccess(avimTypedMessages);
                    } else {
                        mView.onNoHaveMoreMsg();
                    }
                } else {
                    mView.onSyncMsgHistoryFailed();
                }
            }
        });
    }

    @Override
    public void syncMsgHistory(String conversationId) {
        mAVIMConversation = AppManager.getChatEngine().getAVIMConversation(conversationId);
        mAVIMConversation.read();

        ArrayList<AVIMTypedMessage> avimTypedMessages = new ArrayList<>();

        mAVIMConversation.queryMessages(20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        mLastMsg = list.get(0);
                        for (AVIMMessage message : list) {
                            avimTypedMessages.add((AVIMTypedMessage) message);
                        }
                        mView.onSyncMsgHistorySuccess(avimTypedMessages);
                    } else {
                        mView.onNoHaveMsg();
                    }
                } else {
                    mView.onSyncMsgHistoryFailed();
                }
            }
        });
    }

    @Override
    public void sendTextMsg(String content, boolean isQuestion, AVIMTypedMessage replyMsg) {
        this.mReplyMsg = replyMsg;

        AVIMTextMessage msg = new AVIMTextMessage();
        msg.setText(content);

        Map<String, Object> attr = null;

        if (isQuestion || replyMsg != null) {
            attr = new HashMap<>();
        }

        if (isQuestion) {
            attr.put("type", "question");
        }

        if (replyMsg != null) {
            attr.put("mention_id", replyMsg.getMessageId());
            attr.put("type", "reply");
            attr.put("send_timestamp", replyMsg.getTimestamp());
            attr.put("question_msg_id", replyMsg.getMessageId());

            //添加被引用@的消息
            List<String> peerIdList = new ArrayList<>();
            peerIdList.add(replyMsg.getFrom());
            msg.setMentionList(peerIdList);
        }

        if (isQuestion || replyMsg != null) {
            msg.setAttrs(attr);
        }

        sendMsg(msg);
    }

    @Override
    public void sendPicMsg(AppCompatActivity activity, int type, AVIMTypedMessage replyMsg) {
        this.mReplyMsg = replyMsg;
        if (type == PIC_REQUEST_CODE_LOCAL) {//pic local
            picLocal(activity);
        } else {//pic camera
            picCamera(activity);
        }
    }

    private void picCamera(AppCompatActivity activity) {
        cameraFile = new File(generateImagePath(String.valueOf(AppManager.getAccountViewModel().getToken().user.id), App.Companion.getAppContext()), AppManager.getAccountViewModel().getToken().user.id + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < 24) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            activity.startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
        } else {//android 7.1之后的相机处理方式
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
        }
    }

    @Override
    public void resultCodeDelegate(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PIC_REQUEST_CODE_CAMERA:// capture new image
                    if (cameraFile != null && cameraFile.exists()) {
                        String localImagePath = cameraFile.getAbsolutePath();
                        AVIMImageMessage msg = initImageMsg(localImagePath);
                        sendMsg(msg);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void senAudioMsg(String audioFilePath, int duration, AVIMTypedMessage replyMsg) {
        this.mReplyMsg = replyMsg;

        AVIMAudioMessage msg = null;
        try {
            msg = new AVIMAudioMessage(audioFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> attr;

        if (replyMsg != null) {
            attr = new HashMap<>();
            attr.put("mention_id", "");
            attr.put("type", "reply");
            attr.put("send_timestamp", replyMsg.getTimestamp());
            attr.put("question_msg_id", replyMsg.getMessageId());
            if (msg != null) {
                msg.setAttrs(attr);
                List<String> peerIdList = new ArrayList<>();
                peerIdList.add(replyMsg.getFrom());
                msg.setMentionList(peerIdList);
            }
        }

        sendMsg(msg);
    }

    private void picLocal(Activity activity) {
        ImageGalleryActivity.show(activity, String.valueOf(new SelectOptions
                .Builder()
                .setHasCam(false)
                .setSelectCount(9)
                .setSelectedImages(new String[]{})
                .setCallback(images -> {
                    for (String image : images) {
                        Log.e(TAG, "doSelected: -------->" + image);
                        AVIMImageMessage msg = initImageMsg(image);
                        sendMsg(msg);
                    }
                }).build()));
    }

    private File generateImagePath(String userName, Context applicationContext) {
        String path;
        String pathPrefix = "/Android/data/" + applicationContext.getPackageName() + "/";
        path = pathPrefix + userName + imagePathName;
        return new File(getStorageDir(applicationContext), path);
    }

    private File getStorageDir(Context applicationContext) {
        if (storageDir == null) {
            //try to use sd card if possible
            File sdPath = Environment.getExternalStorageDirectory();
            if (sdPath.exists()) {
                return sdPath;
            }
            //use application internal storage instead
            storageDir = applicationContext.getFilesDir();
        }
        return storageDir;
    }

    @Nullable
    private AVIMImageMessage initImageMsg(String image) {
        AVIMImageMessage msg = null;
        try {
            msg = new AVIMImageMessage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> attr;

        if (mReplyMsg != null) {
            attr = new HashMap<>();
            attr.put("mention_id", mReplyMsg.getMessageId());
            attr.put("type", "reply");
            attr.put("send_timestamp", mReplyMsg.getTimestamp());
            attr.put("question_msg_id", mReplyMsg.getMessageId());
            if (msg != null) {
                msg.setAttrs(attr);

                //添加默认的被@的那条消息
                List<String> peerIdList = new ArrayList<>();
                peerIdList.add(mReplyMsg.getFrom());
                msg.setMentionList(peerIdList);
            }
        }

        return msg;
    }

    private void sendMsg(AVIMTypedMessage msg) {
        if (msg == null) {
            Log.e(TAG, "sendMsg: -------msg is null--->");
            return;
        }

        if (mView != null)
            mView.onSendingMsg(msg);

        AppManager.getChatEngine().sendMsg(mAVIMConversation, msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: ------msg send success--->");
                    if (mView != null) {
                        mView.onSendMsgSuccess(msg);
                    }
                } else {
                    if (mView != null) {
                        mView.onSendMsgFailed(msg);
                    }
                    mAVIMConversation.addToLocalCache(msg);
                    Log.e(TAG, "done: ------msg  send failed---->" + e.toString());
                }
            }
        });
    }

}
