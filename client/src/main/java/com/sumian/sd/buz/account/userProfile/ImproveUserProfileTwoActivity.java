package com.sumian.sd.buz.account.userProfile;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.sumian.common.base.BaseViewModelActivity;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.account.bean.Token;
import com.sumian.sd.widget.TitleBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileTwoActivity extends BaseViewModelActivity<ImproveUserProfilePresenter> implements View.OnClickListener, TitleBar.OnBackClickListener,
        TitleBar.OnMenuClickListener, ImproveUserProfileContract.View, Observer<Token> {

    private TextInputEditText mEtCaptcha;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_improve_user_profile_two;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ImproveUserProfilePresenter.init(this);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnBackClickListener(this).setOnMenuClickListener(this);

        mEtCaptcha = findViewById(R.id.et_captcha);

        Button btComplete = findViewById(R.id.bt_complete);
        btComplete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public void onClick(View v) {
        String name = mEtCaptcha.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort(R.string.improve_user_profile_two_part_one);
            return;
        }

        if (name.length() < 2 || name.length() > 8) {
            ToastUtils.showShort(R.string.name_error);
            return;
        }

        getMViewModel().improveUserProfile(ImproveUserProfileContract.IMPROVE_NAME_KEY, name);
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
        Token cacheToken = AppManager.getAccountViewModel().getToken();
        cacheToken.is_new = false;
        AppManager.getAccountViewModel().updateToken(cacheToken);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, this);
    }

    @Override
    public void setPresenter(ImproveUserProfileContract.Presenter presenter) {
        this.setMViewModel((ImproveUserProfilePresenter) presenter);
    }

    @Override
    public void onImproveUserProfileSuccess() {
        AppManager.INSTANCE.launchMainAndFinishAll();
    }

    @Override
    public void onChanged(@Nullable Token token) {
        onImproveUserProfileSuccess();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }
}
