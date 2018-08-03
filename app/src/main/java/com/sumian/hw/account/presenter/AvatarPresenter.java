package com.sumian.hw.account.presenter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.sumian.hw.account.contract.AvatarContract;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.hw.oss.engine.OssEngine;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.HwAppManager;

import java.io.File;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/3.
 * desc:
 */

public class AvatarPresenter implements AvatarContract.Presenter {

    private static final String TAG = AvatarPresenter.class.getSimpleName();

    private final static String imagePathName = "/image/";
    public static final int PIC_REQUEST_CODE_LOCAL = 0x01;
    public static final int PIC_REQUEST_CODE_CAMERA = 0x02;

    private File cameraFile;
    private File storageDir = null;
    private String mLocalImagePath;

    private AvatarContract.View mView;

    private AvatarPresenter(AvatarContract.View view) {
        view.setPresenter(this);
        this.mView = view;
    }

    public static void init(AvatarContract.View view) {
        new AvatarPresenter(view);
    }


    @Override
    public void release() {

    }

    @Override
    public void uploadOss() {
        upload();
    }

    private void upload() {
        Call<OssResponse> call = HwAppManager.getHwNetEngine().getHttpService().uploadAvatar();
        call.enqueue(new BaseResponseCallback<OssResponse>() {
            @Override
            protected void onSuccess(OssResponse response) {
                new OssEngine().uploadFile(response, mLocalImagePath);
            }

            @Override
            protected void onFailure(String error) {
                upload();
            }
        });
    }

    @Override
    public void sendPic(Activity activity, int type) {

        if (type == PIC_REQUEST_CODE_LOCAL) {//pic local
            Intent intent;
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
            } else {
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
            activity.startActivityForResult(intent, PIC_REQUEST_CODE_LOCAL);

        } else {//pic camera
            cameraFile = new File(generateImagePath(String.valueOf(AppManager.getAccountViewModel().getUserInfo().getId()), App.Companion.getAppContext()), AppManager.getAccountViewModel().getUserInfo().getId()
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
    public void resultCodeDelegate(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PIC_REQUEST_CODE_CAMERA:// capture new image
                    if (cameraFile != null && cameraFile.exists()) {
                        this.mLocalImagePath = cameraFile.getAbsolutePath();
                        updateLocalCache();
                        uploadOss();
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

    private void updateLocalCache() {
        if (mView != null) {
            mView.loadLocalImageSuccess(mLocalImagePath);
        }

        UserInfo userInfo = AppManager.getAccountViewModel().getUserInfo();
        userInfo.setAvatar(mLocalImagePath);
        AppManager.getAccountViewModel().updateUserInfo(userInfo);
    }

    /**
     * send image
     *
     * @param selectedImage selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = App.Companion.getAppContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            this.mLocalImagePath = picturePath;
            if (picturePath == null || picturePath.equals("null")) {
                mView.imageIsExit();
                return;
            }
            updateLocalCache();
            uploadOss();
        } else {
            File file = new File(selectedImage.getPath());
            this.mLocalImagePath = file.getAbsolutePath();
            if (!file.exists()) {
                mView.imageIsExit();
                return;
            }
            updateLocalCache();
            uploadOss();
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
