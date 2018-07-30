package com.sumian.app.improve.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sumian.app.BuildConfig;
import com.sumian.app.R;
import com.sumian.app.account.activity.SleepReminderActivity;
import com.sumian.app.account.activity.UserInfoActivity;
import com.sumian.app.account.callback.OnSleepReminderCallback;
import com.sumian.app.account.callback.UserInfoCallback;
import com.sumian.app.account.contract.UserInfoContract;
import com.sumian.app.account.presenter.SyncUserInfoPresenter;
import com.sumian.app.account.presenter.UserInfoPresenter;
import com.sumian.app.app.App;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BasePagerFragment;
import com.sumian.app.improve.assessment.QuestionActivity;
import com.sumian.app.improve.guideline.activity.ManualActivity;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.activity.MsgActivity;
import com.sumian.app.log.LogManager;
import com.sumian.app.network.response.Reminder;
import com.sumian.app.network.response.UserInfo;
import com.sumian.app.setting.activity.SettingActivity;
import com.sumian.app.upgrade.activity.VersionNoticeActivity;
import com.sumian.app.upgrade.model.VersionModel;

import java.io.FileDescriptor;
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
public class MeFragment extends BasePagerFragment implements View.OnClickListener, UserInfoContract.View,
        LeanCloudHelper.OnShowMsgDotCallback, OnSleepReminderCallback, UserInfoCallback, VersionModel.ShowDotCallback {

    CircleImageView mIvAvatar;
    TextView mTvNickname;
    TextView mTvAgeAndGender;
    TextView mTvSleepAnswerState;
    TextView mTvSleepNoticeState;
    View mVDotMsgNotice;
    View mVDotVersionNotice;
    LinearLayout mLaySleepyAnswer;
    View mViewDividerTwo;

    private UserInfoContract.Presenter mPresenter;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_tab_me;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mIvAvatar = root.findViewById(R.id.iv_avatar);
        mTvNickname = root.findViewById(R.id.tv_nickname);
        mTvAgeAndGender = root.findViewById(R.id.tv_age_and_gender);
        mTvSleepAnswerState = root.findViewById(R.id.tv_sleep_answer_state);
        mTvSleepNoticeState = root.findViewById(R.id.tv_sleep_notice_state);
        mVDotMsgNotice = root.findViewById(R.id.v_dot_msg_notice);
        mVDotVersionNotice = root.findViewById(R.id.v_dot_version_notice);
        mLaySleepyAnswer = root.findViewById(R.id.lay_sleepy_answer);
        mViewDividerTwo = root.findViewById(R.id.view_divider_two);

        root.findViewById(R.id.iv_avatar).setOnClickListener(this);
        root.findViewById(R.id.lay_go_to_info_center).setOnClickListener(this);
        root.findViewById(R.id.lay_sleepy_answer).setOnClickListener(this);
        root.findViewById(R.id.lay_sleepy_notice).setOnClickListener(this);
        root.findViewById(R.id.lay_my_msg_notice).setOnClickListener(this);
        root.findViewById(R.id.lay_firmware_update).setOnClickListener(this);
        root.findViewById(R.id.lay_user_guide).setOnClickListener(this);
        root.findViewById(R.id.lay_setting).setOnClickListener(this);

        UserInfoPresenter.init(this);
        if (BuildConfig.IS_CLINICAL_VERSION) {
            mViewDividerTwo.setVisibility(View.GONE);
            mLaySleepyAnswer.setVisibility(View.GONE);
        } else {
            mViewDividerTwo.setVisibility(View.VISIBLE);
            mLaySleepyAnswer.setVisibility(View.VISIBLE);
        }
        AppManager.getAccountModel().addOnReminderCallback(this);
        AppManager.getAccountModel().addOnSyncUserInfoCallback(this);
        AppManager.getVersionModel().registerShowDotCallback(this);
    }

    @Override
    protected void initData() {
        super.initData();
        LeanCloudHelper.addOnAdminMsgCallback(this);
        this.mPresenter.doLoadCacheUserInfo();
        AppManager.getVersionModel().syncAppVersion();
    }

    @Override
    public void onEnterTab() {
        // Log.e(TAG, "onEnterTab: -------MeFragment------->");
        if (isResumed()) {
            this.mPresenter.doLoadCacheUserInfo();
        }
        AppManager.getOpenAnalytics().onClickEvent(getContext(), "me_tabbar_Ry");
        LogManager.appendUserOperationLog("点击进入 '我的'  界面");
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lay_sleepy_answer) {
            QuestionActivity.show(v.getContext());

        } else if (i == R.id.iv_avatar || i == R.id.lay_go_to_info_center) {
            UserInfoActivity.show(getContext());

        } else if (i == R.id.lay_sleepy_notice) {
            SleepReminderActivity.show(getContext());

        } else if (i == R.id.lay_my_msg_notice) {
            MsgActivity.show(v.getContext(), LeanCloudHelper.SERVICE_TYPE_MAIL);

        } else if (i == R.id.lay_firmware_update) {
            VersionNoticeActivity.show(getContext());

        } else if (i == R.id.lay_user_guide) {
            ManualActivity.show(getContext());

        } else if (i == R.id.lay_setting) {
            SettingActivity.show(getContext());

        } else {
        }
    }

    @Override
    protected void onRelease() {
        LeanCloudHelper.removeOnAdminMsgCallback(this);
        AppManager.getAccountModel().removeOnReminderCallback(this);
        AppManager.getVersionModel().unRegisterShowDotCallback(this);
        super.onRelease();
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSyncCacheUserInfoSuccess(UserInfo userInfo) {
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
            String age = userInfo.getAge();
            if (!TextUtils.isEmpty(age)) {
                this.mTvAgeAndGender.append(String.format(Locale.getDefault(), "%s%s%s", "  丨  ", age, App.getAppContext().getString(R.string.age_hint)));
            }
            this.mTvSleepAnswerState.setVisibility(!userInfo.isHaveAnswers() ? View.VISIBLE : View.INVISIBLE);
        });
        SyncUserInfoPresenter.init().doSyncReminder();
    }

    @Override
    public void onSyncCacheUserInfoFailed(String error) {

    }

    @Override
    public void onStartSyncUserInfo() {

    }

    @Override
    public void onSyncUserInfoSuccess(UserInfo userInfo) {
        onSyncCacheUserInfoSuccess(userInfo);
    }

    @Override
    public void onSyncUserInfoFailed(String error) {

    }

    @Override
    public void onCompletedUserInfo() {

    }

    @Override
    public void onSleepReminderChange(Reminder reminder) {
        updateReminder(reminder);
    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        // Log.e(TAG, "showDot: ------>" + isShowAppDot + "   " + (Looper.myLooper() == Looper.getMainLooper()));
        showDot(mVDotVersionNotice, isShowAppDot || isShowMonitorDot || isShowSleepyDot);
    }

    @Override
    public void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    @Override
    public void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        showDot(mVDotMsgNotice, adminMsgLen > 0);
    }

    private void showDot(View dot, boolean isShow) {
        runOnUiThread(() -> dot.setVisibility(isShow ? View.VISIBLE : View.GONE));
    }

    private void updateReminder(Reminder reminder) {
        runOnUiThread(() -> mTvSleepNoticeState.setText(reminder == null || reminder.getEnable() == 0 ? App.getAppContext().getString(R.string.sleepy_notice_state_off_hint) : reminder.getReminderFormatTime()));
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
