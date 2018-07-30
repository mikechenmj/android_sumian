package com.sumian.app.setting.sheet;

import android.view.Gravity;
import android.view.View;

import com.sumian.app.R;
import com.sumian.app.account.activity.LoginRouterActivity;
import com.sumian.app.app.App;
import com.sumian.app.app.AppManager;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.setting.contract.LogoutContract;
import com.sumian.app.setting.presenter.LogoutPresenter;
import com.sumian.app.widget.BottomSheetView;
import com.sumian.app.widget.refresh.ActionLoadingDialog;
import com.sumian.blue.model.BluePeripheral;

import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public class LogoutBottomSheet extends BottomSheetView implements View.OnClickListener, LogoutContract.View {

    private LogoutContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;


    public static LogoutBottomSheet newInstance() {
        return new LogoutBottomSheet();
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_bottom_sheet_logout;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_logout).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        LogoutPresenter.init(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_logout) {
            BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral != null && bluePeripheral.isConnected()) {
                bluePeripheral.disconnect();
                bluePeripheral.close();
            }
            mPresenter.doLogout();
            dismiss();
        } else if (i == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void onLogoutSuccess() {
        LoginRouterActivity.show(getContext());
        dismiss();
        ToastHelper.show(App.getAppContext(), App.getAppContext().getResources().getString(R.string.logout_success_hint), Gravity.CENTER);
    }

    @Override
    public void onLogoutFailed(String error) {
        dismiss();
        onFailure(error);
    }

    @Override
    public void setPresenter(LogoutContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        ToastHelper.show(getContext(), error, Gravity.CENTER);
    }

    @Override
    public void onBegin() {
        mActionLoadingDialog = new ActionLoadingDialog().show(getFragmentManager());
    }

    @Override
    public void onFinish() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }
}
