package com.sumian.sddoctor.account.sheet;

import android.view.Gravity;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sddoctor.R;
import com.sumian.sddoctor.account.contract.LogoutContract;
import com.sumian.sddoctor.account.presenter.LogoutPresenter;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.login.login.LoginActivity;
import com.sumian.sddoctor.widget.LoadingDialog;
import com.sumian.sddoctor.widget.sheet.AbstractBottomSheetView;


/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class LogoutBottomSheet extends AbstractBottomSheetView implements View.OnClickListener, LogoutContract.View {

    private LogoutContract.Presenter mPresenter;

    private LoadingDialog mActionLoadingDialog;


    public static LogoutBottomSheet newInstance() {
        return new LogoutBottomSheet();
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_logout).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }


    @Override
    protected int getLayout() {
        return R.layout.lay_bottom_sheet_logout;
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = LogoutPresenter.init(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_logout:
                mPresenter.doLogout();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLogoutSuccess() {
        showCenterToast(getString(R.string.logout_success));
        ActivityUtils.finishAllActivities();
        ActivityUtils.startActivity(LoginActivity.class);
        AppManager.INSTANCE.onLogout();
    }

    @Override
    public void onLogoutFailed(String error) {
        showCenterToast(error);
    }

    @Override
    public void showLoading() {
        if (mActionLoadingDialog == null) {
            mActionLoadingDialog = new LoadingDialog(getContext());
        }

        if (!mActionLoadingDialog.isShowing()) {
            mActionLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mActionLoadingDialog != null && mActionLoadingDialog.isShowing()) {
            mActionLoadingDialog.dismiss();
        }
    }

    private void showCenterToast(String text) {
        ToastHelper.show(getContext(), text, Gravity.CLIP_HORIZONTAL);
    }

}
