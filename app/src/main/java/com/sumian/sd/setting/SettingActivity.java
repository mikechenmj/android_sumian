package com.sumian.sd.setting;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.avos.avoscloud.AVInstallation;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.sd.setting.remind.RemindSettingActivity;
import com.sumian.sd.utils.AppUtil;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.h5.SimpleWebActivity;
import com.sumian.sd.network.callback.BaseResponseCallback;
import com.sumian.sd.setting.version.VersionActivity;
import com.sumian.sd.utils.UiUtils;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingActivity extends SdBaseActivity implements TitleBar.OnBackClickListener, View.OnClickListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.version)
    SettingDividerView mSdvVersion;

    private BottomSheetDialog dialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_setting;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        invalidVersion();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @OnClick({R.id.version, R.id.about_me, R.id.bt_logout, R.id.sdv_remind})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_remind:
                ActivityUtils.startActivity(RemindSettingActivity.class);
                break;
            case R.id.version:
                VersionActivity.show(v.getContext(), VersionActivity.class);
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

    private void invalidVersion() {
        PackageInfo packageInfo = UiUtils.getPackageInfo(this);
        String versionName = packageInfo.versionName;
        mSdvVersion.setContent(versionName);
    }

    private void showLogoutDialog() {
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(this).inflate(R.layout.lay_bottom_sheet_logout, null, false);
            inflate.findViewById(R.id.tv_logout).setOnClickListener(v -> {
                logout();
                dialog.dismiss();
            });
            inflate.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
            dialog.setContentView(inflate);
            dialog.setCanceledOnTouchOutside(true);
        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void logout() {
        Call<Unit> call = AppManager.getHttpService().logout(AVInstallation.getCurrentInstallation().getInstallationId());
        addCall(call);
        call.enqueue(new BaseResponseCallback<Unit>() {
            @Override
            protected void onSuccess(Unit response) {
                AppUtil.logoutAndLaunchLoginActivity();
            }

            @Override
            protected void onFailure(int code, @NonNull String message) {
                ToastUtils.showShort(R.string.logout_failed_please_check_network);
            }
        });
    }
}
