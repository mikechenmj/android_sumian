package com.sumian.sleepdoctor.account.activity;

import android.text.TextUtils;
import android.view.View;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.captcha.CaptchaTimeDistanceConfig;
import com.sumian.sleepdoctor.account.config.SumianConfig;
import com.sumian.sleepdoctor.account.contract.LoginContract;
import com.sumian.sleepdoctor.account.presenter.LoginPresenter;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public final class LoginActivity extends BaseActivity<LoginPresenter> implements View.OnClickListener, LoginContract.View {

    @BindView(R.id.et_mobil)
    EditText mEtMobil;
    @BindView(R.id.et_captcha)
    EditText mEtCaptcha;
    @BindView(R.id.bt_send_captcha)
    Button mBtSendCaptcha;
    @BindView(R.id.bt_login)
    Button mBtLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_main_login;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        super.initData();
        runOnUiThread(() -> CaptchaTimeDistanceConfig.showTimer(mBtSendCaptcha, false, SumianConfig.LOGIN_CAPTCHA_TYPE));
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        LoginPresenter.init(this);
    }

    @Override
    public void bindPresenter(LoginContract.Presenter presenter) {
        mPresenter = (LoginPresenter) presenter;
    }

    @Override
    protected void onRelease() {
        super.onRelease();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onLoginSuccess(boolean isNewAccount) {
        runOnUiThread(() -> {
            if (isNewAccount) {
                ImproveUserProfileOneActivity.show(mEtMobil.getContext(), ImproveUserProfileOneActivity.class);
            } else {
                MainActivity.show(mEtMobil.getContext(), MainActivity.class);
            }
        });
    }

    @Override
    public void onSendCaptchaSuccess() {
        runOnUiThread(() -> CaptchaTimeDistanceConfig.showTimer(mBtSendCaptcha, true, SumianConfig.LOGIN_CAPTCHA_TYPE));
    }

    @Override
    public void onFailure(String error) {
        runOnUiThread(() -> ToastHelper.show(error));
    }

    @OnClick({R.id.bt_send_captcha, R.id.bt_login})
    @Override
    public void onClick(View v) {
        String mobile;
        switch (v.getId()) {
            case R.id.bt_send_captcha:
                mobile = mEtMobil.getText().toString().trim();
                mPresenter.doSendCaptcha(mobile);
                break;
            case R.id.bt_login:
                mobile = mEtMobil.getText().toString().trim();
                String captcha = mEtCaptcha.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) return;

                if (TextUtils.isEmpty(captcha)) return;

                mPresenter.doLogin(mobile, captcha);
                break;
            default:
                break;
        }
    }
}
