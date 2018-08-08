package com.sumian.hw.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.common.util.CheckUtils;
import com.sumian.hw.common.util.UiUtil;
import com.sumian.hw.network.request.ModifyPwdBody;
import com.sumian.hw.setting.contract.ModifyPwdContract;
import com.sumian.hw.setting.presenter.ModifyPwdPresenter;
import com.sumian.hw.utils.AppUtil;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.adapter.OnTextWatcherAdapter;
import com.sumian.sleepdoctor.R;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class ModifyPwdActivity extends HwBaseActivity implements TitleBar.OnBackListener, View.OnClickListener, ModifyPwdContract.View {

    TitleBar mTitleBar;
    TextView mTvErrorPop;
    FrameLayout mLayPwdPop;
    EditText mEtOldPwd;
    ImageView mIvOldPwdShow;
    EditText mEtNewPwd;
    ImageView mIvNewPwdShow;
    EditText mEtReNewPwd;
    ImageView mIvReNewPwdShow;
    Button mBtSave;

    private ModifyPwdContract.Presenter mPresenter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, ModifyPwdActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_modify_pwd;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mTvErrorPop = findViewById(R.id.tv_error_pop);
        mLayPwdPop = findViewById(R.id.lay_pwd_pop);
        mEtOldPwd = findViewById(R.id.et_old_pwd);
        mIvOldPwdShow = findViewById(R.id.iv_old_pwd_show);
        mEtNewPwd = findViewById(R.id.et_new_pwd);
        mIvNewPwdShow = findViewById(R.id.iv_new_pwd_show);
        mEtReNewPwd = findViewById(R.id.et_re_new_pwd);
        mIvReNewPwdShow = findViewById(R.id.iv_re_new_pwd_show);
        mBtSave = findViewById(R.id.bt_save);
        findViewById(R.id.iv_old_pwd_show).setOnClickListener(this);
        findViewById(R.id.iv_new_pwd_show).setOnClickListener(this);
        findViewById(R.id.iv_re_new_pwd_show).setOnClickListener(this);
        findViewById(R.id.bt_save).setOnClickListener(this);

        this.mTitleBar.addOnBackListener(this);
        this.mEtOldPwd.addTextChangedListener(new OnTextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mIvOldPwdShow.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        this.mEtNewPwd.addTextChangedListener(new OnTextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mIvNewPwdShow.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        this.mEtReNewPwd.addTextChangedListener(new OnTextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mIvReNewPwdShow.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        ModifyPwdPresenter.init(this);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_old_pwd_show) {
            UiUtil.notifyInputType(mIvOldPwdShow, mEtOldPwd);
        } else if (id == R.id.iv_new_pwd_show) {
            UiUtil.notifyInputType(mIvNewPwdShow, mEtNewPwd);
        } else if (id == R.id.iv_re_new_pwd_show) {
            UiUtil.notifyInputType(mIvReNewPwdShow, mEtReNewPwd);
        } else if (id == R.id.bt_save) {
            String oldPwd = this.mEtOldPwd.getText().toString().trim();
            String newPwd = this.mEtNewPwd.getText().toString().trim();
            String reNewPwd = this.mEtReNewPwd.getText().toString().trim();
            mLayPwdPop.setVisibility(View.GONE);
            if (!CheckUtils.isValidPassword(oldPwd)) {
                ToastHelper.show(R.string.pwd_error_hint);
                return;
            }
            if (!CheckUtils.isValidPassword(newPwd)) {
                ToastHelper.show(R.string.pwd_error_hint);
                return;
            }
            if (!CheckUtils.isValidPassword(reNewPwd)) {
                ToastHelper.show(R.string.pwd_error_hint);
                return;
            }
            if (!newPwd.equals(reNewPwd)) {
                mTvErrorPop.setText(R.string.verify_pwd_error_hint);
                mLayPwdPop.setVisibility(View.VISIBLE);
                return;
            }
            ModifyPwdBody modifyBody = new ModifyPwdBody()
                    .setOld_password(oldPwd)
                    .setPassword(newPwd)
                    .setPassword_confirmation(reNewPwd);
            mPresenter.doResetPwd(modifyBody);
        }
    }

    @Override
    public void setPresenter(ModifyPwdContract.Presenter presenter) {
        this.mPresenter = presenter;
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
    public void onModifyPwdSuccess() {
        ToastHelper.show(R.string.setting_set_pwd_success_hint);
        AppUtil.logoutAndLaunchLoginActivity();
    }

    @Override
    public void onModifyPwdFailed(String error) {
        mTvErrorPop.setText(error);
        mLayPwdPop.setVisibility(View.VISIBLE);
        ToastHelper.show(error);
    }
}
