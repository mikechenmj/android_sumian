package com.sumian.sleepdoctor.pager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.media.Callback;
import com.sumian.common.media.ImagePickerActivity;
import com.sumian.common.media.SelectOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.oss.bean.OssResponse;
import com.sumian.sleepdoctor.oss.engine.OssEngine;
import com.sumian.sleepdoctor.pager.sheet.AvatarBottomSheet;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.sumian.sleepdoctor.pager.activity.ModifyNicknameActivity.EXTRA_MODIFY_NAME;
import static com.sumian.sleepdoctor.pager.activity.ModifyNicknameActivity.MODIFY_NAME;
import static com.sumian.sleepdoctor.pager.activity.ModifyNicknameActivity.MODIFY_NICKNAME;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class UserProfileActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
        SettingDividerView.OnShowMoreListener, AvatarBottomSheet.OnTakePhotoCallback, EasyPermissions.PermissionCallbacks {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private final static String imagePathName = "/image/";

    private static final int PIC_REQUEST_CODE_CAMERA = 0x02;

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;

    @BindView(R.id.dv_nickname)
    SettingDividerView mDvNickname;
    @BindView(R.id.dv_name)
    SettingDividerView mDvName;

    @BindView(R.id.dv_mobile)
    SettingDividerView mDvMobile;

    private UserProfile mUserProfile;
    private File cameraFile;
    private File storageDir = null;
    private String mLocalImagePath;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_user_profile;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
        mDvNickname.setOnShowMoreListener(this);
        mDvName.setOnShowMoreListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mUserProfile = AppManager.getAccountViewModel().getToken().user;
        updateUserProfile(mUserProfile);

        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null)
                updateUserProfile(token.user);
        });
    }

    private void updateUserProfile(UserProfile userProfile) {
        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.info_avatar_patient).placeholder(R.mipmap.info_avatar_patient).getOptions();
        Glide.with(this).load(userProfile.avatar).apply(options).into(mIvAvatar);

        mDvNickname.setContent(userProfile.nickname);
        mDvName.setContent(userProfile.name);

        mDvMobile.setContent(userProfile.mobile);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.lay_avatar)
    @Override
    public void onClick(View v) {

        getSupportFragmentManager()
                .beginTransaction()
                .add(AvatarBottomSheet.newInstance().addOnTakePhotoCallback(this), AvatarBottomSheet.class.getSimpleName())
                .commitNowAllowingStateLoss();

    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PIC_REQUEST_CODE_CAMERA:// capture new image
                    if (cameraFile != null && cameraFile.exists()) {
                        this.mLocalImagePath = cameraFile.getAbsolutePath();
                        RequestOptions options = new RequestOptions();
                        options.error(R.mipmap.info_avatar_patient).placeholder(R.mipmap.info_avatar_patient).getOptions();
                        Glide.with(getApplicationContext()).load(mLocalImagePath).apply(options).into(mIvAvatar);
                        upload();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onShowMore(View v) {
        Bundle extras;
        switch (v.getId()) {
            case R.id.dv_nickname:
                extras = new Bundle();
                extras.putInt(EXTRA_MODIFY_NAME, MODIFY_NICKNAME);
                ModifyNicknameActivity.show(this, ModifyNicknameActivity.class, extras);
                break;
            case R.id.dv_name:
                extras = new Bundle();
                extras.putInt(EXTRA_MODIFY_NAME, MODIFY_NAME);
                ModifyNicknameActivity.show(this, ModifyNicknameActivity.class, extras);
                break;
            default:
                break;
        }

    }

    @AfterPermissionGranted(PIC_REQUEST_CODE_CAMERA)
    @Override
    public void onTakePhotoCallback() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {

            cameraFile = new File(generateImagePath(String.valueOf(AppManager.getAccountViewModel().getToken().user.id), App.Companion.getAppContext()), AppManager.getAccountViewModel().getToken().user.id + System.currentTimeMillis() + ".jpg");
            //noinspection ResultOfMethodCallIgnored
            cameraFile.getParentFile().mkdirs();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //android 7.1之后的相机处理方式
            if (Build.VERSION.SDK_INT < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA);
            }

        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.str_request_camera_message), PIC_REQUEST_CODE_CAMERA, perms);
        }
    }

    @Override
    public void onPicPictureCallback() {
        ImagePickerActivity.show(this, new SelectOptions
                .Builder()
                .setHasCam(false)
                .setSelectCount(1)
                .setSelectedImages(new String[]{})
                .setCallback(new Callback() {
                    @Override
                    public void doSelected(String[] images) {
                        super.doSelected(images);
                        for (String image : images) {
                            mLocalImagePath = image;
                            RequestOptions options = new RequestOptions();
                            options.error(R.mipmap.info_avatar_patient).placeholder(R.mipmap.info_avatar_patient).getOptions();
                            Glide.with(getApplicationContext()).load(image).apply(options).into(mIvAvatar);
                            upload();
                        }
                    }
                }).build());
    }

    private void upload() {
        mUserProfile.avatar = mLocalImagePath;
        AppManager.getAccountViewModel().updateUserProfile(mUserProfile);
        AppManager.getHttpService().uploadAvatar().enqueue(new BaseResponseCallback<OssResponse>() {
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
