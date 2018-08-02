package com.sumian.hw.improve.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.hw.account.activity.UserInfoActivity;
import com.sumian.hw.account.contract.UserInfoContract;
import com.sumian.hw.account.presenter.UserInfoPresenter;
import com.sumian.hw.app.App;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.base.BasePagerFragment;
import com.sumian.hw.event.ReminderChangeEvent;
import com.sumian.hw.improve.assessment.QuestionActivity;
import com.sumian.hw.leancloud.LeanCloudHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.network.response.Reminder;
import com.sumian.hw.reminder.ReminderManager;
import com.sumian.hw.setting.activity.SettingActivity;
import com.sumian.hw.setting.widget.HwSettingItemView;
import com.sumian.hw.upgrade.activity.VersionNoticeActivity;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.event.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class MeFragment extends BasePagerFragment implements View.OnClickListener, UserInfoContract.View,
        LeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_age_and_gender)
    TextView mTvAgeAndGender;
    @BindView(R.id.siv_upgrade)
    HwSettingItemView mSivUpgrade;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_tab_me;
    }

    @Override
    protected void initData() {
        super.initData();
        LeanCloudHelper.addOnAdminMsgCallback(this);
        HwAppManager.getVersionModel().syncAppVersion();
        HwAppManager.getVersionModel().registerShowDotCallback(this);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, new Observer<Token>() {
            @Override
            public void onChanged(@Nullable Token token) {
                updateUserInfoUI(token.user);
            }
        });
    }

    @Override
    public void onEnterTab() {
        HwAppManager.getOpenAnalytics().onClickEvent(getContext(), "me_tabbar_Ry");
        LogManager.appendUserOperationLog("点击进入 '我的'  界面");
    }

    @OnClick({R.id.ll_user_info_container, R.id.siv_customer_service, R.id.siv_upgrade, R.id.siv_setting})
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (v.getId()) {
            case R.id.ll_user_info_container:
                UserInfoActivity.show(getContext());
                break;
            case R.id.siv_customer_service:

                break;
            case R.id.siv_upgrade:
                VersionNoticeActivity.show(getContext());
                break;
            case R.id.siv_setting:
                SettingActivity.show(getContext());
                break;
            default:
                break;
        }
//        if (i == R.id.lay_sleepy_answer) {
//            QuestionActivity.show(v.getContext());
//        } else if (i == R.id.iv_avatar || i == R.id.ll_user_info_container) {
//            UserInfoActivity.show(getContext());
//        } else if (i == R.id.lay_sleepy_notice) {
//            SleepReminderActivity.show(getContext());
//        } else if (i == R.id.lay_my_msg_notice) {
//            MsgActivity.show(v.getContext(), LeanCloudHelper.SERVICE_TYPE_MAIL);
//        } else if (i == R.id.lay_firmware_update) {
//            VersionNoticeActivity.show(getContext());
//        } else if (i == R.id.lay_user_guide) {
//            ManualActivity.show(getContext());
//        } else if (i == R.id.lay_setting) {
//            SettingActivity.show(getContext());
//        }
    }

    @Override
    protected void onRelease() {
        LeanCloudHelper.removeOnAdminMsgCallback(this);
        HwAppManager.getVersionModel().unRegisterShowDotCallback(this);
        super.onRelease();
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSyncCacheUserInfoSuccess(UserInfo userInfo) {
        updateUserInfoUI(userInfo);
    }

    private void updateUserInfoUI(UserInfo userInfo) {
        runOnUiThread(() -> {
            String avatar = userInfo.getAvatar();
            if (!TextUtils.isEmpty(avatar)) {
//                Glide.with(getActivity())
//                        .load(avatar)
//                        .asBitmap()
//                        .error(R.mipmap.ic_default_avatar)
//                        .placeholder(R.mipmap.ic_default_avatar)
//                        .into(mIvAvatar);
            }
            this.mTvAgeAndGender.setText(formatGender(userInfo.getGender()));
            this.mTvNickname.setText(userInfo.getNickname());
            String age = userInfo.getAge() == null ? null : userInfo.getAge().toString();
            if (!TextUtils.isEmpty(age)) {
                this.mTvAgeAndGender.append(String.format(Locale.getDefault(), "%s%s%s", "  丨  ", age, App.getAppContext().getString(R.string.age_hint)));
            }
        });
        ReminderManager.getReminder();
    }

    @Override
    public void onSyncCacheUserInfoFailed(String error) {

    }

    @Override
    public void onStartSyncUserInfo() {

    }

    @Override
    public void onCompletedUserInfo() {

    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        mSivUpgrade.showDot(isShowAppDot || isShowMonitorDot || isShowSleepyDot);
    }

    @Override
    public void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    @Override
    public void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
//        showDot(mVDotMsgNotice, adminMsgLen > 0);
    }

    private void showDot(View dot, boolean isShow) {
        runOnUiThread(() -> dot.setVisibility(isShow ? View.VISIBLE : View.GONE));
    }

    private String formatGender(String gender) {
        String genderText = App.getAppContext().getString(R.string.gender_secrecy_hint);
        if (TextUtils.isEmpty(gender)) {
            return genderText;
        }
        switch (gender) {
            case "male":
                genderText = App.getAppContext().getString(R.string.gender_male_hint);
                break;
            case "female":
                genderText = App.getAppContext().getString(R.string.gender_female_hint);
                break;
            case "secrecy":
                genderText = App.getAppContext().getString(R.string.gender_secrecy_hint);
                break;
            default:
                genderText = App.getAppContext().getString(R.string.user_none_default_hint);
                break;
        }
        return genderText;
    }
}
