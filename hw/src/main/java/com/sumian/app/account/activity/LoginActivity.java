package com.sumian.app.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.account.contract.LoginContract;
import com.sumian.app.account.presenter.LoginPresenter;
import com.sumian.app.app.delegate.ApplicationDelegate;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.network.request.LoginBody;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.refresh.ActionLoadingDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
    LoginContract.View {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String KEY_LAUNCHER = "extra_launcher";
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_mobile)
    EditText mEtMobile;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;

    @BindView(R.id.bt_login)
    Button mBtLogin;
    @BindView(R.id.tv_forget_pwd)
    TextView mTvForgetPwd;

    private boolean mIsLauncher;

    private LoginContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void show(Context context, boolean isLauncher) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_LAUNCHER, isLauncher);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        LoginPresenter.init(this);
        if (bundle != null) {
            this.mIsLauncher = bundle.getBoolean(KEY_LAUNCHER, false);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        if (mIsLauncher) {
            this.mTitleBar.addOnBackListener(this);
        } else {
            this.mTitleBar.hideBack();
        }
    }

    @OnClick({R.id.bt_login, R.id.tv_forget_pwd})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:

                String mobile = this.mEtMobile.getText().toString().trim();

                if (TextUtils.isEmpty(mobile)) {
                    ToastHelper.show(R.string.mobile_error_hint);
                    return;
                }

                String pwd = this.mEtPwd.getText().toString().trim();

                if (TextUtils.isEmpty(pwd)) {
                    ToastHelper.show(R.string.pwd_error_hint);
                    return;
                }

                LoginBody loginBody = new LoginBody()
                    .setMobile(mobile)
                    .setPassword(pwd);
                this.mPresenter.doLogin(loginBody);

                break;
            case R.id.tv_forget_pwd:
                ForgetPwdActivity.show(this);
                break;
            default:
                break;
        }

    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void loginSuccess() {
        ApplicationDelegate.goHome(this);
        runUiThread(() -> ToastHelper.show(R.string.login_success_hint));
    }

    @Override
    public void loginFailed(String error) {
        onFailure(error);
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        runUiThread(() -> ToastHelper.show(R.string.in_login_hint));
        mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
    }

    @Override
    public void onFinish() {
        mActionLoadingDialog.dismiss();
    }

    @Override
    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        ApplicationDelegate.setIsLoginActivity(false);
        LoginRouterActivity.show(this);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        this.mPresenter.release();
    }
}
