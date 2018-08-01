package com.sumian.sleepdoctor.tab;

import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.utils.ImageLoader;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.account.userProfile.activity.UserProfileActivity;
import com.sumian.sleepdoctor.advisory.activity.AdvisoryListActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.h5.SleepFileWebActivity;
import com.sumian.sleepdoctor.notification.NotificationListActivity;
import com.sumian.sleepdoctor.notification.NotificationViewModel;
import com.sumian.sleepdoctor.onlinereport.OnlineReportListActivity;
import com.sumian.sleepdoctor.scale.ScaleListActivity;
import com.sumian.sleepdoctor.setting.SettingActivity;

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
public class MeFragment extends BaseFragment implements View.OnClickListener {

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
        UserInfo userProfile = AppManager.getAccountViewModel().getToken().user;
        updateUserProfile(userProfile);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null) {
                updateUserProfile(token.user);
            }
        });
        ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(NotificationViewModel.class)
                .getUnreadCount()
                .observe(this, unreadCount -> mIvNotification.setActivated(unreadCount != null && unreadCount > 0));
    }

    @Override
    @OnClick({
            R.id.iv_avatar,
            R.id.dv_user_info_center,
            R.id.tv_nickname,
            R.id.dv_my_evaluation,
            R.id.dv_my_consulting,
            R.id.dv_setting,
            R.id.dv_electric_report,
            R.id.iv_notification,
            R.id.dv_my_sleep_file,
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
                UserProfileActivity.show(getContext(), UserProfileActivity.class);
                break;
            case R.id.dv_user_info_center:
                UserProfileActivity.show(getContext(), UserProfileActivity.class);
                break;
            case R.id.tv_nickname:
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
            case R.id.dv_my_sleep_file:
                // String title = getString(R.string.me_my_sleep_file);
                // String urlContentPart = H5Uri.MY_MEDICAL_RECORD;
                SleepFileWebActivity.show(getContext(), SleepFileWebActivity.class);
                break;
            default:
                break;
        }
    }

    private void updateUserProfile(UserInfo userProfile) {
        ImageLoader.loadImage(this, mIvAvatar, userProfile.avatar, R.mipmap.ic_info_avatar_patient);
        String nickname = userProfile.nickname;
        mTvNickname.setText(TextUtils.isEmpty(nickname) ? userProfile.mobile : nickname);
    }
}
