package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.widget.TitleBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/2/9.
 * desc:
 */

public class ModifyNicknameActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackClickListener {

    public static final String EXTRA_MODIFY_NAME = "EXTRA_MODIFY_NAME";
    public static final int MODIFY_NICKNAME = 0x01;
    public static final int MODIFY_NAME = 0x02;

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.et_name)
    TextInputEditText mEtName;

    private int mModifyType = 0x01;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mModifyType = bundle.getInt(EXTRA_MODIFY_NAME, MODIFY_NICKNAME);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_modify_nickname;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        mTvLabel.setText(mModifyType == MODIFY_NICKNAME ? R.string.nickname : R.string.real_name);
        mEtName.setHint(mModifyType == MODIFY_NICKNAME ? R.string.input_nickname : R.string.input_real_name);
        mEtName.setHint(mModifyType == MODIFY_NICKNAME ? AppManager.getAccountViewModel().getUserProfile().nickname : AppManager.getAccountViewModel().getUserProfile().name);
    }

    @OnClick(R.id.bt_finish)
    @Override
    public void onClick(View v) {
        String input = mEtName.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            showToast("请输入有效的信息");
            return;
        }

        modifyName(input);
    }

    private void modifyName(String input) {
        Map<String, String> map = new HashMap<>();
        map.put(mModifyType == MODIFY_NICKNAME ? "nickname" : "name", input);
        AppManager
                .getHttpService()
                .modifyUserProfile(map)
                .enqueue(new BaseResponseCallback<UserProfile>() {
                    @Override
                    protected void onSuccess(UserProfile response) {
                        AppManager.getAccountViewModel().updateUserProfile(response);
                        finish();
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {
                        showToast(errorResponse.message);
                    }

                });
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}
