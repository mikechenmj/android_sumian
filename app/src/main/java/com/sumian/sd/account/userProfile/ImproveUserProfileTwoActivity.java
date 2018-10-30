package com.sumian.sd.account.userProfile;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.sumian.sd.R;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.utils.AppUtil;
import com.sumian.sd.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileTwoActivity extends SdBaseActivity<ImproveUserProfilePresenter> implements View.OnClickListener, TitleBar.OnBackClickListener,
        TitleBar.OnMenuClickListener, ImproveUserProfileContract.View, Observer<Token> {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_captcha)
    TextInputEditText mEtCaptcha;
    @BindView(R.id.bt_complete)
    AppCompatButton mBtComplete;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_improve_user_profile_two;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this).setOnMenuClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        ImproveUserProfilePresenter.init(this);
    }

    @OnClick(R.id.bt_complete)
    @Override
    public void onClick(View v) {
        String name = mEtCaptcha.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showToast(R.string.improve_user_profile_two_part_one);
            return;
        }

        if (name.length() < 2 || name.length() > 8) {
            showToast(R.string.name_error);
            return;
        }

        mPresenter.improveUserProfile(ImproveUserProfileContract.IMPROVE_NAME_KEY, name);
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
        Token cacheToken = AppManager.getAccountViewModel().getToken();
        cacheToken.is_new = false;
        AppManager.getAccountViewModel().updateToken(cacheToken);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, this);
    }

    @Override
    public void setPresenter(ImproveUserProfileContract.Presenter presenter) {
        this.mPresenter = (ImproveUserProfilePresenter) presenter;
    }

    @Override
    public void onImproveUserProfileSuccess() {
        AppUtil.launchMainAndFinishAll();
    }

    @Override
    public void onChanged(@Nullable Token token) {
        onImproveUserProfileSuccess();
    }
}
