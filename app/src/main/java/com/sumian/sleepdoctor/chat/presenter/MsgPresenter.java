package com.sumian.sleepdoctor.chat.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sumian.common.media.Callback;
import com.sumian.common.media.ImagePickerActivity;
import com.sumian.common.media.SelectOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.chat.contract.MsgContract;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2017/10/17.
 * desc:
 */

public class MsgPresenter implements MsgContract.Presenter, EasyPermissions.PermissionCallbacks {

    private static final String TAG = MsgPresenter.class.getSimpleName();

    private final static String imagePathName = "/image/";

    public static final int PIC_REQUEST_CODE_LOCAL = 0x01;
    public static final int PIC_REQUEST_CODE_CAMERA = 0x02;

    private WeakReference<MsgContract.View> mViewWeakReference;

    private long mTimestamp;
    private String mMsgId;

    private boolean mIsLoad;

    private File cameraFile;
    private File storageDir = null;
    private String mLocalImagePath;

    private MsgPresenter(MsgContract.View view) {
        view.bindPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
        // LeanCloudHelper.addOnMsgCallback(this);
        //LeanCloudHelper.addOnConversationCallback(this);
    }

    public static void init(MsgContract.View view) {
        new MsgPresenter(view);
    }

    @Override
    public void joinChatRoom(String conversationId) {
        AppManager.getChatEngine().joinChatGroup(conversationId);
    }

    @Override
    public void getGroupDetail(int groupId) {

        AppManager
                .getHttpService()
                .getGroupsDetail(groupId, "users,packages")
                .enqueue(new BaseResponseCallback<GroupDetail<UserProfile, UserProfile>>() {

                    @Override
                    protected void onSuccess(GroupDetail<UserProfile, UserProfile> response) {

                    }

                    @Override
                    protected void onFailure(String error) {

                    }
                });

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

        // AVIMConversation conversation = LeanCloudHelper.getConversation(mServiceType);

//        conversation.queryMessages(mMsgId, mTimestamp, 20, new AVIMMessagesQueryCallback() {
//            @Override
//            public void done(List<AVIMMessage> list, AVIMException e) {
//                view.onFinish();
//                if (e == null) {
//                    if (list == null || list.isEmpty()) {
//                        view.onNoHaveMsg();
//                    } else {
//                        mMsgId = list.get(0).getMessageId();
//                        mTimestamp = list.get(0).getTimestamp();
//
//                        if (mServiceType == LeanCloudHelper.SERVICE_TYPE_MAIL) {
//                            Collections.reverse(list);
//                        }
//                        if (isLoadPre) {
//                            view.onSyncPreMsgHistorySuccess(list);
//                        } else {
//                            view.onSyncMsgHistorySuccess(list);
//                        }
//
//                    }
//                    mIsLoad = false;
//                } else {
//                    view.onSyncMsgHistoryFailed();
//                    mIsLoad = false;
//                }
//            }
//        });
    }

    @Override
    public void doSendTextMsg(String content) {
        //  LeanCloudHelper.sendTextMsg(mServiceType, content);
    }

    @AfterPermissionGranted(CAMERA_PERM)
    @Override
    public void sendPic(Activity activity, int type) {
        if (type == PIC_REQUEST_CODE_LOCAL) {//pic local
            ImagePickerActivity.show(activity, new SelectOptions
                    .Builder()
                    .setHasCam(true)
                    .setSelectCount(9)
                    .setSelectedImages(new String[]{})
                    .setCallback(new Callback() {

                        @Override
                        public void doSelected(String[] images) {
                            super.doSelected(images);
                            for (String image : images) {
                                Log.e(TAG, "doSelected: ---------->" + image);
                            }

                        }
                    }).build());

        } else {//pic camera

            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
            if (EasyPermissions.hasPermissions(activity, perms)) {

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

            } else {
                // Request one permission
                EasyPermissions.requestPermissions(activity, activity.getResources().getString(R.string.str_request_camera_message), CAMERA_PERM, perms);
            }
        }

    }

    @AfterPermissionGranted(RECORD_PERM)
    @Override
    public void sendVoice(Activity activity, String recordFilePath, int second) {
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
        if (EasyPermissions.hasPermissions(activity, perms)) {
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(activity, activity.getResources().getString(R.string.str_request_record_message), RECORD_PERM, perms);
        }
    }

    @Override
    public void syncMsgHistory() {

    }

    @Override
    public void resultCodeDelegate(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PIC_REQUEST_CODE_CAMERA:// capture new image
                    if (cameraFile != null && cameraFile.exists()) {
                        this.mLocalImagePath = cameraFile.getAbsolutePath();
                        // updateLocalCache();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void release() {
        // LeanCloudHelper.clearMsgNotification(mServiceType);
        //  LeanCloudHelper.removeOnMsgCallback(this);
        // LeanCloudHelper.removeOnConversationCallback();
        this.mTimestamp = 0;
        this.mMsgId = null;
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
        Cursor cursor = App.Companion.getAppContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            this.mLocalImagePath = picturePath;
            if (picturePath == null || picturePath.equals("null")) {
                return;
            }
            //updateLocalCache();
        } else {
            File file = new File(selectedImage.getPath());
            this.mLocalImagePath = file.getAbsolutePath();
            if (!file.exists()) {
                return;
            }
            // updateLocalCache();
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //  if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

        //}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
