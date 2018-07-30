package com.sumian.app.account.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.account.contract.RegisterContract;
import com.sumian.app.account.presenter.RegisterPresenter;
import com.sumian.app.app.AppManager;
import com.sumian.app.app.App;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.captcha.CaptchaTimeDistanceConfig;
import com.sumian.app.common.config.SumianConfig;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.util.CheckUtils;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.improve.assessment.AssessmentActivity;
import com.sumian.app.improve.main.HomeActivity;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.request.RegisterBody;
import com.sumian.app.network.response.HwToken;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.adapter.OnTextWatcherAdapter;
import com.sumian.app.widget.refresh.ActionLoadingDialog;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
        RegisterContract.View {

    TitleBar mTitleBar;
    EditText mEtMobile;
    EditText mEtPwd;
    ImageView mIvPwdShow;
    EditText mEtCaptcha;
    TextView mTvCaptcha;
    Button mBtLogin;

    private RegisterContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_register;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mEtMobile = findViewById(R.id.et_mobile);
        mEtPwd = findViewById(R.id.et_pwd);
        mIvPwdShow = findViewById(R.id.iv_pwd_show);
        mEtCaptcha = findViewById(R.id.et_captcha);
        mTvCaptcha = findViewById(R.id.tv_captcha);
        mBtLogin = findViewById(R.id.bt_register);

        findViewById(R.id.bt_register).setOnClickListener(this);
        findViewById(R.id.iv_pwd_show).setOnClickListener(this);
        findViewById(R.id.tv_captcha).setOnClickListener(this);

        this.mTitleBar.addOnBackListener(this);
        this.mEtPwd.addTextChangedListener(new OnTextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mIvPwdShow.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        RegisterPresenter.init(this);
        CaptchaTimeDistanceConfig.showTimer(mTvCaptcha, false, SumianConfig.REGISTER_CAPTCHA_KEY);
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        String mobile;
        int id = v.getId();
        if (id == R.id.bt_register) {
            mobile = this.mEtMobile.getText().toString().trim();
            if (!CheckUtils.isPhoneNum(mobile)) {
                ToastHelper.show(R.string.mobile_error_hint);
                return;
            }
            String pwd = this.mEtPwd.getText().toString().trim();
            if (!CheckUtils.isValidPassword(pwd)) {
                ToastHelper.show(R.string.pwd_error_hint);
                return;
            }
            String captcha = this.mEtCaptcha.getText().toString().trim();
            RegisterBody registerBody = new RegisterBody()
                    .setMobile(mobile)
                    .setPassword(pwd)
                    .setCaptcha(captcha);
            this.mPresenter.doRegister(registerBody);
        } else if (id == R.id.iv_pwd_show) {
            UiUtil.notifyInputType(mIvPwdShow, mEtPwd);
        } else if (id == R.id.tv_captcha) {
            mobile = this.mEtMobile.getText().toString().trim();
            if (!CheckUtils.isPhoneNum(mobile)) {
                ToastHelper.show(R.string.mobile_error_hint);
                return;
            }
            this.mPresenter.doCaptcha(new CaptchaBody().setMobile(mobile));
        }
    }

    @Override
    public void onRegisterSuccess(HwToken token) {
        runUiThread(() -> {
            ToastHelper.show(R.string.register_success_hint);
            if (!AppManager.getAccountModel().isHaveUserInfoAndSleepBarrierTest()) {
                AssessmentActivity.show(this, true);
            } else {
                HomeActivity.show(App.getAppContext());
            }
        });
    }

    @Override
    public void onRegisterFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onCaptchaSuccess() {
        runUiThread(() -> {
            ToastHelper.show(R.string.ok_captcha_hint);
            CaptchaTimeDistanceConfig.showTimer(mTvCaptcha, true, SumianConfig.REGISTER_CAPTCHA_KEY);
        });
    }

    @Override
    public void onCaptchaFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
    }

    @Override
    public void onFinish() {
        mActionLoadingDialog.dismiss();
    }

    @Override
    public void onBack(View v) {
        finish();
    }


    @Override
    protected void onRelease() {
        CaptchaTimeDistanceConfig.NotifyOrClearCaptchaTimeDistance(mTvCaptcha, SumianConfig.REGISTER_CAPTCHA_KEY);
        this.mPresenter.release();
        super.onRelease();
    }

}
