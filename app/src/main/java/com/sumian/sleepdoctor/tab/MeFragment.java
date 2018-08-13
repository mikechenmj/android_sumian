package com.sumian.sleepdoctor.tab;

import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.image.ImageLoader;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.account.userProfile.activity.UserProfileActivity;
import com.sumian.sleepdoctor.advisory.activity.AdvisoryListActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.SdBaseFragment;
import com.sumian.sleepdoctor.h5.SleepFileWebActivity;
import com.sumian.sleepdoctor.notification.NotificationListActivity;
import com.sumian.sleepdoctor.notification.NotificationViewModel;
import com.sumian.sleepdoctor.onlinereport.OnlineReportListActivity;
import com.sumian.sleepdoctor.scale.ScaleListActivity;
import com.sumian.sleepdoctor.service.tel.activity.TelServiceActivity;
import com.sumian.sleepdoctor.setting.SettingActivity;
import com.sumian.sleepdoctor.widget.tips.PatientRecordTips;
import com.sumian.sleepdoctor.widget.tips.PatientServiceTips;

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
public class MeFragment extends SdBaseFragment implements View.OnClickListener, PatientServiceTips.OnServiceTipsCallback,
        PatientRecordTips.OnRecordTipsCallback {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.iv_notification)
    ImageView mIvNotification;

    @BindView(R.id.tips_service)
    PatientServiceTips mTipsService;
    @BindView(R.id.tips_record)
    PatientRecordTips mTipsRecord;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        this.mTipsService.setOnServiceTipsCallback(this);
        this.mTipsRecord.setOnRecordTipsCallback(this);
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
    @OnClick({R.id.iv_avatar, R.id.tv_nickname, R.id.dv_setting, R.id.iv_notification})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_avatar:
            case R.id.tv_nickname:
                UserProfileActivity.show(getContext(), UserProfileActivity.class);
                break;
            case R.id.dv_setting:
                SettingActivity.show(getContext(), SettingActivity.class);
                break;
            case R.id.iv_notification:
                NotificationListActivity.launch(getActivity());
                break;
            default:
                break;
        }
    }

    private void updateUserProfile(UserInfo userProfile) {
        ImageLoader.loadImage(userProfile.avatar, mIvAvatar, R.mipmap.ic_info_avatar_patient);
        String nickname = userProfile.nickname;
        mTvNickname.setText(TextUtils.isEmpty(nickname) ? userProfile.mobile : nickname);
    }

    @Override
    public void showGraphicService() {
        AdvisoryListActivity.show(getActivity(), AdvisoryListActivity.class);
    }

    @Override
    public void showTelService() {
        TelServiceActivity.show();
    }

    @Override
    public void showSleepRecord() {
        SleepFileWebActivity.show(getContext(), SleepFileWebActivity.class);
    }

    @Override
    public void showEvaluation() {
        ScaleListActivity.launch(getContext(), ScaleListActivity.TYPE_FILLED);
    }

    @Override
    public void showOnlineReport() {
        OnlineReportListActivity.launchForShowAll(this);
    }
}
