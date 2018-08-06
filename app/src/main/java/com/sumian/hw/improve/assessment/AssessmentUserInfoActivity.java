package com.sumian.hw.improve.assessment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.account.contract.ModifyUserInfoContract;
import com.sumian.hw.account.contract.UserInfoContract;
import com.sumian.hw.account.presenter.UserInfoPresenter;
import com.sumian.hw.account.sheet.SelectBottomSheet;
import com.sumian.hw.account.sheet.SelectGenderBottomSheet;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.widget.BottomSheetView;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sm
 * on 2018/3/15.
 * desc:
 */

public class AssessmentUserInfoActivity extends BaseActivity<UserInfoContract.Presenter> implements View.OnClickListener, TitleBar.OnBackListener, UserInfoContract.View {

    public static final String ACTION_MODIFY_ASSESSMENT_USER_INFO = "com.sumian.app.action_MODIFY_ASSESSMENT_USER_INFO";
    public static final String EXTRA_ASSESSMENT_USER_INFO = "com.sumian.app.extra.ASSESSMENT_USER_INFO";

    TitleBar mTitleBar;
    EditText mEtNickname;
    TextView mTvGender;
    TextView mTvBirthday;
    TextView mTvHeight;
    TextView mTvWeight;
    Button mBtSave;

    private UserInfo mUserInfo;

    private Boolean[] mIsBoss = new Boolean[5];

    public static void show(Context context) {
        context.startActivity(new Intent(context, AssessmentUserInfoActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_lack_user_info;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mEtNickname = findViewById(R.id.et_nickname);
        mTvGender = findViewById(R.id.tv_gender);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mTvHeight = findViewById(R.id.tv_height);
        mTvWeight = findViewById(R.id.tv_weight);
        mBtSave = findViewById(R.id.bt_save);

        findViewById(R.id.lay_gender).setOnClickListener(this);
        findViewById(R.id.lay_birthday).setOnClickListener(this);
        findViewById(R.id.lay_height).setOnClickListener(this);
        findViewById(R.id.lay_weight).setOnClickListener(this);
        findViewById(R.id.bt_save).setOnClickListener(this);

        mTitleBar.addOnBackListener(this);
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null) {
                updateUserInfoUI(token.user);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            if (token != null) {
                updateUserInfoUI(token.user);
            }
        });
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        UserInfoPresenter.init(this);
    }

    public void onClick(View v) {
        String formKey;
        int id = v.getId();
        if (id == R.id.lay_gender) {
            commitBottomSheet(SelectGenderBottomSheet.newInstance(ModifyUserInfoContract.KEY_GENDER, mUserInfo, true));

        } else if (id == R.id.lay_birthday) {
            formKey = ModifyUserInfoContract.KEY_BIRTHDAY;
            if (TextUtils.isEmpty(formKey)) {
                return;
            }
            commitBottomSheet(SelectBottomSheet.newInstance(formKey, mUserInfo, true));

        } else if (id == R.id.lay_height) {
            formKey = ModifyUserInfoContract.KEY_HEIGHT;
            if (TextUtils.isEmpty(formKey)) {
                return;
            }
            commitBottomSheet(SelectBottomSheet.newInstance(formKey, mUserInfo, true));

        } else if (id == R.id.lay_weight) {
            formKey = ModifyUserInfoContract.KEY_WEIGHT;
            if (TextUtils.isEmpty(formKey)) {
                return;
            }
            commitBottomSheet(SelectBottomSheet.newInstance(formKey, mUserInfo, true));

        } else if (id == R.id.bt_save) {
            int count = 0;
            int index = -1;
            for (int i = 0; i < mIsBoss.length; i++) {
                Boolean isBoss = mIsBoss[i];
                if (isBoss) {
                    count++;
                    index = i;
                }
            }

            if (count >= 2) {
                ToastHelper.show("请完成以上信息的填写");
                return;
            } else {
                if (index != -1) {
                    switch (index) {
                        case 0:
                            ToastHelper.show("请完成昵称信息的填写");
                            break;
                        case 1:
                            ToastHelper.show("请选择性别");
                            break;
                        case 2:
                            ToastHelper.show("请选择生日信息");
                            break;
                        case 3:
                            ToastHelper.show("请选择身高信息");
                            break;
                        case 4:
                            ToastHelper.show("请选择体重信息");
                            break;
                    }
                    return;
                }
            }

            mUserInfo.nickname = mEtNickname.getText().toString().trim();
            onSyncCacheUserInfoSuccess(mUserInfo);

            Map<String, Object> map = new HashMap<>(0);

            map.put("nickname", mUserInfo.nickname);
            map.put("gender", mUserInfo.gender);
            map.put("birthday", mUserInfo.birthday);
            map.put("height", mUserInfo.height);
            map.put("weight", mUserInfo.weight);
            map.put("include", "doctor");
            AppManager.getHwNetEngine().getHttpService().doModifyUserInfo(map).enqueue(new BaseResponseCallback<UserInfo>() {
                @Override
                protected void onSuccess(UserInfo response) {
                    onSyncCacheUserInfoSuccess(response);
                    AppManager.getAccountViewModel().updateUserInfo(response);
                    finish();
                }

                @Override
                protected void onFailure(String error) {
                    ToastHelper.show(error);
                }
            });
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    private void commitBottomSheet(BottomSheetView bottomSheetView) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(bottomSheetView, bottomSheetView.getClass().getSimpleName())
                .commit();
    }

    private void setText(String text, TextView textView) {
        if (textView != null) {
            textView.setText(TextUtils.isEmpty(text) || "".equals(text) ? getString(R.string.user_none_default_select_hint) : text);
        }
    }

    @Override
    public void onSyncCacheUserInfoSuccess(UserInfo userInfo) {
        updateUserInfoUI(userInfo);
    }

    private void updateUserInfoUI(UserInfo userInfo) {
        setText(userInfo.getNickname(), mEtNickname);
        mIsBoss[0] = TextUtils.isEmpty(userInfo.getNickname());
        mIsBoss[1] = TextUtils.isEmpty(userInfo.getGender()) || userInfo.getGender().equals("secrecy");
        mIsBoss[2] = TextUtils.isEmpty(userInfo.getBirthday());
        mIsBoss[3] = TextUtils.isEmpty(userInfo.getHeight());
        mIsBoss[4] = TextUtils.isEmpty(userInfo.getWeight());

        mTvGender.setText(mPresenter.formatGender(userInfo.getGender()));
        setText(userInfo.getBirthday(), mTvBirthday);
        String height = userInfo.getHeight();
        setText(height, mTvHeight);
        if (!TextUtils.isEmpty(height)) {
            mTvHeight.append(" cm");
        }

        String weight = userInfo.getWeight();
        setText(weight, mTvWeight);
        if (!TextUtils.isEmpty(weight)) {
            mTvWeight.append(" kg");
        }
        try {
            if (mUserInfo == null) {
                mUserInfo = userInfo.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSyncCacheUserInfoFailed(String error) {

    }

    @Override
    public void onStartSyncUserInfo() {

    }

    @Override
    public void onCompletedUserInfo() {

    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

}
