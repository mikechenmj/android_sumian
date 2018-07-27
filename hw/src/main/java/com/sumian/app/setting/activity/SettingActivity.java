package com.sumian.app.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.improve.feedback.FeedbackActivity;
import com.sumian.app.improve.qrcode.activity.QrCodeActivity;
import com.sumian.app.network.response.UserInfo;
import com.sumian.app.network.response.UserSetting;
import com.sumian.app.setting.contract.SettingContract;
import com.sumian.app.setting.presenter.SettingPresenter;
import com.sumian.app.setting.presenter.SocialPresenter;
import com.sumian.app.setting.sheet.LogoutBottomSheet;
import com.sumian.app.setting.sheet.SocialBottomSheet;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.ToggleButton;
import com.sumian.app.widget.refresh.ActionLoadingDialog;
import com.sumian.blue.model.BluePeripheral;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener, SettingContract.View, UMAuthListener, SocialBottomSheet.UnbindSocialCallback {

    //private static final String TAG = SettingActivity.class.getSimpleName();

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.tb_reminder)
    ToggleButton mTbReminder;

    @BindView(R.id.tv_wechat_nickname)
    TextView mTvWechatNickname;
    @BindView(R.id.bt_bind_wechat)
    ToggleButton mBtBindWechat;

    private int mCount;

    private ActionLoadingDialog mActionLoadingDialog;

    private SettingContract.Presenter mPresenter;

    private boolean mIsInit;

    public static void show(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        this.mTitleBar.addOnSpannerListener(v -> {
            mCount++;
            BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral != null && bluePeripheral.isConnected() && mCount >= 5) {
                DeviceLogActivity.show(v.getContext());
                mCount = 0;
            }
        });
        this.mTitleBar.addOnBackListener(this);
        this.mBtBindWechat.setOnToggleChanged(on -> {
            if (!on) {
                SocialBottomSheet socialBottomSheet = SocialBottomSheet.newInstance(SocialPresenter.SOCIAL_WECHAT).setUnbindSocialCallback(this);
                getSupportFragmentManager()
                    .beginTransaction()
                    .add(socialBottomSheet, SocialBottomSheet.class.getSimpleName())
                    .commit();
            } else {
                mPresenter.doLoginOpen(SHARE_MEDIA.WEIXIN, this, this);
            }
        });

        mTbReminder.setTag(true);
        mTbReminder.setOnToggleChanged(on -> mPresenter.updateSleepDiary(on ? 0x01 : 0x00));
        SettingPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mIsInit = true;
        mPresenter.syncSleepDiary();
        UserInfo userInfo = AppManager.getAccountModel().getUserInfo();
        if (userInfo == null) return;
        List<UserInfo.Social> socialites = userInfo.getSocialites();
        if (socialites == null || socialites.isEmpty()) return;
        UserInfo.Social social = socialites.get(0);
        mTvWechatNickname.setText(social.getNickname());
        mBtBindWechat.setToggleOn();
    }

    @Override
    protected void onRelease() {
        mPresenter.release();
        super.onRelease();
    }

    @Override
    public void setPresenter(SettingContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        mIsInit = false;
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        if (!mIsInit)
            this.mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
    }

    @Override
    public void onFinish() {
        if (!mIsInit)
            this.mActionLoadingDialog.dismiss();
    }

    @Override
    public void syncSleepDiaryCallback(UserSetting userSetting) {
        mIsInit = false;
        runUiThread(() -> {
            if (userSetting == null || userSetting.sleep_diary_enable == 0x00) {
                mTbReminder.setToggleOff();
            } else {
                mTbReminder.setToggleOn();
            }
        });
    }

    @Override
    public void onBindOpenSuccess(UserInfo.Social social) {
        runUiThread(() -> {
            mBtBindWechat.setToggleOn();
            mTvWechatNickname.setText(social.getNickname());
            ToastHelper.show(getString(R.string.bind_open_platform_success));
        });
    }

    @Override
    public void onBindOpenFailed(String error) {
        runUiThread(() -> mBtBindWechat.setToggleOff());
        onFailure(error);
    }

    @OnClick({R.id.lay_about_me, R.id.lay_modify_pwd, R.id.lay_unbind_sleepy, R.id.lay_feedback, R.id.tv_logout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_about_me:
                ConfigActivity.show(v.getContext(), ConfigActivity.ABOUT_TYPE);
                break;
            case R.id.lay_modify_pwd:
                ModifyPwdActivity.show(v.getContext());
                break;
            case R.id.lay_unbind_sleepy:
                BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
                if (bluePeripheral == null || !bluePeripheral.isConnected()) {
                    ToastHelper.show(getString(R.string.not_show_monitor_todo));
                    return;
                }
                QrCodeActivity.show(this);
                break;
            case R.id.lay_feedback://意见反馈
                FeedbackActivity.show(v.getContext());
                break;
            case R.id.tv_logout:
                getSupportFragmentManager()
                    .beginTransaction()
                    .add(LogoutBottomSheet.newInstance(), LogoutBottomSheet.class.getSimpleName())
                    .commit();
                break;
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        switch (share_media) {
            case WEIXIN:
                ToastHelper.show(getString(R.string.opening_wechat));
                break;
        }
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        onFinish();
        mPresenter.bindOpen(share_media, map);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        onFinish();
        switch (share_media) {
            case WEIXIN:
                ToastHelper.show(getString(R.string.no_have_wechat));
                break;
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        onFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void unbindSocial(int socialType, boolean isUnBind) {
        switch (socialType) {
            case SocialPresenter.SOCIAL_WECHAT:
                if (isUnBind) {//解绑成功
                    mBtBindWechat.setToggleOff();
                } else {//解绑失败
                    mBtBindWechat.setToggleOn();
                }
                break;
        }
    }
}
