package com.sumian.sleepdoctor.account.userProfile.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.userProfile.contract.ImproveUserProfileContract;
import com.sumian.sleepdoctor.account.userProfile.presenter.ImproveUserProfilePresenter;
import com.sumian.sleepdoctor.base.SdBaseActivity;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileOneActivity extends SdBaseActivity<ImproveUserProfilePresenter> implements View.OnClickListener,
        TitleBar.OnBackClickListener, TitleBar.OnMenuClickListener, ImproveUserProfileContract.View {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_captcha)
    TextInputEditText mEtCaptcha;

    @BindView(R.id.bt_next_step)
    AppCompatButton mBtNextStep;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_improve_user_profile_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // mTitleBar.getTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.font_16);
        //mTitleBar.getMore().setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.font_14);
        mTitleBar.setOnBackClickListener(this).setOnMenuClickListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        ImproveUserProfilePresenter.init(this);
    }

    @OnClick(R.id.bt_next_step)
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
        ImproveUserProfileTwoActivity.show(this, ImproveUserProfileTwoActivity.class);
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
