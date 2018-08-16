package com.sumian.sd.account.loginold;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.utils.AppUtil;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.account.bindMobile.BindMobileActivity;
import com.sumian.sd.account.captcha.CaptchaTimeDistanceConfig;
import com.sumian.sd.account.config.SumianConfig;
import com.sumian.sd.account.userProfile.activity.MyTargetAndInformationActivity;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.setting.version.delegate.VersionDelegate;
import com.sumian.sd.widget.LoginRuleView;
import com.sumian.sd.widget.dialog.ActionLoadingDialog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public final class LoginActivity extends SdBaseActivity<LoginPresenter> implements View.OnClickListener, LoginContract.View, LoginRuleView.OnCheckedListener, UMAuthListener {

    public static final int REQUEST_CODE_SET_USER_INFO = 100;

    @BindView(R.id.et_mobil)
    EditText mEtMobil;
    @BindView(R.id.et_captcha)
    EditText mEtCaptcha;
    @BindView(R.id.bt_send_captcha)
    TextView mBtSendCaptcha;
    @BindView(R.id.bt_login)
    TextView mBtLogin;

    @BindView(R.id.login_rule_view)
    LoginRuleView mLoginRuleView;

    private ActionLoadingDialog mActionLoadingDialog;

    private VersionDelegate mVersionDelegate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_login;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mLoginRuleView.setOnCheckedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mVersionDelegate = VersionDelegate.Companion.init();
        runOnUiThread(() -> CaptchaTimeDistanceConfig.showTimer(mBtSendCaptcha, false, SumianConfig.LOGIN_CAPTCHA_TYPE));
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        LoginPresenter.init(this);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = (LoginPresenter) presenter;
    }

    @Override
    protected void onRelease() {
        super.onRelease();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mVersionDelegate.checkVersion(this);
    }


    @Override
    public void onBegin() {
        if (mActionLoadingDialog == null) {
            mActionLoadingDialog = new ActionLoadingDialog();
        }
        if (!mActionLoadingDialog.isAdded()) {
            mActionLoadingDialog.show(getSupportFragmentManager());
        }
    }

    @Override
    public void onFinish() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }

    @Override
    public void onLoginSuccess(boolean isNewAccount) {
        runOnUiThread(() -> {
            if (isNewAccount) {
                MyTargetAndInformationActivity.Companion.launchForResult(this, false, REQUEST_CODE_SET_USER_INFO);
            } else {
                launchMainAndFinish();
            }
        });
    }

    private void launchMainAndFinish() {
        AppUtil.launchMainAndFinishAll();
    }

    @Override
    public void onSendCaptchaSuccess() {
        runOnUiThread(() -> CaptchaTimeDistanceConfig.showTimer(mBtSendCaptcha, true, SumianConfig.LOGIN_CAPTCHA_TYPE));
    }

    @Override
    public void onBindOpenSuccess(Token token) {
        launchMainAndFinish();
    }

    @Override
    public void onNotBindCallback(String error, String openUserInfo) {
        Bundle extras = new Bundle();
        extras.putSerializable(BindMobileActivity.EXTRA_SHARE_MEDIA, SHARE_MEDIA.WEIXIN);
        extras.putString(BindMobileActivity.EXTRA_OPEN_USER_INFO, openUserInfo);
        BindMobileActivity.show(this, BindMobileActivity.class, extras);
    }

    @Override
    public void onFailure(String error) {
        runOnUiThread(() -> ToastHelper.show(error));
    }

    @OnClick({R.id.bt_send_captcha, R.id.bt_login, R.id.tv_wechat_login})
    @Override
    public void onClick(View v) {
        String mobile;
        switch (v.getId()) {
            case R.id.bt_send_captcha:
                mobile = mEtMobil.getText().toString().trim();
                if (checkMobile(mobile)) {
                    return;
                }
                mPresenter.doSendCaptcha(mobile);
                break;
            case R.id.bt_login:
                mobile = mEtMobil.getText().toString().trim();
                String captcha = mEtCaptcha.getText().toString().trim();
                if (checkMobile(mobile)) {
                    return;
                }
                if (TextUtils.isEmpty(captcha)) {
                    return;
                }
                mPresenter.doLogin(mobile, captcha);
                break;
            case R.id.tv_wechat_login:
                mPresenter.doLoginOpen(SHARE_MEDIA.WEIXIN, this, this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_USER_INFO) {
            launchMainAndFinish();
        } else {
            AppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart(SHARE_MEDIA shareMedia) {
        switch (shareMedia) {
            case WEIXIN:
                ToastHelper.show(R.string.opening_wechat);
                break;
            default:
                break;
        }
    }

    @Override
    public void onComplete(SHARE_MEDIA shareMedia, int i, Map<String, String> map) {
        mPresenter.checkOpenIsBind(shareMedia, map);
        onFinish();
    }

    @Override
    public void onError(SHARE_MEDIA shareMedia, int i, Throwable throwable) {
        onFinish();
        // if (i == UMAuthListener.ACTION_AUTHORIZE) {
        switch (shareMedia) {
            case WEIXIN:
                ToastHelper.show(R.string.no_have_wechat);
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
    public void onChecked(boolean isChecked) {
        mBtLogin.setEnabled(isChecked);
        mBtLogin.setBackground(isChecked ? getDrawable(R.drawable.bg_bt) : getDrawable(R.drawable.bg_enable_bt));
    }

    private boolean checkMobile(String mobile) {
        if (TextUtils.isEmpty(mobile) || !mobile.matches("^1([0-9]{10})$")) {
            showToast(R.string.mobile_error);
            return true;
        }
        return false;
    }
}
