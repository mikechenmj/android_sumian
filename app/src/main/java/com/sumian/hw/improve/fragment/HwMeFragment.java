package com.sumian.hw.improve.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.common.image.ImageLoader;
import com.sumian.hw.account.activity.UserInfoActivity;
import com.sumian.hw.account.contract.UserInfoContract;
import com.sumian.hw.base.HwBasePagerFragment;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.reminder.ReminderManager;
import com.sumian.hw.setting.activity.HwSettingActivity;
import com.sumian.hw.setting.widget.HwSettingItemView;
import com.sumian.hw.upgrade.activity.VersionNoticeActivity;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class HwMeFragment extends HwBasePagerFragment implements View.OnClickListener, UserInfoContract.View,
        HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_age_and_gender)
    TextView mTvAgeAndGender;
    @BindView(R.id.siv_customer_service)
    HwSettingItemView mSivKefu;
    @BindView(R.id.siv_upgrade)
    HwSettingItemView mSivUpgrade;


    public static HwMeFragment newInstance() {
        return new HwMeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_tab_me;
    }

    @Override
    protected void initData() {
        super.initData();
        HwLeanCloudHelper.addOnAdminMsgCallback(this);
        AppManager.getVersionModel().syncAppVersion();
        AppManager.getVersionModel().registerShowDotCallback(this);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> updateUserInfoUI(token.user));
    }

    @Override
    public void onEnterTab() {
        LogManager.appendUserOperationLog("点击进入 '我的'  界面");
    }

    @OnClick({R.id.ll_user_info_container, R.id.siv_customer_service, R.id.siv_upgrade, R.id.siv_setting})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_info_container:
                UserInfoActivity.show(getContext());
                break;
            case R.id.siv_customer_service:
                UIProvider.getInstance().clearCacheMsg();
                HwLeanCloudHelper.checkLoginEasemob(HwLeanCloudHelper::startEasemobChatRoom);
                break;
            case R.id.siv_upgrade:
                VersionNoticeActivity.show(getContext());
                break;
            case R.id.siv_setting:
                HwSettingActivity.show(getContext());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRelease() {
        HwLeanCloudHelper.removeOnAdminMsgCallback(this);
        AppManager.getVersionModel().unRegisterShowDotCallback(this);
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
            ImageLoader.loadImage(userInfo.getAvatar(), mIvAvatar, R.mipmap.ic_default_avatar, R.mipmap.ic_default_avatar);
            this.mTvAgeAndGender.setText(formatGender(userInfo.getGender()));
            this.mTvNickname.setText(userInfo.getNickname());
            String age = userInfo.getAge() == null ? null : userInfo.getAge().toString();
            if (!TextUtils.isEmpty(age)) {
                this.mTvAgeAndGender.append(String.format(Locale.getDefault(), "%s%s%s", "  丨  ", age, App.Companion.getAppContext().getString(R.string.age_hint)));
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
    }

    private String formatGender(String gender) {
        String genderText = App.Companion.getAppContext().getString(R.string.gender_secrecy_hint);
        if (TextUtils.isEmpty(gender)) {
            return genderText;
        }
        switch (gender) {
            case "male":
                genderText = App.Companion.getAppContext().getString(R.string.gender_male_hint);
                break;
            case "female":
                genderText = App.Companion.getAppContext().getString(R.string.gender_female_hint);
                break;
            case "secrecy":
                genderText = App.Companion.getAppContext().getString(R.string.gender_secrecy_hint);
                break;
            default:
                genderText = App.Companion.getAppContext().getString(R.string.user_none_default_hint);
                break;
        }
        return genderText;
    }
}
