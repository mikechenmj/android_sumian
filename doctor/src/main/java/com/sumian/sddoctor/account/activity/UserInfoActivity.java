package com.sumian.sddoctor.account.activity;

import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.common.base.BaseActivity;
import com.sumian.common.image.ImageLoader;
import com.sumian.common.utils.ColorCompatUtil;
import com.sumian.common.widget.dialog.SumianDialog;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.account.contract.UserAvatarContract;
import com.sumian.sddoctor.account.presenter.UserAvatarPresenter;
import com.sumian.sddoctor.account.sheet.SelectDoctorInfoBottomSheet;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.login.login.bean.DoctorInfo;
import com.sumian.sddoctor.me.authentication.AuthenticationActivity;
import com.sumian.sddoctor.widget.TitleBar;
import com.sumian.sddoctor.widget.divider.SettingDividerView;
import com.sumian.sddoctor.widget.sheet.AbstractBottomSheetView;
import com.sumian.sddoctor.widget.sheet.SelectPictureBottomSheet;

import org.jetbrains.annotations.NotNull;


public class UserInfoActivity extends BaseActivity implements TitleBar.OnBackClickListener, View.OnClickListener, UserAvatarContract.View {

    private QMUIRadiusImageView mQMUIRadiusImageView;
    private SettingDividerView mDvName;
    private SettingDividerView mDvAuthentication;
    private SettingDividerView mDvHospital;
    private SettingDividerView mDvDepartment;
    private SettingDividerView mDvJobTitle;
    private SettingDividerView mDvInviteCode;

    private UserAvatarContract.Presenter mPresenter;
    private DoctorInfo mDoctorInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_user_info;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TitleBar titleBar = findViewById(R.id.title_bar);
        mQMUIRadiusImageView = findViewById(R.id.iv_avatar);
        mDvName = findViewById(R.id.dv_name);
        mDvAuthentication = findViewById(R.id.dv_authentication);
        mDvHospital = findViewById(R.id.dv_hospital_name);
        mDvDepartment = findViewById(R.id.dv_department);
        mDvJobTitle = findViewById(R.id.dv_job_title);
        mDvInviteCode = findViewById(R.id.dv_invite_code);

        mPresenter = UserAvatarPresenter.init(UserInfoActivity.this);
        titleBar.setOnBackClickListener(this);
        findViewById(R.id.lay_avatar).setOnClickListener(this);
//        mDvName.setOnClickListener(this);
//        mDvHospital.setOnClickListener(this);
//        mDvDepartment.setOnClickListener(this);
//        mDvJobTitle.setOnClickListener(this);
        mDvAuthentication.setOnClickListener(this);
        AppManager.getAccountViewModel().getDoctorInfo().observe(this, this::invalidDoctorInfo);
    }

    private void invalidDoctorInfo(DoctorInfo doctorInfo) {
        mDoctorInfo = doctorInfo;
        if (doctorInfo != null) {
            ImageLoader.loadImage(doctorInfo.getAvatar(), mQMUIRadiusImageView, R.mipmap.ic_info_avatar_doctor_s, R.mipmap.ic_info_avatar_doctor_s);
            mDvName.setContentText(doctorInfo.getName());
            sedItemContentText(doctorInfo.getAuthenticationString(), "未认证", mDvAuthentication);
            sedItemContentText(doctorInfo.getDepartment(), "未选择", mDvDepartment);
            sedItemContentText(doctorInfo.getTitle(), "未选择", mDvJobTitle);
            sedItemContentText(doctorInfo.getHospital(), "未填写", mDvHospital);
            sedItemContentText(doctorInfo.getInvitation_code(), "未设置", mDvInviteCode);

            // update doctor authentication info visibility
            int authenticationState = doctorInfo.getAuthenticationState();
            boolean showDoctorInfo = authenticationState == DoctorInfo.AUTHENTICATION_STATE_AUTHENTICATED;
            int doctorInfoVisibility = showDoctorInfo ? View.VISIBLE : View.GONE;
            mDvAuthentication.setVisibility(!showDoctorInfo ? View.VISIBLE : View.GONE);
            mDvName.setVisibility(doctorInfoVisibility);
            mDvDepartment.setVisibility(doctorInfoVisibility);
            mDvJobTitle.setVisibility(doctorInfoVisibility);
            mDvHospital.setVisibility(doctorInfoVisibility);
            int authenticationTvColor = R.color.t1_color;
            if (authenticationState == DoctorInfo.AUTHENTICATION_STATE_NOT_AUTHENTICATED) {
                authenticationTvColor = R.color.t4_color;
            } else if (authenticationState == DoctorInfo.AUTHENTICATION_STATE_IS_AUTHENTICATING) {
                authenticationTvColor = R.color.b3_color;
            }
            mDvAuthentication.setTvContentColor(ColorCompatUtil.getColor(this, authenticationTvColor));
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onCleared();
        }
        super.onDestroy();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_avatar:
                SelectPictureBottomSheet.show(getSupportFragmentManager(), filePath -> mPresenter.uploadAvatar(filePath));
                break;
            case R.id.dv_authentication:
                if (mDoctorInfo.getAuthenticationState() == DoctorInfo.AUTHENTICATION_STATE_NOT_AUTHENTICATED) {
                    ActivityUtils.startActivity(this, AuthenticationActivity.class);
                } else if (mDoctorInfo.getAuthenticationState() == DoctorInfo.AUTHENTICATION_STATE_IS_AUTHENTICATING) {
                    new SumianDialog(this)
                            .setTitleText(R.string.doctor_authentication)
                            .setMessageText(R.string.your_authentication_is_goning)
                            .setLeftBtn(R.string.confirm, null)
                            .show();
                }
                break;
            case R.id.dv_name:
                ModifyDoctorInfoActivity.Companion.show(this, ModifyDoctorInfoActivity.MODIFY_TYPE_NAME);
                break;
            case R.id.dv_hospital_name:
                ModifyDoctorInfoActivity.Companion.show(this, ModifyDoctorInfoActivity.MODIFY_TYPE_HOSPITAL);
                break;
            case R.id.dv_department:
                ModifyDoctorInfoActivity.Companion.show(this, ModifyDoctorInfoActivity.MODIFY_TYPE_DEPARTMENT);
                break;
            case R.id.dv_job_title:
                commitBottomSheet(SelectDoctorInfoBottomSheet.Companion.newInstance(ModifyDoctorInfoActivity.MODIFY_TYPE_JOB_TITLE));
                break;
            default:
                break;
        }
    }

    private void sedItemContentText(String content, String defaultContent, SettingDividerView sdv) {
        sdv.setContentText(TextUtils.isEmpty(content) ? defaultContent : content);
    }

    private void commitBottomSheet(AbstractBottomSheetView bottomSheetView) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(bottomSheetView, bottomSheetView.getClass().getSimpleName())
                .commitNowAllowingStateLoss();
    }

    @Override
    public void onUploadAvatarSuccess(@NotNull DoctorInfo userInfo) {
        invalidDoctorInfo(userInfo);
    }

    @Override
    public void onUploadAvatarFailed(@NotNull String error) {
        dismissLoading();
        ToastUtils.showShort(error);
    }
}
