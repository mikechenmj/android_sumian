package com.sumian.hw.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.account.contract.LoginContract;
import com.sumian.hw.account.presenter.LoginPresenter;
import com.sumian.sleepdoctor.app.HwApplicationDelegate;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.network.request.LoginBody;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.refresh.ActionLoadingDialog;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class HwLoginActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
        LoginContract.View {

    private static final String TAG = HwLoginActivity.class.getSimpleName();

    public static final String KEY_LAUNCHER = "extra_launcher";
    TitleBar mTitleBar;
    EditText mEtMobile;
    EditText mEtPwd;
    Button mBtLogin;
    TextView mTvForgetPwd;

    private boolean mIsLauncher;

    private LoginContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context) {
        Intent intent = new Intent(context, HwLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void show(Context context, boolean isLauncher) {
        Intent intent = new Intent(context, HwLoginActivity.class);
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
        mTitleBar = findViewById(R.id.title_bar);
        mEtMobile = findViewById(R.id.et_mobile);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtLogin = findViewById(R.id.bt_login);
        mTvForgetPwd = findViewById(R.id.tv_forget_pwd);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);

        if (mIsLauncher) {
            this.mTitleBar.addOnBackListener(this);
        } else {
            this.mTitleBar.hideBack();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_login) {
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
        } else if (id == R.id.tv_forget_pwd) {
            ForgetPwdActivity.show(this);
        }
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void loginSuccess() {
        HwApplicationDelegate.goHome(this);
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
        HwApplicationDelegate.setIsLoginActivity(false);
        LoginRouterActivity.show(this);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        this.mPresenter.release();
    }
}
