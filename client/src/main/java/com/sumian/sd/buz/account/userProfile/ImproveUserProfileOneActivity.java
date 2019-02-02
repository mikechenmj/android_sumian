package com.sumian.sd.buz.account.userProfile;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.widget.TitleBar;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileOneActivity extends SdBaseActivity<ImproveUserProfilePresenter> implements View.OnClickListener,
        TitleBar.OnBackClickListener, TitleBar.OnMenuClickListener, ImproveUserProfileContract.View {

    private TextInputEditText mEtCaptcha;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_improve_user_profile_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        TitleBar titleBar = findViewById(R.id.title_bar);

        mEtCaptcha = findViewById(R.id.et_captcha);
        Button btNextStep = findViewById(R.id.bt_next_step);
        btNextStep.setOnClickListener(this);

        // mTitleBar.getTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.font_16);
        //mTitleBar.getMore().setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.font_14);
        titleBar.setOnBackClickListener(this).setOnMenuClickListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        ImproveUserProfilePresenter.init(this);
    }

    @Override
    public void onClick(View v) {

        String nickname = mEtCaptcha.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)) {
            showCenterToast(R.string.improve_user_profile_one_part_two);
            return;
        }

        if (nickname.length() <= 0 || nickname.length() > 10) {
            showCenterToast(R.string.error_nickname);
            return;
        }

        mPresenter.improveUserProfile(ImproveUserProfileContract.IMPROVE_NICKNAME_KEY, nickname);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onMenuClick(View v) {
        ActivityUtils.startActivity(ImproveUserProfileTwoActivity.class);
    }

    @Override
    public void setPresenter(ImproveUserProfileContract.Presenter presenter) {
        this.mPresenter = (ImproveUserProfilePresenter) presenter;
    }

    @Override
    public void onImproveUserProfileSuccess() {
        onMenuClick(mRoot);
    }
}
