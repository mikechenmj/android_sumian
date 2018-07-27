package com.sumian.app.account.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.account.contract.ValidationCaptchaContract;
import com.sumian.app.account.presenter.ValidationCaptchaPresenter;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.captcha.CaptchaTimeDistanceConfig;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.config.SumianConfig;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.request.ValidationCaptchaBody;
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

public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
    ValidationCaptchaContract.View {

    private static final String TAG = ForgetPwdActivity.class.getSimpleName();

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_mobile)
    EditText mEtMobile;
    @BindView(R.id.et_captcha)
    EditText mEtCaptcha;

    @BindView(R.id.tv_captcha)
    TextView mTvCaptcha;

    @BindView(R.id.bt_ok)
    Button mBtOk;

    private String mMobile;

    private ValidationCaptchaContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context) {
        context.startActivity(new Intent(context, ForgetPwdActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_foregt_pwd_one;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        this.mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        CaptchaTimeDistanceConfig.showTimer(mTvCaptcha, false, SumianConfig.FORGET_PWD_CAPTCHA_KEY);
        ValidationCaptchaPresenter.init(this);
    }

    @Override
    public void setPresenter(ValidationCaptchaContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @OnClick({R.id.tv_captcha, R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_captcha:

                mMobile = this.mEtMobile.getText().toString().trim();
                if (TextUtils.isEmpty(mMobile)) {
                    ToastHelper.show(R.string.mobile_error_hint);
                    return;
                }

                this.mPresenter.doCaptcha(new CaptchaBody().setMobile(mMobile));

                break;
            case R.id.bt_ok:

                String captcha = this.mEtCaptcha.getText().toString().trim();
                if (TextUtils.isEmpty(captcha)) {
                    ToastHelper.show(R.string.captcha_error_hint);
                    return;
                }

                ValidationCaptchaBody validationCaptchaBody = new ValidationCaptchaBody()
                    .setMobile(mMobile)
                    .setCaptcha(captcha);

                mPresenter.doValidationCaptcha(validationCaptchaBody);
                break;
            default:
                break;
        }

    }

    @Override
    public void onValidationCaptchaSuccess(String ticket) {
        ResetPwdActivity.show(this, mMobile, ticket);
    }

    @Override
    public void onValidationCaptchaFailed(String error) {
        onFailure(error);
    }

    @Override
    public void onCaptchaSuccess() {
        runUiThread(() -> {
            CaptchaTimeDistanceConfig.showTimer(mTvCaptcha, true, SumianConfig.FORGET_PWD_CAPTCHA_KEY);
            ToastHelper.show(R.string.ok_captcha_hint);
        });
    }

    @Override
    public void onCaptchaFailed(String error) {
        onFailure(error);
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
        this.mPresenter.release();
        CaptchaTimeDistanceConfig.NotifyOrClearCaptchaTimeDistance(mTvCaptcha, SumianConfig.FORGET_PWD_CAPTCHA_KEY);
        super.onRelease();
    }

}
