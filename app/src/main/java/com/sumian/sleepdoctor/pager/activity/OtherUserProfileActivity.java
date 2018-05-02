package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/24.
 * desc:
 */

public class OtherUserProfileActivity extends BaseActivity implements TitleBar.OnBackListener {

    private static final String TAG = OtherUserProfileActivity.class.getSimpleName();

    public static final String ARGS_USER_PROFILE = "args_user_profile";

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

    @BindView(R.id.iv_avatar_more)
    ImageView mIvAvatarMore;

    private UserProfile mUserProfile;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mUserProfile = bundle.getParcelable(ARGS_USER_PROFILE);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_user_profile;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
        mIvAvatarMore.setVisibility(View.INVISIBLE);
        mDvNickname.hideMoreIcon();
        mDvName.hideMoreIcon();
    }

    @Override
    protected void initData() {
        super.initData();
        updateUserProfile(mUserProfile);
    }

    private void updateUserProfile(UserProfile userProfile) {
        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.ic_info_avatar_patient).placeholder(R.mipmap.ic_info_avatar_patient).getOptions();
        Glide.with(this).load(userProfile.avatar).apply(options).into(mIvAvatar);

        mDvNickname.setContent(userProfile.nickname);
        mDvName.setContent(userProfile.name);

        mDvMobile.setContent(userProfile.mobile);
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
