package com.sumian.sleepdoctor.account.fragment;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ImproveUserProfileOneFragment extends BaseFragment implements View.OnClickListener,
        TitleBar.OnBackListener, TitleBar.OnMoreListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.et_captcha)
    TextInputEditText mEtCaptcha;
    @BindView(R.id.bt_next_step)
    AppCompatButton mBtNextStep;

    public static ImproveUserProfileOneFragment newInstance() {
        return new ImproveUserProfileOneFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_improve_user_profile_one;
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
    }

    @OnClick(R.id.bt_next_step)
    @Override
    public void onClick(View v) {
        onMore(v);
    }

    @Override
    public void onBack(View v) {
        popBack();
    }

    @Override
    public void onMore(View v) {
        commitReplacePager(ImproveUserProfileTwoFragment.newInstance());
    }
}
