package com.sumian.sd.buz.account.userProfile;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.sumian.common.base.BaseViewModelActivity;
import com.sumian.sd.R;
import com.sumian.sd.widget.TitleBar;

import org.jetbrains.annotations.NotNull;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileOneActivity extends BaseViewModelActivity<ImproveUserProfilePresenter> implements View.OnClickListener,
        TitleBar.OnBackClickListener, TitleBar.OnMenuClickListener, ImproveUserProfileContract.View {

    private TextInputEditText mEtCaptcha;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_improve_user_profile_one;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ImproveUserProfilePresenter.init(this);
        TitleBar titleBar = findViewById(R.id.title_bar);
        mEtCaptcha = findViewById(R.id.et_captcha);
        Button btNextStep = findViewById(R.id.bt_next_step);
        btNextStep.setOnClickListener(this);

        // mTitleBar.getTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.font_16);
        //mTitleBar.getMore().setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.font_14);
        titleBar.setOnBackClickListener(this).setOnMenuClickListener(this);
    }


    @Override
    public void onClick(View v) {

        String nickname = mEtCaptcha.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)) {
            ToastUtils.showShort(R.string.improve_user_profile_one_part_two);
            return;
        }

        if (nickname.length() <= 0 || nickname.length() > 10) {
            ToastUtils.showShort(R.string.error_nickname);
            return;
        }

        getMViewModel().improveUserProfile(ImproveUserProfileContract.IMPROVE_NICKNAME_KEY, nickname);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onFailure(String error) {
        ToastUtils.showShort(error);
    }

    @Override
    public void onMenuClick(View v) {
        ActivityUtils.startActivity(ImproveUserProfileTwoActivity.class);
    }

    @Override
    public void setPresenter(@NotNull ImproveUserProfileContract.Presenter presenter) {
        this.setMViewModel((ImproveUserProfilePresenter) presenter);
    }

    @Override
    public void onImproveUserProfileSuccess() {
        onMenuClick(getWindow().getDecorView());
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }
}
