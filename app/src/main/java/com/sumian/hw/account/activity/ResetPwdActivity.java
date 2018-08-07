package com.sumian.hw.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.account.contract.ResetPwdContract;
import com.sumian.hw.account.presenter.RestPwdPresenter;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.common.util.CheckUtils;
import com.sumian.hw.network.request.ResetPwdBody;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.refresh.ActionLoadingDialog;
import com.sumian.sleepdoctor.R;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class ResetPwdActivity extends HwBaseActivity implements View.OnClickListener, TitleBar.OnBackListener,
        ResetPwdContract.View {

    public static final String MOBILE_KEY = "mobile";
    private static final String TAG = ResetPwdActivity.class.getSimpleName();
    private static final String TICKET_KEY = "ticket";

    TitleBar mTitleBar;
    FrameLayout mLayPwdPop;
    TextView mTvErrorPop;
    EditText mEtNewPwd;
    EditText mEtNewPwdVerify;
    Button mBtSave;


    private String mMobile;
    private String mTicket;

    private ResetPwdContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context, String mobile, String ticket) {
        Intent intent = new Intent(context, ResetPwdActivity.class);
        intent.putExtra(MOBILE_KEY, mobile);
        intent.putExtra(TICKET_KEY, ticket);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mMobile = bundle.getString(MOBILE_KEY);
            this.mTicket = bundle.getString(TICKET_KEY);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_forget_pwd_two;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mLayPwdPop = findViewById(R.id.lay_pwd_pop);
        mTvErrorPop = findViewById(R.id.tv_error_pop);
        mEtNewPwd = findViewById(R.id.et_new_pwd);
        mEtNewPwdVerify = findViewById(R.id.et_new_pwd_verify);
        mBtSave = findViewById(R.id.bt_save);
        mBtSave.setOnClickListener(this);

        this.mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        RestPwdPresenter.init(this);
    }

    @Override
    public void setPresenter(ResetPwdContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {

        String newPwd = this.mEtNewPwd.getText().toString().trim();

        if (!CheckUtils.isValidPassword(newPwd)) {
            ToastHelper.show(R.string.pwd_error_hint);
            return;
        }

        String verifyNewPwd = this.mEtNewPwdVerify.getText().toString().trim();

        if (!CheckUtils.isValidPassword(verifyNewPwd)) {
            ToastHelper.show(R.string.pwd_error_hint);
            return;
        }

        if (!newPwd.equals(verifyNewPwd)) {
            mLayPwdPop.setVisibility(View.VISIBLE);
            mTvErrorPop.setText(R.string.verify_pwd_error_hint);
            return;
        }

        mLayPwdPop.setVisibility(View.GONE);

        ResetPwdBody resetPwdBody = new ResetPwdBody()
                .setMobile(TextUtils.isEmpty(mMobile) ? "" : mMobile)
                .setPassword(newPwd)
                .setPassword_confirmation(verifyNewPwd)
                .setTicket(TextUtils.isEmpty(mTicket) ? "" : mTicket);

        this.mPresenter.doResetPwd(resetPwdBody);

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
    public void onResetPwdSuccess() {
        runUiThread(() -> {
            ToastHelper.show(R.string.set_pwd_success_hint);
            HwLoginActivity.show(this);
            finish();
        });
    }

    @Override
    public void onResetPwdFailed(String error) {
        runUiThread(() -> {
            mLayPwdPop.setVisibility(View.VISIBLE);
            mTvErrorPop.setText(error);
            ToastHelper.show(error);
        });
    }

    @Override
    public void onBack(View v) {
        finish();
    }


    @Override
    protected void onRelease() {
        super.onRelease();
        this.mPresenter.release();
    }

}
