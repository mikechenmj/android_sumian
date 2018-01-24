package com.sumian.sleepdoctor.pager.activity;

import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.media.ImagePickerActivity;
import com.sumian.common.media.SelectOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.pager.sheet.AvatarBottomSheet;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class UserProfileActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener, SettingDividerView.OnShowMoreListener, AvatarBottomSheet.OnTakePhotoCallback {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

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

    private UserProfile userProfile;

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
        userProfile = AppManager.getAccountViewModel().getToken().user;

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
    public void onShowMore(View v) {
        switch (v.getId()) {
            case R.id.dv_nickname:

                break;
            case R.id.dv_name:

                break;
            default:
                break;
        }

    }

    @Override
    public void onTakePhotoCallback() {
        onPicPictureCallback();
    }

    @Override
    public void onPicPictureCallback() {
        ImagePickerActivity.show(this, new SelectOptions
                .Builder()
                .setHasCam(true)
                .setSelectCount(1)
                .setSelectedImages(new String[]{})
                .setCallback(images -> {
                    for (String image : images) {
                        Log.e(TAG, "doSelected: ---------->" + image);
                        RequestOptions options = new RequestOptions();
                        options.error(R.mipmap.info_avatar_patient).placeholder(R.mipmap.info_avatar_patient).getOptions();
                        Glide.with(this).load(userProfile.avatar).apply(options).into(mIvAvatar);
                    }
                }).build());
    }
}
