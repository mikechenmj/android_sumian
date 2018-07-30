package com.sumian.app.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.account.contract.ModifyUserInfoContract;
import com.sumian.app.account.presenter.ModifyNickNamePresenter;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.network.response.HwUserInfo;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.refresh.ActionLoadingDialog;


/**
 * Created by jzz
 * on 2017/10/26.
 * <p>
 * desc:
 */

public class ModifyNicknameActivity extends BaseActivity implements TitleBar.OnMoreListener, TitleBar.OnBackListener,
    ModifyUserInfoContract.View<HwUserInfo> {

    public static final String MODIFY_TYPE = "modify_type";

    public static final int NICKNAME_TYPE = 0x01;
    public static final int CAREER_TYPE = 0x02;

    TitleBar mTitleBar;
    FrameLayout mAdapterPop;
    TextView mTvPopError;
    EditText mEtMobile;
    TextView mTvModifyLabel;
    TextView mTvWarnLabel;

    private ModifyUserInfoContract.Presenter mPresenter;
    private ActionLoadingDialog mActionLoadingDialog;
    private int mType = NICKNAME_TYPE;

    public static void show(Context context, int type) {

        Intent intent = new Intent(context, ModifyNicknameActivity.class);
        intent.putExtra(MODIFY_TYPE, type);

        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mType = bundle.getInt(MODIFY_TYPE, NICKNAME_TYPE);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_modify_nickname;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mAdapterPop= findViewById(R.id.lay_pop);
        mTvPopError= findViewById(R.id.tv_pop_error);
        mEtMobile= findViewById(R.id.et_nickname);
        mTvModifyLabel= findViewById(R.id.tv_modify_label);
        mTvWarnLabel= findViewById(R.id.tv_warn_label);

        if (mType == NICKNAME_TYPE) {
            this.mEtMobile.setHint(R.string.new_nickname_hint);
            this.mTvModifyLabel.setText(R.string.nickname_hint);
            this.mTitleBar.setTitle(R.string.nickname_hint);
            this.mTvWarnLabel.setText(R.string.nickname_warn_label);
        } else {
            this.mEtMobile.setHint(R.string.input_career_hint);
            this.mTvModifyLabel.setText(R.string.career_hint);
            this.mTitleBar.setTitle(R.string.career_hint);
            this.mTvWarnLabel.setText(R.string.career_warn_label);
            this.mEtMobile.setMaxEms(12);
        }
        this.mTitleBar.showMoreIcon(R.mipmap.ic_nav_ok).addOnMoreListener(this).addOnBackListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        ModifyNickNamePresenter.init(this);
    }

    @Override
    public void onMore(View v) {

        String nickname = mEtMobile.getText().toString().trim();

        this.mAdapterPop.setVisibility(View.GONE);

        if (TextUtils.isEmpty(nickname) || "".equals(nickname)) {
            if (mType == NICKNAME_TYPE) {
                ToastHelper.show(R.string.new_nickname_error_hint);
                return;
            } else {
                //ToastHelper.show(getString(R.string.input_career_error));
                nickname = "";
            }
        }

        if (nickname.length() < 2) {
            if (mType == NICKNAME_TYPE) {
                this.mAdapterPop.setVisibility(View.VISIBLE);
                this.mTvPopError.setText(R.string.new_nickname_less_error);
                return;
            }
        }

        if (mType == NICKNAME_TYPE) {
            if (nickname.length() > 24) {
                this.mAdapterPop.setVisibility(View.VISIBLE);
                this.mTvPopError.setText(R.string.new_nickname_too_much_more_error);
                return;
            }
        } else {
            if (nickname.length() > 12) {
                this.mAdapterPop.setVisibility(View.VISIBLE);
                this.mTvPopError.setText(R.string.new_areer_too_much_more_error);
                return;
            }
        }

        UiUtil.closeKeyboard(mEtMobile);

        mPresenter.doModifyUserInfo(mType == NICKNAME_TYPE ? ModifyUserInfoContract.KEY_NICKNAME : ModifyUserInfoContract.KEY_CAREER, nickname);

    }

    @Override
    public void onBack(View v) {
        finish();
    }


    @Override
    public void setPresenter(ModifyUserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
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
    public void onModifySuccess(HwUserInfo userInfo) {
        runUiThread(() -> {
            ToastHelper.show(R.string.modify_user_info_success);
            AppManager.getAccountModel().updateUserCache(userInfo);
            UiUtil.closeKeyboard(mEtMobile);
            finish();
        });
    }

    @Override
    public void onModifyFailed(String error) {
        runUiThread(() -> {
            this.mAdapterPop.setVisibility(View.VISIBLE);
            this.mTvPopError.setText(error);
            UiUtil.showSoftKeyboard(mEtMobile);
        });
    }
}
