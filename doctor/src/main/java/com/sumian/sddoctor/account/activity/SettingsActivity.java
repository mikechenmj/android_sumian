package com.sumian.sddoctor.account.activity;

import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.CompoundButton;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.common.base.BaseViewModelActivity;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.account.contract.SettingsContract;
import com.sumian.sddoctor.account.presenter.SettingsPresenter;
import com.sumian.sddoctor.account.sheet.LogoutBottomSheet;
import com.sumian.sddoctor.account.version.VersionManager;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.constants.StatConstants;
import com.sumian.sddoctor.login.login.UserProtocolActivity;
import com.sumian.sddoctor.login.login.bean.DoctorInfo;
import com.sumian.sddoctor.login.login.bean.SocialiteInfo;
import com.sumian.sddoctor.util.UiUtils;
import com.sumian.sddoctor.widget.SumianAlertDialog;
import com.sumian.sddoctor.widget.TitleBar;
import com.sumian.sddoctor.widget.divider.SettingDividerView;
import com.sumian.sddoctor.widget.sheet.AbstractBottomSheetView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


@SuppressWarnings("ConstantConditions")
public class SettingsActivity extends BaseViewModelActivity<SettingsPresenter> implements TitleBar.OnBackClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, SettingsContract.View, Observer<DoctorInfo> {

    private SettingDividerView mSdvMobile;
    private SettingDividerView mSdvBindWechat;
    private SettingDividerView mSdvVersion;

    private boolean mIsSuccess = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setMViewModel(SettingsPresenter.Companion.init(this));
        TitleBar titleBar = findViewById(R.id.title_bar);
        mSdvMobile = findViewById(R.id.sdv_mobile);

        SettingDividerView sdvModifyPwd = findViewById(R.id.sdv_modify_pwd);
        mSdvBindWechat = findViewById(R.id.sdv_bind_wechat);
        mSdvVersion = findViewById(R.id.sdv_version);
        SettingDividerView sdvFeedback = findViewById(R.id.sdv_feedback);
        SettingDividerView userAgreement = findViewById(R.id.sdv_user_agreement);

        findViewById(R.id.tv_logout).setOnClickListener(this);
        titleBar.setOnBackClickListener(this);
        mSdvMobile.setOnClickListener(v -> new SumianAlertDialog(this)
                .setTitle(R.string.modify_cellphone)
                .setMessage(R.string.modify_cellphone_hint)
                .setLeftBtn(R.string.submit, null)
                .show()
        );
        sdvModifyPwd.setOnClickListener(v -> ModifyPasswordActivity.Companion.start());
        mSdvBindWechat.setOnCheckedChangeListener(this);
        mSdvVersion.setOnClickListener(v -> ActivityUtils.startActivity(VersionActivity.class));
        userAgreement.setOnClickListener(v -> ActivityUtils.startActivity(UserProtocolActivity.class));
        sdvFeedback.setOnClickListener(v -> FeedbackActivity.show());
        AppManager.getAccountViewModel().getDoctorInfo().observe(this, this);
    }

    @NotNull
    @Override
    public String getPageName() {
        return StatConstants.page_profile_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        invalidWechat();
        invalidVersion();
        VersionManager.INSTANCE.queryVersion();
        VersionManager.INSTANCE.getMHasUpgradeLiveData().observe(this, isUpdate -> mSdvVersion.redDotInvalid(isUpdate));
    }

    private void invalidVersion() {
        PackageInfo packageInfo = UiUtils.getPackageInfo(this);
        String versionName = packageInfo.versionName;
        mSdvVersion.setContentText(versionName);
    }

    private void invalidWechat() {
        DoctorInfo doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().getValue();
        if (doctorInfo != null) {
            mSdvMobile.setContentText(doctorInfo.getMobile());
            List<SocialiteInfo> socialite = doctorInfo.getSocialite();
            if (socialite == null || socialite.isEmpty()) {
                mSdvBindWechat.setContentText(getString(R.string.no_binding_wechat));
                mSdvBindWechat.setSwitchCheckedWithoutCallback(false);
            } else {
                mSdvBindWechat.setSwitchCheckedWithoutCallback(true);
                SocialiteInfo socialiteInfo = null;
                for (SocialiteInfo s : socialite) {
                    if (s.getType() == 0) {
                        socialiteInfo = s;
                        break;
                    }
                }

                if (socialiteInfo == null) {
                    mSdvBindWechat.setContentText(getString(R.string.no_binding_wechat));
                    mSdvBindWechat.setSwitchCheckedWithoutCallback(false);
                } else {
                    mSdvBindWechat.setContentText(socialiteInfo.getNickname());
                    mSdvBindWechat.setSwitchCheckedWithoutCallback(true);
                }

            }
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        commitBottomSheet(LogoutBottomSheet.newInstance());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mIsSuccess = isChecked;
        if (!isChecked) {
            new SumianAlertDialog(this)
                    .setTitle(R.string.are_you_sure_unbind_wechat_title)
                    .setMessage(R.string.are_you_sure_unbind_wechat_message)
                    .setLeftBtn(R.string.cancel, v -> mSdvBindWechat.setSwitchCheckedWithoutCallback(!mIsSuccess))
                    .whitenLeft()
                    .setRightBtn(R.string.unbind, v -> getMViewModel().unbindWechat())
                    .show();
        } else {
            new SumianAlertDialog(this)
                    .setTitle(R.string.are_you_sure_bind_wechat_title)
                    .setMessage(R.string.are_you_sure_bind_wechat_message)
                    .setLeftBtn(R.string.cancel, v -> mSdvBindWechat.setSwitchCheckedWithoutCallback(!mIsSuccess))
                    .whitenLeft()
                    .setRightBtn(R.string.binding, v -> getMViewModel().bindWechat(SettingsActivity.this))
                    .show();
        }
    }

    private void commitBottomSheet(AbstractBottomSheetView bottomSheetView) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(bottomSheetView, bottomSheetView.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onUnbindSuccess() {
        ToastUtils.showShort(getString(R.string.unbind_wechat_success));
    }

    @Override
    public void onUnBindFailed(@NotNull String error) {
        mSdvBindWechat.setSwitchCheckedWithoutCallback(!mIsSuccess);
        ToastUtils.showShort(error);
    }

    @Override
    public void onBindFailed(@NotNull String error) {
        onUnBindFailed(error);
    }

    @Override
    public void onChanged(@Nullable DoctorInfo doctorInfo) {
        invalidWechat();
    }

    @Override
    public void onBindSuccess() {
        ToastUtils.showShort(getString(R.string.binding_wechat_success));
    }

    @Override
    public void onCancelBind(@NotNull String error) {
        mSdvBindWechat.setSwitchCheckedWithoutCallback(!mIsSuccess);
        ToastUtils.showShort(error);
    }
}
