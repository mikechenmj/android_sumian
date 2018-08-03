package com.sumian.hw.leancloud.presenter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.sumian.common.media.ImagePickerActivity;
import com.sumian.common.media.SelectOptions;
import com.sumian.sleepdoctor.app.HwApp;
import com.sumian.hw.leancloud.LeanCloudHelper;
import com.sumian.hw.leancloud.contract.MsgContract;
import com.sumian.sleepdoctor.app.AppManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Created by jzz
 * on 2017/10/17.
 * desc:
 */

public class MsgPresenter implements MsgContract.Presenter, LeanCloudHelper.OnMsgCallback, LeanCloudHelper.OnConversationCallback {

    private static final String TAG = MsgPresenter.class.getSimpleName();

    private final static String imagePathName = "/image/";
    public static final int PIC_REQUEST_CODE_LOCAL = 0x01;
    public static final int PIC_REQUEST_CODE_CAMERA = 0x02;

    private WeakReference<MsgContract.View> mViewWeakReference;
    private int mServiceType;

    private long mTimestamp;
    private String mMsgId;

    private boolean mIsLoad;

    private File cameraFile;
    private File storageDir = null;
    private String mLocalImagePath;

    private MsgPresenter(MsgContract.View view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        LeanCloudHelper.addOnMsgCallback(this);
        LeanCloudHelper.addOnConversationCallback(this);
    }

    public static void init(MsgContract.View view) {
        new MsgPresenter(view);
    }


    @Override
    public void loginServiceType(int serviceType) {
        this.mServiceType = serviceType;
        LeanCloudHelper.establishConversationWithService(serviceType);
    }

    @Override
    public void syncMsgHistory(int serviceType) {
        this.mServiceType = serviceType;
        // LeanCloudHelper.clearMsgNotification(mServiceType);
        syncPreMsgHistory(false);
    }

    @Override
    public void syncPreMsgHistory(boolean isLoadPre) {
        if (mIsLoad) {
            return;
        }

        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;

        view.onBegin();
        mIsLoad = true;

        AVIMConversation conversation = LeanCloudHelper.getConversation(mServiceType);

        conversation.queryMessages(mMsgId, mTimestamp, 20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                view.onFinish();
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        view.onNoHaveMsg();
                    } else {
                        mMsgId = list.get(0).getMessageId();
                        mTimestamp = list.get(0).getTimestamp();

                        if (mServiceType == LeanCloudHelper.SERVICE_TYPE_MAIL) {
                            Collections.reverse(list);
                        }
                        if (isLoadPre) {
                            view.onSyncPreMsgHistorySuccess(list);
                        } else {
                            view.onSyncMsgHistorySuccess(list);
                        }

                    }
                    mIsLoad = false;
                } else {
                    view.onSyncMsgHistoryFailed();
                    mIsLoad = false;
                }
            }
        });
    }

    @Override
    public void doSendTextMsg(String content) {
        LeanCloudHelper.sendTextMsg(mServiceType, content);
    }

    @Override
    public void sendPic(Activity activity, int type) {

        if (type == PIC_REQUEST_CODE_LOCAL) {//pic local
//            Intent intent;
//            if (Build.VERSION.SDK_INT < 19) {
//                intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//            } else {
//                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            }
//            activity.startActivityForResult(intent, PIC_REQUEST_CODE_LOCAL);

            ImagePickerActivity.show(activity, new SelectOptions
                    .Builder()
                    .setHasCam(true)
                    .setSelectCount(9)
                    .setSelectedImages(new String[]{})
                    .setCallback(images -> {
                        for (String image : images) {
                            Log.e(TAG, "doSelected: ---------->" + image);
                            LeanCloudHelper.sendImageMsg(mServiceType, image);
                        }
                    }).build());

        } else {//pic camera
            cameraFile = new File(generateImagePath(String.valueOf(AppManager.getAccountViewModel().getUserInfo().getId()), HwApp.getAppContext()), AppManager.getAccountViewModel().getUserInfo().getId()
                    + System.currentTimeMillis() + ".jpg");

            //noinspection ResultOfMethodCallIgnored
            cameraFile.getParentFile().mkdirs();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //android 7.1之后的相机处理方式
            if (Build.VERSION.SDK_INT < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                activity.startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
                Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
            }
        }

    }

    @Override
    public void sendVoice(String recordFilePath, int second) {
        LeanCloudHelper.sendVoiceMsg(mServiceType, recordFilePath);
    }

    @Override
    public void resultCodeDelegate(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PIC_REQUEST_CODE_CAMERA:// capture new image
                    if (cameraFile != null && cameraFile.exists()) {
                        this.mLocalImagePath = cameraFile.getAbsolutePath();
                        updateLocalCache();
                    }
                    break;
                case PIC_REQUEST_CODE_LOCAL:// send local image
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void release() {
        LeanCloudHelper.clearMsgNotification(mServiceType);
        LeanCloudHelper.removeOnMsgCallback(this);
        LeanCloudHelper.removeOnConversationCallback();
        this.mTimestamp = 0;
        this.mMsgId = null;
    }

    @Override
    public void onSendingMsgCallback(AVIMMessage msg) {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onSendingMsg(msg);
    }

    @Override
    public void onSendMsgSuccess(AVIMMessage msg) {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onSendMsgSuccess(msg);
    }

    @Override
    public void onRemoteReceipt(AVIMMessage msg) {

    }

    @Override
    public void onSendMsgFailed(AVIMMessage msg) {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onSendMsgFailed(msg);
    }

    @Override
    public void onReceiveMsg(AVIMMessage message) {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onReceiveMsg(message);
    }

    @Override
    public void onEstablishConversationCallback() {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onPrepareLogin();
    }

    @Override
    public void onEstablishConversationSuccessCallback() {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onLoginSuccess();
        syncMsgHistory(mServiceType);
    }

    @Override
    public void onEstablishConversationFailedCallback() {
        WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        MsgContract.View view = viewWeakReference.get();
        if (view == null) return;
        view.onLoginFailed();
    }

    private void updateLocalCache() {
        LeanCloudHelper.sendImageMsg(mServiceType, mLocalImagePath);
    }

    /**
     * send image
     *
     * @param selectedImage selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {

        // WeakReference<MsgContract.View> viewWeakReference = this.mViewWeakReference;
        //MsgContract.View view = viewWeakReference.get();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = HwApp.getAppContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            this.mLocalImagePath = picturePath;
            if (picturePath == null || picturePath.equals("null")) {
                return;
            }
            updateLocalCache();
        } else {
            File file = new File(selectedImage.getPath());
            this.mLocalImagePath = file.getAbsolutePath();
            if (!file.exists()) {
                return;
            }
            updateLocalCache();
        }
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
}
