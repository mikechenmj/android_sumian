package com.sumian.sleepdoctor.pager.activity;

import android.annotation.SuppressLint;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.sumian.common.operator.AppOperator;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.cache.AccountCache;
import com.sumian.sleepdoctor.account.login.LoginActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.h5.SimpleWebActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingActivity extends BaseActivity implements TitleBar.OnBackClickListener, View.OnClickListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.version)
    SettingDividerView mVersion;
    private BottomSheetDialog dialog;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_setting;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @OnClick({R.id.version, R.id.about_me, R.id.bt_logout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.version:

                break;
            case R.id.about_me:
                SimpleWebActivity.launch(this, H5Uri.ABOUT_US);
                break;
            case R.id.bt_logout:
                showLogoutDialog();
                break;
            default:
                break;
        }
    }

    private void showLogoutDialog() {
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(this).inflate(R.layout.lay_logout_bottom_sheet, null, false);
            inflate.findViewById(R.id.tv_logout).setOnClickListener(v -> logout());
            inflate.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
            dialog.setContentView(inflate);
            dialog.setCanceledOnTouchOutside(true);
        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void logout() {
        AppManager.getHttpService().logout().enqueue(new BaseResponseCallback<Unit>() {
            @Override
            protected void onSuccess(Unit response) {

            }

            @Override
            protected void onFailure(ErrorResponse errorResponse) {

            }

        });
        AppOperator.runOnThread(AccountCache::clearCache);
        AppManager.getGroupViewModel().notifyGroups(null);
        AppManager.getChatEngine().logoutImServer();
        AppManager.getAccountViewModel().updateToken(null);
        LoginActivity.showClearTop(this, LoginActivity.class);
    }
}
