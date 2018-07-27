package com.sumian.app.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.account.contract.OpenBindContract;
import com.sumian.app.account.presenter.OpenBindPresenter;
import com.sumian.app.app.delegate.ApplicationDelegate;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.captcha.CaptchaTimeDistanceConfig;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.config.SumianConfig;
import com.sumian.app.common.util.CheckUtils;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.network.request.CaptchaBody;
import com.sumian.app.network.response.Token;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.adapter.OnTextWatcherAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class OpenBindActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
    OpenBindContract.View {

    private static final String TAG = OpenBindActivity.class.getSimpleName();

    private static final String EXTRA_OPEN_USER_INFO = "open_user_info";
    private static final String EXTRA_SHARE_MEDIA = "share_media";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_mobile)
    EditText mEtMobile;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_pwd_show)
    ImageView mIvPwdShow;
    @BindView(R.id.et_captcha)
    EditText mEtCaptcha;
    @BindView(R.id.tv_captcha)
    TextView mTvCaptcha;

    @BindView(R.id.bt_bind)
    Button mBtBind;

    private OpenBindContract.Presenter mPresenter;

    private String mOpenUserInfo;
    private SHARE_MEDIA mShareMedia;

    public static void show(Context context, String openUserInfo, SHARE_MEDIA shareMedia) {
        Intent intent = new Intent(context, OpenBindActivity.class);
        intent.putExtra(EXTRA_OPEN_USER_INFO, openUserInfo);
        intent.putExtra(EXTRA_SHARE_MEDIA, shareMedia);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mOpenUserInfo = bundle.getString(EXTRA_OPEN_USER_INFO);
        this.mShareMedia = (SHARE_MEDIA) bundle.get(EXTRA_SHARE_MEDIA);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_bind_open;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
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
        OpenBindPresenter.init(this);
        CaptchaTimeDistanceConfig.showTimer(mTvCaptcha, false, SumianConfig.REGISTER_CAPTCHA_KEY);
    }

    @Override
    public void setPresenter(OpenBindContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @OnClick({R.id.bt_bind, R.id.iv_pwd_show, R.id.tv_captcha})
    @Override
    public void onClick(View v) {
        String mobile;
        switch (v.getId()) {
            case R.id.bt_bind:

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

                this.mPresenter.doBind(mobile, pwd, captcha, mShareMedia, mOpenUserInfo);

                break;
            case R.id.iv_pwd_show:
                UiUtil.notifyInputType(mIvPwdShow, mEtPwd);
                break;
            case R.id.tv_captcha:

                mobile = this.mEtMobile.getText().toString().trim();

                if (!CheckUtils.isPhoneNum(mobile)) {
                    ToastHelper.show(R.string.mobile_error_hint);
                    return;
                }

                this.mPresenter.doCaptcha(new CaptchaBody().setMobile(mobile));

                break;
            default:
                break;
        }

    }

    @Override
    public void onBindSuccess(Token token) {
        runUiThread(() -> ToastHelper.show(R.string.register_success_hint));
        ApplicationDelegate.goHome(this);
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
        onFailure(error);
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

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
