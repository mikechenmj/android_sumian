package com.sumian.sd.account.userProfile;

import android.Manifest;
import android.app.Activity;
import androidx.lifecycle.Observer;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.sumian.common.image.ImageLoader;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.config.SelectOptions;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.Social;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.account.sheet.ModifySelectBottomSheet;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.utils.JsonUtil;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.dialog.SumianAlertDialog;
import com.sumian.sd.widget.divider.SettingDividerView;
import com.sumian.sd.widget.sheet.PictureBottomSheet;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:用户信息
 */

@SuppressWarnings("ALL")
public class SdUserProfileActivity extends SdBaseActivity<SdUserInfoContract.Presenter> implements View.OnClickListener, TitleBar.OnBackClickListener,
        SettingDividerView.OnShowMoreListener, PictureBottomSheet.OnTakePhotoCallback, EasyPermissions.PermissionCallbacks,
        CompoundButton.OnCheckedChangeListener, SdUserInfoContract.View, UMAuthListener, Observer<Token> {

    @SuppressWarnings("unused")
    private static final String TAG = SdUserProfileActivity.class.getSimpleName();
    private final static String imagePathName = "/image/";
    private static final int PIC_REQUEST_CODE_CAMERA = 0x02;

    private TitleBar mTitleBar;
    private CircleImageView mIvAvatar;
    private SettingDividerView mDvNickname;
    private SettingDividerView mDvName;
    private SettingDividerView mDvGender;
    private SettingDividerView mDvBirthday;
    private SettingDividerView mDvArea;
    private SettingDividerView mDvHeight;
    private SettingDividerView mDvWeight;
    private SettingDividerView mDvEduLevel;
    private SettingDividerView mDvCareer;
    private SettingDividerView mDvMobile;
    private SettingDividerView mDvWechat;
    private SettingDividerView mDvMedicineHistory;
    private UserInfo mUserProfile;
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
        mTitleBar = findViewById(R.id.title_bar);
        mIvAvatar = findViewById(R.id.iv_avatar);
        findViewById(R.id.lay_avatar).setOnClickListener(this);
        mDvNickname = findViewById(R.id.dv_nickname);
        mDvName = findViewById(R.id.dv_name);
        mDvGender = findViewById(R.id.dv_gender);
        mDvBirthday = findViewById(R.id.dv_birthday);
        mDvArea = findViewById(R.id.dv_area);
        mDvArea.setOnShowMoreListener(this);
        mDvArea.getContentView().setMaxLines(1);
        mDvArea.getContentView().setMaxEms(11);
        mDvArea.getContentView().setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mDvArea.getContentView().getLayoutParams();
        layoutParams.rightMargin = 0;
        mDvArea.getContentView().setLayoutParams(layoutParams);
        mDvHeight = findViewById(R.id.dv_height);
        mDvWeight = findViewById(R.id.dv_weight);
        mDvEduLevel = findViewById(R.id.dv_edu_level);
        mDvCareer = findViewById(R.id.dv_career);
        mDvMobile = findViewById(R.id.dv_mobile);
        mDvWechat = findViewById(R.id.dv_wechat_bind);
        mDvMedicineHistory = findViewById(R.id.dv_medicine_history);
        findViewById(R.id.dv_my_target).setOnClickListener(this);

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
        mDvMedicineHistory.setOnShowMoreListener(this);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        SdUserInfoPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getUserInfo();
    }

    @Override
    public void setPresenter(SdUserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        AppManager.getAccountViewModel().getLiveDataToken().removeObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_avatar:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(PictureBottomSheet.newInstance().addOnTakePhotoCallback(this), PictureBottomSheet.class.getSimpleName())
                        .commitNowAllowingStateLoss();
                break;
            case R.id.dv_my_target:
                MyTargetAndInformationActivity.launchForResult(this, true, 0);
                break;
            default:
                break;
        }
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
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_GENDER_KEY);
                break;
            case R.id.dv_birthday:
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY);
                break;
            case R.id.dv_area:
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_AREA_KEY);
                break;
            case R.id.dv_height:
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_HEIGHT_KEY);
                break;
            case R.id.dv_weight:
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_WEIGHT_KEY);
                break;
            case R.id.dv_edu_level:
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_EDUCATION_KEY);
                break;
            case R.id.dv_medicine_history:
                commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_MEDICINE_HISTORY);
                break;
            case R.id.dv_career:
                ModifyUserInfoActivity.show(this, ImproveUserProfileContract.IMPROVE_CAREER_KEY);
                break;
            default:
                break;
        }
    }

    private void commitModifySelectBottomSheet(String modifyKey) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(ModifySelectBottomSheet.Companion.newInstance(modifyKey), ModifySelectBottomSheet.class.getSimpleName())
                .commitNowAllowingStateLoss();
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
    public void onGetUserInfoSuccess(UserInfo userProfile) {
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
        new SumianAlertDialog(this)
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
                .show();
    }

    private void updateSocialites(Social social) {
        List<Social> socials = new ArrayList<>();
        if (social != null) {
            socials.add(social);
        }
        mUserProfile.socialites = socials;
        AppManager.getAccountViewModel().updateUserInfo(mUserProfile);
        AppManager.getOpenLogin().deleteWechatTokenCache(this, null);
    }

    private void bindSocialitesFailed(String message) {
        showCenterToast(message);
        mDvWechat.setSwitchCheckedWithoutCallback(false);
    }

    private void uploadAvatar(String imageUrl) {
        mPresenter.uploadAvatar(imageUrl);
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
    private void updateUserProfileUI(UserInfo userProfile) {
        ImageLoader.loadImage(userProfile.avatar, mIvAvatar, R.mipmap.ic_info_avatar_patient);
        mDvNickname.setContent(userProfile.nickname);
        mDvName.setContent(userProfile.formatField(userProfile.name));
        mDvGender.setContent(userProfile.formatGander());
        mDvBirthday.setContent(userProfile.formatField(userProfile.birthday));
        mDvArea.setContent(userProfile.formatField(userProfile.area));
        mDvHeight.setContent(userProfile.formatHeight(userProfile.formatField(userProfile.height)));
        mDvWeight.setContent(userProfile.formatWeight(userProfile.formatField(userProfile.weight)));
        mDvEduLevel.setContent(userProfile.formatField(userProfile.education));
        mDvCareer.setContent(userProfile.formatField(userProfile.career));
        mDvMobile.setContent(userProfile.mobile);
        mDvMedicineHistory.setContent(userProfile.formatIsUsingSleepPills());
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
        if (token == null) {
            return;
        }
        updateUserProfileUI(token.user);
    }

    @Override
    public void onBegin() {
        showLoading();
    }

    @Override
    public void onFinish() {
        dismissLoading();
    }
}
