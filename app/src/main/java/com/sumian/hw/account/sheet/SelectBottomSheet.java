package com.sumian.hw.account.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.hw.account.contract.ModifySelectContract;
import com.sumian.hw.account.contract.ModifyUserInfoContract;
import com.sumian.hw.account.presenter.ModifySelectPresenter;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.improve.assessment.AssessmentUserInfoActivity;
import com.sumian.hw.widget.BottomSheetView;
import com.sumian.hw.widget.refresh.ActionLoadingDialog;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;

import java.util.Calendar;
import java.util.Locale;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by jzz
 * on 2017/10/5
 * <p>
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class SelectBottomSheet extends BottomSheetView implements View.OnClickListener,
        ModifySelectContract.View<UserInfo>, NumberPickerView.OnValueChangeListener, NumberPickerView.OnScrollListener {

    @SuppressWarnings("unused")
    private static final String TAG = SelectBottomSheet.class.getSimpleName();

    private static final String FORM_KEY = "form_key";
    private static final String USER_KEY = "user_key";
    private static final String IS_ASSESSMENT_KEY = "is_assessment_key";


    NumberPickerView mPickerViewOne;
    NumberPickerView mPickerViewTwo;
    NumberPickerView mPickerViewThree;
    TextView mTvOk;

    private String mFormKey;

    private ModifySelectContract.Presenter mPresenter;
    private UserInfo mUserInfo;
    private ActionLoadingDialog mActionLoadingDialog;

    private boolean mIsAssessment = false;

    public static SelectBottomSheet newInstance(String formKey, UserInfo userInfo) {
        SelectBottomSheet selectBottomSheet = new SelectBottomSheet();
        Bundle args = new Bundle();
        args.putString(FORM_KEY, formKey);
        args.putParcelable(USER_KEY, userInfo);
        selectBottomSheet.setArguments(args);
        return selectBottomSheet;
    }

    public static SelectBottomSheet newInstance(String formKey, UserInfo userInfo, boolean isAssessment) {
        SelectBottomSheet selectBottomSheet = new SelectBottomSheet();
        Bundle args = new Bundle();
        args.putString(FORM_KEY, formKey);
        args.putParcelable(USER_KEY, userInfo);
        args.putBoolean(IS_ASSESSMENT_KEY, isAssessment);
        selectBottomSheet.setArguments(args);
        return selectBottomSheet;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mFormKey = arguments.getString(FORM_KEY);
        this.mUserInfo = (UserInfo) arguments.getSerializable(USER_KEY);
        this.mIsAssessment = arguments.getBoolean(IS_ASSESSMENT_KEY, false);
    }


    @Override
    protected int getLayout() {
        return R.layout.hw_lay_bottom_sheet_modify_user_info;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mPickerViewOne = rootView.findViewById(R.id.picker_one);
        mPickerViewTwo = rootView.findViewById(R.id.picker_two);
        mPickerViewThree = rootView.findViewById(R.id.picker_three);
        mTvOk = rootView.findViewById(R.id.tv_ok);

        rootView.findViewById(R.id.tv_ok).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);

        mPickerViewOne.setOnValueChangedListener(this);
        mPickerViewOne.setOnScrollListener(this);
        mPickerViewTwo.setOnValueChangedListener(this);
        mPickerViewTwo.setOnScrollListener(this);
        mPickerViewThree.setOnScrollListener(this);
        ModifySelectPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.transformFormKey(mFormKey, mUserInfo);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_ok) {
            String oneValue = mPickerViewOne.getContentByCurrValue();
            if (TextUtils.isEmpty(oneValue)) {
                return;
            }
            String twoValue = mPickerViewTwo.getContentByCurrValue();
            if (TextUtils.isEmpty(twoValue)) {
                return;
            }
            String threeValue = mPickerViewThree.getContentByCurrValue();
            //if (TextUtils.isEmpty(threeValue)) return;

            if (mIsAssessment) {
                Object value = mPresenter.transformFormValue(mFormKey, oneValue, twoValue, threeValue);
                switch (mFormKey) {
                    case ModifyUserInfoContract.KEY_BIRTHDAY:
                        mUserInfo.setBirthday((String) value);
                        break;
                    case ModifyUserInfoContract.KEY_HEIGHT:
                        mUserInfo.setHeight(String.format(Locale.getDefault(), "%.1f", value));
                        break;
                    case ModifyUserInfoContract.KEY_WEIGHT:
                        mUserInfo.setWeight(String.format(Locale.getDefault(), "%.1f", value));
                        break;
                    default:
                        break;
                }

                Intent intent = new Intent(AssessmentUserInfoActivity.ACTION_MODIFY_ASSESSMENT_USER_INFO);
                intent.putExtra(AssessmentUserInfoActivity.EXTRA_ASSESSMENT_USER_INFO, mUserInfo);
                boolean sendBroadcast = LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                if (sendBroadcast) {
                    dismiss();
                }
            } else {
                mPresenter.doModifyUserInfo(mFormKey, mPresenter.transformFormValue(mFormKey, oneValue, twoValue, threeValue));
            }
        }
    }

    @Override
    public void setPresenter(ModifyUserInfoContract.Presenter presenter) {
        this.mPresenter = (ModifySelectContract.Presenter) presenter;
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        mActionLoadingDialog = new ActionLoadingDialog().show(getFragmentManager());
        // runUiThread(() -> ToastHelper.show(R.string.refresh_user_info_ing_hint));
    }

    @Override
    public void onFinish() {
        mActionLoadingDialog.dismiss();
    }

    @Override
    public void onModifySuccess(UserInfo userInfo) {
        runUiThread(this::dismiss);
        HwAppManager.getAccountModel().updateUserInfo(userInfo);
    }

    @Override
    public void onModifyFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void transformOneDisplayedValues(int currentPosition, String hintText, String[]
            displayedValues) {
        runUiThread(() -> {
            mPickerViewOne.refreshByNewDisplayedValues(displayedValues);
            mPickerViewOne.setPickedIndexRelativeToRaw(currentPosition);
            mPickerViewOne.setHintText(hintText);
            mPickerViewOne.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void transformTwoDisplayedValues(int currentPosition, String hintText, String[] displayedValues) {
        runUiThread(() -> {
            mPickerViewTwo.refreshByNewDisplayedValues(displayedValues);
            mPickerViewTwo.setPickedIndexRelativeToRaw(currentPosition);
            mPickerViewTwo.setHintText(hintText);
            mPickerViewTwo.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void transformThreeDisplayedValues(int currentPosition, String hintText, String[] displayedValues) {
        runUiThread(() -> {
            mPickerViewThree.refreshByNewDisplayedValues(displayedValues);
            //mPickerViewOne.setValue(currentPosition);
            mPickerViewThree.setHintText(hintText);
            mPickerViewThree.setVisibility(View.VISIBLE);
            mTvOk.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        int id = picker.getId();
        switch (mFormKey) {
            case ModifyUserInfoContract.KEY_BIRTHDAY:
                int year;
                if (id == R.id.picker_one) {
                    year = Integer.parseInt(picker.getContentByCurrValue(), 10);
                    if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                        int monthCount = Calendar.getInstance().get(Calendar.MONTH) + 1;
                        String[] months = new String[monthCount];
                        for (int i = 0; i < months.length; i++) {
                            months[i] = String.format(Locale.getDefault(), "%02d", i + 1);
                        }
                        mPickerViewTwo.refreshByNewDisplayedValues(months);
                    } else {
                        String[] months = new String[12];
                        for (int i = 0; i < months.length; i++) {
                            months[i] = String.format(Locale.getDefault(), "%02d", i + 1);
                        }
                        mPickerViewTwo.refreshByNewDisplayedValues(months);
                    }
                }
                break;
            case ModifyUserInfoContract.KEY_AREA:
                String province;
                if (id == R.id.picker_one) {
                    province = picker.getContentByCurrValue();
                    mPresenter.transformCityForProvince(province);
                } else if (id == R.id.picker_one) {
                    String city = picker.getContentByCurrValue();
                    mPresenter.transformAreaForCity(city);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onScrollStateChange(NumberPickerView view, int scrollState) {
        runUiThread(() -> {
            int id = view.getId();
            if (id == R.id.picker_one || id == R.id.picker_two || id == R.id.picker_three) {
                if (scrollState != 0) {
                    mTvOk.setVisibility(View.INVISIBLE);
                } else {
                    mTvOk.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
