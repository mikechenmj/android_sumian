package com.sumian.sleepdoctor.tab.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.account.userProfile.UserProfileActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.delegate.HomeDelegate;
import com.sumian.sleepdoctor.base.ActivityLauncher;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.h5.SimpleWebActivity;
import com.sumian.sleepdoctor.improve.advisory.activity.AdvisoryListActivity;
import com.sumian.sleepdoctor.notification.NotificationListActivity;
import com.sumian.sleepdoctor.notification.NotificationViewModel;
import com.sumian.sleepdoctor.onlinereport.OnlineReportListActivity;
import com.sumian.sleepdoctor.pager.activity.SettingActivity;
import com.sumian.sleepdoctor.scale.ScaleListActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MeFragment extends BaseFragment implements HomeDelegate, View.OnClickListener, ActivityLauncher {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.iv_notification)
    ImageView mIvNotification;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_me;
    }

    @Override
    protected void initData() {
        super.initData();
        UserProfile userProfile = AppManager.getAccountViewModel().getToken().user;
        updateUserProfile(userProfile);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null)
                updateUserProfile(token.user);
        });
        ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(NotificationViewModel.class)
                .getUnreadCount()
                .observe(this, unreadCount -> mIvNotification.setActivated(unreadCount != null && unreadCount > 0));
    }

    @OnClick({
            R.id.dv_user_info_center,
            R.id.dv_my_evaluation,
            R.id.dv_my_consulting,
            R.id.dv_setting,
            R.id.dv_electric_report,
            R.id.iv_notification,
            R.id.dv_my_medical_record,
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dv_user_info_center:
                UserProfileActivity.show(getContext(), UserProfileActivity.class);
                break;
            case R.id.dv_my_evaluation:
                ScaleListActivity.launch(getContext(), ScaleListActivity.TYPE_FILLED);
                break;
            case R.id.dv_my_consulting:
                AdvisoryListActivity.show(getActivity(), AdvisoryListActivity.class);
                break;
            case R.id.dv_setting:
                SettingActivity.show(getContext(), SettingActivity.class);
                break;
            case R.id.dv_electric_report:
                OnlineReportListActivity.launchForShowAll(this);
                break;
            case R.id.iv_notification:
                NotificationListActivity.launch(getActivity());
                break;
            case R.id.dv_my_medical_record:
                String title = getString(R.string.me_my_medical_record);
                String urlContentPart = H5Uri.MY_MEDICAL_RECORD;
                SimpleWebActivity.launch(getContext(), title, urlContentPart);
                break;
        }
    }

    private void updateUserProfile(UserProfile userProfile) {
        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.ic_info_avatar_patient).placeholder(R.mipmap.ic_info_avatar_patient).getOptions();
        Glide.with(this).load(userProfile.avatar).apply(options).into(mIvAvatar);

        String nickname = userProfile.nickname;
        mTvNickname.setText(TextUtils.isEmpty(nickname) ? userProfile.mobile : nickname);
    }
}
