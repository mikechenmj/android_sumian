package com.sumian.app.account.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import com.sumian.app.R;
import com.sumian.app.account.contract.ModifyUserInfoContract;
import com.sumian.app.account.presenter.ModifyGenderPresenter;
import com.sumian.app.app.AppManager;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.improve.assessment.AssessmentUserInfoActivity;
import com.sumian.app.network.response.UserInfo;
import com.sumian.app.widget.BottomSheetView;
import com.sumian.app.widget.refresh.ActionLoadingDialog;

import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/10/5
 * <p>
 * desc:
 */

public class SelectGenderBottomSheet extends BottomSheetView implements View.OnClickListener, ModifyUserInfoContract.View<UserInfo> {

    private static final String TAG = SelectGenderBottomSheet.class.getSimpleName();

    private static final String FORM_KEY = "form_key";
    private static final String USER_KEY = "user_key";
    private static final String IS_ASSESSMENT_KEY = "is_assessment_key";

    private ModifyUserInfoContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    private String mFormKey;
    private UserInfo mUserInfo;
    private boolean mIsAssessment = false;

    public static SelectGenderBottomSheet newInstance(String formKey) {
        SelectGenderBottomSheet selectBottomSheet = new SelectGenderBottomSheet();
        Bundle args = new Bundle();
        args.putString(FORM_KEY, formKey);
        selectBottomSheet.setArguments(args);
        return selectBottomSheet;
    }

    public static BottomSheetView newInstance(String formKey, UserInfo userInfo, boolean isAssessment) {
        SelectGenderBottomSheet selectBottomSheet = new SelectGenderBottomSheet();
        Bundle args = new Bundle();
        args.putString(FORM_KEY, formKey);
        args.putSerializable(USER_KEY, userInfo);
        args.putBoolean(IS_ASSESSMENT_KEY, isAssessment);
        selectBottomSheet.setArguments(args);
        return selectBottomSheet;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mFormKey = arguments.getString(FORM_KEY);
        this.mFormKey = arguments.getString(FORM_KEY);
        this.mUserInfo = (UserInfo) arguments.getSerializable(USER_KEY);
        this.mIsAssessment = arguments.getBoolean(IS_ASSESSMENT_KEY, false);
    }


    @Override
    protected int getLayout() {
        return R.layout.hw_lay_bottom_sheet_modify_gender;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

    }

    @Override
    protected void initData() {
        super.initData();
        ModifyGenderPresenter.init(this);
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
        this.mActionLoadingDialog = new ActionLoadingDialog().show(getFragmentManager());
        //runUiThread(() -> ToastHelper.show(R.string.refresh_user_info_ing_hint));
    }

    @Override
    public void onFinish() {
        this.mActionLoadingDialog.dismiss();
    }

    @Override
    public void onModifySuccess(UserInfo userInfo) {
        runUiThread(() -> {
            //ToastHelper.show(R.string.modify_user_info_success);
            AppManager.getAccountModel().updateUserCache(userInfo);
            dismiss();
        });
    }

    @Override
    public void onModifyFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }


    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.tv_male, R.id.tv_female, R.id.tv_cancel})
    public void onClick(View view) {
        String gender = null;
        switch (view.getId()) {
            case R.id.tv_male:
                gender = "male";
                break;
            case R.id.tv_female:
                gender = "female";
                break;
            // case R.id.tv_secrecy:
            //    gender = "secrecy";
            //   break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(gender)) return;

        if (mIsAssessment) {
            mUserInfo.setGender(gender);
            Intent intent = new Intent(AssessmentUserInfoActivity.ACTION_MODIFY_ASSESSMENT_USER_INFO);
            intent.putExtra(AssessmentUserInfoActivity.EXTRA_ASSESSMENT_USER_INFO, mUserInfo);
            boolean sendBroadcast = LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            if (sendBroadcast) {
                dismiss();
            }

        } else {
            mPresenter.doModifyUserInfo(mFormKey, gender);
        }
    }

}
