package com.sumian.sleepdoctor.account.userProfile.activity;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Social;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.account.userProfile.contract.ImproveUserProfileContract;
import com.sumian.sleepdoctor.account.userProfile.contract.UserInfoContract;
import com.sumian.sleepdoctor.account.userProfile.presenter.UserInfoPresenter;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.improve.widget.sheet.PictureBottomSheet;
import com.sumian.sleepdoctor.utils.JsonUtil;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:用户信息
 */

public class UserProfileActivity extends BaseActivity<UserInfoContract.Presenter> implements View.OnClickListener, TitleBar.OnBackClickListener,
        SettingDividerView.OnShowMoreListener, PictureBottomSheet.OnTakePhotoCallback, EasyPermissions.PermissionCallbacks,
        CompoundButton.OnCheckedChangeListener, UserInfoContract.View, UMAuthListener, Observer<Token> {

    @SuppressWarnings("unused")
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
    @BindView(R.id.dv_gender)
    SettingDividerView mDvGender;
    @BindView(R.id.dv_birthday)
    SettingDividerView mDvBirthday;
    @BindView(R.id.dv_height)
    SettingDividerView mDvHeight;
    @BindView(R.id.dv_weight)
    SettingDividerView mDvWeight;
    @BindView(R.id.dv_edu_level)
    SettingDividerView mDvEduLevel;
    @BindView(R.id.dv_career)
    SettingDividerView mDvCareer;

    @BindView(R.id.dv_mobile)
    SettingDividerView mDvMobile;
    @BindView(R.id.dv_wechat_bind)
    SettingDividerView mDvWechat;

    private UserProfile mUserProfile;

    private File cameraFile;
    private File storageDir = null;
    private String mLocalImagePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);

        mDvNickname.setOnShowMoreListener(this);

        mDvName.setOnShowMoreListener(this);
        mDvGender.setOnShowMoreListener(this);
        mDvBirthday.setOnShowMoreListener(this);
        mDvHeight.setOnShowMoreListener(this);
        mDvWeight.setOnShowMoreListener(this);
        mDvEduLevel.setOnShowMoreListener(this);
        mDvCareer.setOnShowMoreListener(this);

        mDvWechat.setOnCheckedChangeListener(this);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        UserInfoPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getUserInfo();
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        AppManager.getAccountViewModel().getLiveDataToken().removeObserver(this);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.lay_avatar)
    @Override
    public void onClick(View v) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(PictureBottomSheet.newInstance().addOnTakePhotoCallback(this), PictureBottomSheet.class.getSimpleName())
                .commitNow();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

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
                        uploadAvatar(mLocalImagePath);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onShowMore(View v) {
        switch (v.getId()) {
            case R.id.dv_nickname:
                ModifyUserInfoActivity.show(this, ImproveUserProfileContract.IMPROVE_NICKNAME_KEY);
                break;
            case R.id.dv_name:
                ModifyUserInfoActivity.show(this, ImproveUserProfileContract.IMPROVE_NAME_KEY);
                break;
            case R.id.dv_gender:

                break;
            case R.id.dv_birthday:
                break;
            case R.id.dv_height:
                break;
            case R.id.dv_weight:
                break;
            case R.id.dv_edu_level:
                break;
            case R.id.dv_career:
                ModifyUserInfoActivity.show(this, ImproveUserProfileContract.IMPROVE_CAREER_KEY);
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
        SelectImageActivity.show(this, new SelectOptions
                .Builder()
                .setHasCam(false)
                .setSelectCount(1)
                .setSelectedImages(new String[]{})
                .setCallback(images -> {
                    for (String image : images) {
                        uploadAvatar(mLocalImagePath = image);
                    }
                }).build());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        showDialog(isChecked);
    }

    @Override
    public void onGetUserInfoSuccess(UserProfile userProfile) {
        this.mUserProfile = userProfile;
        updateUserProfileUI(userProfile);
    }

    @Override
    public void onGetUserInfoFailed(String error) {
        showCenterToast(error);
    }

    @Override
    public void onUnBindWechatSuccess() {
        showCenterToast(R.string.unbind_success);
        updateSocialites(null);
    }

    @Override
    public void onUnBindWechatFailed(String error) {
        showCenterToast(error);
    }

    @Override
    public void onBindSocialSuccess(Social social) {
        updateSocialites(social);
    }

    @Override
    public void onBindSocialFailed(String error) {
        bindSocialitesFailed(error);
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        map.put("nickname", map.get("name"));
        String userInfoJson = JsonUtil.toJson(map);
        mPresenter.bindSocial(Social.SOCIAL_TYPE_WECHAT, userInfoJson);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        bindSocialitesFailed(throwable.getMessage());
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        bindSocialitesFailed(getString(R.string.bind_canceled));
    }

    private void showDialog(boolean isChecked) {
        int title = isChecked ? R.string.bind_wechat_title : R.string.unbind_wechat_title;
        int message = isChecked ? R.string.bind_wechat_message : R.string.unbind_wechat_message;
        int leftBtn = R.string.cancel;
        int rightBtn = isChecked ? R.string.bind : R.string.unbind;
        SumianAlertDialog.create()
                .setTitle(title)
                .setMessage(message)
                .whitenLeft()
                .setLeftBtn(leftBtn, v -> mDvWechat.setSwitchCheckedWithoutCallback(!isChecked))
                .setRightBtn(rightBtn, v -> {
                    if (isChecked) {
                        mPresenter.bindWechat(this, this);
                    } else {
                        List<Social> socialites = mUserProfile.socialites;
                        if (socialites == null || socialites.size() == 0) {
                            return;
                        }
                        Social social = socialites.get(0);
                        mPresenter.unBindWechat(social.id);
                    }
                })
                .show(getSupportFragmentManager());
    }

    private void updateSocialites(Social social) {
        List<Social> socials = new ArrayList<>();
        if (social != null) {
            socials.add(social);
        }
        mUserProfile.socialites = socials;
        AppManager.getAccountViewModel().updateUserProfile(mUserProfile);
        AppManager.getOpenLogin().deleteWeiXinOauth(this);
    }

    private void bindSocialitesFailed(String message) {
        showCenterToast(message);
        mDvWechat.setSwitchCheckedWithoutCallback(false);
    }

    private void uploadAvatar(String imageUrl) {
        mUserProfile.avatar = imageUrl;
        AppManager.getAccountViewModel().updateUserProfile(mUserProfile);
        mPresenter.uploadAvatar(mUserProfile.avatar);
    }

    private void updateDvWechatUI(List<Social> socialites) {
        boolean hasSocial = socialites != null && socialites.size() > 0;
        mDvWechat.setSwitchCheckedWithoutCallback(hasSocial);
        if (hasSocial) {
            String wechatNickname = socialites.get(0).nickname;
            mDvWechat.setContent(wechatNickname);
        } else {
            mDvWechat.setContent(getResources().getString(R.string.not_bind_yet));
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void updateUserProfileUI(UserProfile userProfile) {
        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.ic_info_avatar_patient).placeholder(R.mipmap.ic_info_avatar_patient).getOptions();
        Glide.with(this).load(userProfile.avatar).apply(options).into(mIvAvatar);
        mDvNickname.setContent(userProfile.nickname);

        mDvName.setContent(userProfile.name);
        mDvGender.setContent(userProfile.formatGander());
        mDvBirthday.setContent(userProfile.formatField(userProfile.birthday));
        mDvHeight.setContent(userProfile.formatField(userProfile.height));
        mDvWeight.setContent(userProfile.formatField(userProfile.weight));
        mDvEduLevel.setContent(userProfile.formatField(userProfile.education));
        mDvCareer.setContent(userProfile.career);

        mDvMobile.setContent(userProfile.mobile);
        updateDvWechatUI(userProfile.socialites);
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
    public void onChanged(@Nullable Token token) {
        if (token == null) return;
        updateUserProfileUI(token.user);
    }
}
