package com.sumian.sleepdoctor.account.bindMobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.captcha.CaptchaTimeDistanceConfig;
import com.sumian.sleepdoctor.account.config.SumianConfig;
import com.sumian.sleepdoctor.account.userProfile.ImproveUserProfileOneActivity;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/2/28.
 * desc:
 */

public class BindMobileActivity extends BaseActivity<BindMobilePresenter> implements View.OnClickListener, BindMobileContract.View {

    public static final String EXTRA_OPEN_USER_INFO = "open_user_info";
    public static final String EXTRA_SHARE_MEDIA = "share_media";

    @BindView(R.id.et_mobil)
    EditText mEtMobil;
    @BindView(R.id.et_captcha)
    EditText mEtCaptcha;
    @BindView(R.id.bt_send_captcha)
    TextView mBtSendCaptcha;
    @BindView(R.id.bt_login)
    TextView mBtLogin;


    private String mOpenUserInfo;
    private SHARE_MEDIA mShareMedia;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mOpenUserInfo = bundle.getString(EXTRA_OPEN_USER_INFO);
        this.mShareMedia = (SHARE_MEDIA) bundle.get(EXTRA_SHARE_MEDIA);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_main_bind_mobile;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();

        BindMobilePresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        CaptchaTimeDistanceConfig.showTimer(mBtSendCaptcha, false, SumianConfig.BIND_SOCIAL_CAPTCHA_TYPE);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        CaptchaTimeDistanceConfig.NotifyOrClearCaptchaTimeDistance(mBtSendCaptcha, SumianConfig.BIND_SOCIAL_CAPTCHA_TYPE);
    }

    @OnClick({R.id.bt_send_captcha, R.id.bt_login})
    @Override
    public void onClick(View v) {
        String mobile;
        switch (v.getId()) {
            case R.id.bt_send_captcha:
                mobile = mEtMobil.getText().toString().trim();

                if (checkMobile(mobile)) return;

                mPresenter.doSendCaptcha(mobile);
                break;
            case R.id.bt_login:
                mobile = mEtMobil.getText().toString().trim();
                String captcha = mEtCaptcha.getText().toString().trim();
                if (checkMobile(mobile)) return;

                if (TextUtils.isEmpty(captcha)) return;

                int thirdType = 0;
                if (mShareMedia == SHARE_MEDIA.WEIXIN) {
                    thirdType = 0;
                }
                mPresenter.bindOpenSocial(mobile, captcha, thirdType, mOpenUserInfo);
                break;
            default:
                break;
        }
    }

    private boolean checkMobile(String mobile) {
        if (TextUtils.isEmpty(mobile) || !mobile.matches("^1([0-9]{10})$")) {
            showToast(R.string.mobile_error);
            return true;
        }
        return false;
    }

    @Override
    public void setPresenter(BindMobileContract.Presenter presenter) {
        this.mPresenter = (BindMobilePresenter) presenter;
    }

    @Override
    public void onSendCaptchaSuccess() {
        runOnUiThread(() -> CaptchaTimeDistanceConfig.showTimer(mBtSendCaptcha, true, SumianConfig.BIND_SOCIAL_CAPTCHA_TYPE));
    }

    @Override
    public void bindOpenSocialSuccess(Token response) {
        if (response.is_new) {
            ImproveUserProfileOneActivity.show(this, ImproveUserProfileOneActivity.class);
        }else {
            MainActivity.showClearTop(this,MainActivity.class);
        }
    }
}
