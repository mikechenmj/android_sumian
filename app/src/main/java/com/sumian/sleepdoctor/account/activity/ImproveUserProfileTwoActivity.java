package com.sumian.sleepdoctor.account.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.contract.ImproveUserProfileContract;
import com.sumian.sleepdoctor.account.presenter.ImproveUserProfilePresenter;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileTwoActivity extends BaseActivity<ImproveUserProfilePresenter> implements View.OnClickListener, TitleBar.OnBackListener, TitleBar.OnMoreListener, ImproveUserProfileContract.View {

    private static final String TAG = ImproveUserProfileOneActivity.class.getSimpleName();

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_captcha)
    TextInputEditText mEtCaptcha;
    @BindView(R.id.bt_complete)
    AppCompatButton mBtComplete;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_improve_user_profile_two;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this).addOnMoreListener(this);
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
        mPresenter.improveUserProfile(ImproveUserProfileContract.IMPROVE_NAME_KEY, name);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onMore(View v) {
        Token cacheToken = AppManager.getAccountViewModel().getToken();
        cacheToken.is_new = false;
        AppManager.getAccountViewModel().updateToken(cacheToken);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> finish());
    }

    @Override
    public void bindPresenter(ImproveUserProfileContract.Presenter presenter) {
        this.mPresenter = (ImproveUserProfilePresenter) presenter;
    }

    @Override
    public void onImproveUserProfileSuccess() {
        MainActivity.show(this,MainActivity.class);
    }
}
