package com.sumian.sleepdoctor.account.fragment;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.contract.ImproveUserProfileContract;
import com.sumian.sleepdoctor.account.presenter.ImproveUserProfilePresenter;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileOneFragment extends BaseFragment<ImproveUserProfilePresenter> implements View.OnClickListener,
        TitleBar.OnBackListener, TitleBar.OnMoreListener, ImproveUserProfileContract.View {


    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_captcha)
    TextInputEditText mEtCaptcha;

    @BindView(R.id.bt_next_step)
    AppCompatButton mBtNextStep;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_improve_user_profile_one;
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

    @OnClick(R.id.bt_next_step)
    @Override
    public void onClick(View v) {

        String nickname = mEtCaptcha.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)) {
            showToast(R.string.improve_user_profile_one_part_two);
            return;
        }

        mPresenter.improveUserProfile(ImproveUserProfileContract.IMPROVE_NICKNAME_KEY, nickname);
    }

    @Override
    public void onBack(View v) {
        popBack();
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onMore(View v) {
        commitReplace(ImproveUserProfileTwoFragment.class);
    }

    @Override
    public void bindPresenter(ImproveUserProfileContract.Presenter presenter) {
        this.mPresenter = (ImproveUserProfilePresenter) presenter;
    }

    @Override
    public void onImproveUserProfileSuccess() {
        goHome();
    }
}
