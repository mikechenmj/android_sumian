package com.sumian.sleepdoctor.tab.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.delegate.HomeDelegate;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.pager.activity.SettingActivity;
import com.sumian.sleepdoctor.pager.activity.UserProfileActivity;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class MeFragment extends BaseFragment<UserProfile> implements HomeDelegate, View.OnClickListener {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        setStatusBarTranslucent();
    }

    @Override
    protected void initData() {
        super.initData();
        UserProfile userProfile = AppManager.getAccountViewModel().getToken().user;

        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.info_avatar_patient).placeholder(R.mipmap.info_avatar_patient).getOptions();
        Glide.with(this).load(userProfile.avatar).apply(options).into(mIvAvatar);

        String nickname = userProfile.nickname;
        mTvNickname.setText(TextUtils.isEmpty(nickname) ? userProfile.mobile : nickname);
    }

    @OnClick({R.id.dv_user_info_center, R.id.dv_setting})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dv_user_info_center:
                UserProfileActivity.show(getContext(), UserProfileActivity.class);
                break;
            case R.id.dv_setting:
                SettingActivity.show(getContext(), SettingActivity.class);
                break;
        }
    }
}
