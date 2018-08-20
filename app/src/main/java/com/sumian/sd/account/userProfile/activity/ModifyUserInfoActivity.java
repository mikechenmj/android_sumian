package com.sumian.sd.account.userProfile.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.sd.account.config.SumianConfig;
import com.sumian.sd.account.userProfile.contract.ImproveUserProfileContract;
import com.sumian.sd.account.userProfile.presenter.ImproveUserProfilePresenter;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.utils.EditTextUtil;
import com.sumian.sd.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/2/9.
 * desc:修改用户需要自己填写的信息   e.g. 昵称,姓名,职业
 */

public class ModifyUserInfoActivity extends SdBaseActivity<ImproveUserProfileContract.Presenter> implements View.OnClickListener, TitleBar.OnBackClickListener, ImproveUserProfileContract.View {

    private static final String EXTRA_MODIFY = "com.sumian.sleepdoctor.extra.MODIFY";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.et_name)
    TextInputEditText mEtName;

    private String mModifyType;

    public static void show(Context context, String modifyType) {
        Bundle extras = new Bundle();
        extras.putString(EXTRA_MODIFY, modifyType);
        show(context, ModifyUserInfoActivity.class, extras);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mModifyType = bundle.getString(EXTRA_MODIFY, ImproveUserProfileContract.IMPROVE_NICKNAME_KEY);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_modify_nickname;
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        ImproveUserProfilePresenter.init(this);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
        if (mModifyType.equals(ImproveUserProfileContract.IMPROVE_NICKNAME_KEY)) {
            EditTextUtil.Companion.setMaxLength(mEtName, SumianConfig.NICK_NAME_LENGTH_MAX);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        initType();
    }

    @Override
    public void setPresenter(ImproveUserProfileContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void initType() {
        @StringRes int title = 0;
        @StringRes int hint = 0;
        String input = "";

        switch (mModifyType) {
            case ImproveUserProfileContract.IMPROVE_NICKNAME_KEY:
                title = R.string.nickname;
                hint = R.string.input_nickname;
                input = AppManager.getAccountViewModel().getUserInfo().nickname;
                break;
            case ImproveUserProfileContract.IMPROVE_NAME_KEY:
                title = R.string.real_name;
                hint = R.string.input_real_name;
                input = AppManager.getAccountViewModel().getUserInfo().name;
                break;
            case ImproveUserProfileContract.IMPROVE_CAREER_KEY:
                title = R.string.career;
                hint = R.string.input_career_hint;
                input = AppManager.getAccountViewModel().getUserInfo().career;
                break;
            default:
                break;
        }

        mTitleBar.setTitle(title);
        mTvLabel.setText(title);
        mEtName.setHint(hint);
        mEtName.setText(input);
        mEtName.setSelection(input.length());
    }

    @OnClick(R.id.bt_finish)
    @Override
    public void onClick(View v) {
        String input = mEtName.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            showCenterToast("请输入有效的信息");
            return;
        }

        mPresenter.improveUserProfile(mModifyType, input);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onImproveUserProfileSuccess() {
        finish();
    }

    @Override
    public void onFailure(String error) {
        showCenterToast(error);
    }
}
