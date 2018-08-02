package com.sumian.hw.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.blue.model.BluePeripheral;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.improve.assessment.QuestionActivity;
import com.sumian.hw.improve.feedback.FeedbackActivity;
import com.sumian.hw.improve.guideline.activity.ManualActivity;
import com.sumian.hw.improve.qrcode.activity.QrCodeActivity;
import com.sumian.hw.network.response.UserSetting;
import com.sumian.hw.setting.contract.SettingContract;
import com.sumian.hw.setting.presenter.SettingPresenter;
import com.sumian.hw.setting.presenter.SocialPresenter;
import com.sumian.hw.setting.sheet.LogoutBottomSheet;
import com.sumian.hw.setting.sheet.SocialBottomSheet;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.ToggleButton;
import com.sumian.hw.widget.refresh.ActionLoadingDialog;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Social;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener, SettingContract.View, UMAuthListener, SocialBottomSheet.UnbindSocialCallback {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
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
            BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral != null && bluePeripheral.isConnected() && mCount >= 5) {
                DeviceLogActivity.show(this);
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
        SettingPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mIsInit = true;
        mPresenter.syncSleepDiary();
        UserInfo userInfo = AppManager.getAccountViewModel().getUserInfo();
        if (userInfo == null) {
            return;
        }
        List<Social> socialites = userInfo.getSocialites();
        if (socialites == null || socialites.isEmpty()) {
            return;
        }
        Social social = socialites.get(0);
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
        if (!mIsInit) {
            this.mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
        }
    }

    @Override
    public void onFinish() {
        if (!mIsInit) {
            this.mActionLoadingDialog.dismiss();
        }
    }

    @Override
    public void syncSleepDiaryCallback(UserSetting userSetting) {
        mIsInit = false;
    }

    @Override
    public void onBindOpenSuccess(Social social) {
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

    @OnClick({
            R.id.siv_modify_password,
            R.id.siv_change_bind,
            R.id.siv_about_us,
            R.id.siv_feedback,
            R.id.siv_sleep_disorders_evaluation,
            R.id.siv_user_guide,
            R.id.tv_logout,
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.siv_modify_password:
                ModifyPwdActivity.show(this);
                break;
            case R.id.siv_change_bind:
                BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
                if (bluePeripheral == null || !bluePeripheral.isConnected()) {
                    ToastHelper.show(getString(R.string.not_show_monitor_todo));
                    return;
                }
                QrCodeActivity.show(this);
                break;
            case R.id.siv_about_us:
                ConfigActivity.show(this, ConfigActivity.ABOUT_TYPE);
                break;
            case R.id.siv_feedback:
                FeedbackActivity.show(this);
                break;
            case R.id.siv_sleep_disorders_evaluation:
                QuestionActivity.show(this);
                break;
            case R.id.siv_user_guide:
                ManualActivity.show(this);
                break;
            case R.id.tv_logout:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(LogoutBottomSheet.newInstance(), LogoutBottomSheet.class.getSimpleName())
                        .commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onStart(SHARE_MEDIA shareMedia) {
        ToastHelper.show(getString(R.string.opening_wechat));
    }

    @Override
    public void onComplete(SHARE_MEDIA shareMedia, int i, Map<String, String> map) {
        onFinish();
        mPresenter.bindOpen(shareMedia, map);
    }

    @Override
    public void onError(SHARE_MEDIA shareMedia, int i, Throwable throwable) {
        onFinish();
        switch (shareMedia) {
            case WEIXIN:
                ToastHelper.show(getString(R.string.no_have_wechat));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA shareMedia, int i) {
        onFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HwAppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void unbindSocial(int socialType, boolean isUnBind) {
        if (isUnBind) {
            mBtBindWechat.setToggleOff();
        } else {
            mBtBindWechat.setToggleOn();
        }
    }
}