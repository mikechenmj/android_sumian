package com.sumian.sd.setting;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.avos.avoscloud.AVInstallation;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.hw.feedback.FeedbackActivity;
import com.sumian.hw.qrcode.activity.QrCodeActivity;
import com.sumian.hw.upgrade.activity.DeviceVersionNoticeActivity;
import com.sumian.sd.R;
import com.sumian.sd.account.login.ModifyPasswordActivity;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.h5.SimpleWebActivity;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.setting.remind.RemindSettingActivity;
import com.sumian.sd.setting.version.VersionActivity;
import com.sumian.sd.utils.AppUtil;
import com.sumian.sd.utils.UiUtils;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.dialog.SumianAlertDialog;
import com.sumian.sd.widget.divider.SettingDividerView;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingActivity extends SdBaseActivity implements TitleBar.OnBackClickListener, View.OnClickListener {

    TitleBar mTitleBar;
    SettingDividerView mSdvAppVersion;

    private BottomSheetDialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_setting;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnBackClickListener(this);
        mSdvAppVersion = findViewById(R.id.sdv_app_version);
        findViewById(R.id.sdv_app_version).setOnClickListener(this);
        findViewById(R.id.sdv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_logout).setOnClickListener(this);
        findViewById(R.id.sdv_remind).setOnClickListener(this);
        findViewById(R.id.sdv_device_version).setOnClickListener(this);
        findViewById(R.id.sdv_change_bind).setOnClickListener(this);
        findViewById(R.id.sdv_feedback).setOnClickListener(this);
        findViewById(R.id.sdv_modify_password).setOnClickListener(this);
        findViewById(R.id.sdv_clear_cache).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_remind:
                RemindSettingActivity.show();
                break;
            case R.id.sdv_app_version:
                VersionActivity.show(v.getContext(), VersionActivity.class);
                break;
            case R.id.sdv_about_us:
                SimpleWebActivity.launch(this, H5Uri.ABOUT_US);
                break;
            case R.id.sdv_device_version:
                DeviceVersionNoticeActivity.show(v.getContext());
                break;
            case R.id.sdv_change_bind:
                BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
                if (bluePeripheral == null || !bluePeripheral.isConnected()) {
                    ToastHelper.show(getString(R.string.please_connect_monitor_before_change_bind));
                    return;
                }

                new SumianAlertDialog(this)
                        .hideTopIcon(true)
                        .setCancelable(true)
                        .setCloseIconVisible(false)
                        .setTitle(R.string.are_sure_2_bind)
                        .setMessage("此功能适用于监测仪或速眠仪发生故障，更换设备后重新绑定速眠仪的操作，是否继续？")
                        .setLeftBtn(R.string.cancel, null)
                        .whitenLeft()
                        .setRightBtn(R.string.sure, v1 -> QrCodeActivity.show(SettingActivity.this))
                        .show();

                break;
            case R.id.sdv_feedback:
                FeedbackActivity.show(this);
                break;
            case R.id.sdv_modify_password:
                ActivityUtils.startActivity(ModifyPasswordActivity.class);
                break;
            case R.id.tv_logout:
                showLogoutDialog();
                break;
            case R.id.sdv_clear_cache:
                new SumianAlertDialog(this)
                        .hideTopIcon(true)
                        .setTitle(R.string.clear_cache)
                        .setMessage(R.string.clear_cache_hint)
                        .setLeftBtn(R.string.cancel, null)
                        .setRightBtn(R.string.confirm, v12 -> {
                            boolean b = FileUtils.deleteAllInDir(getCacheDir());
                            LogUtils.d(b);
                            ToastUtils.showShort(R.string.clear_success);
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    private void invalidVersion() {
        PackageInfo packageInfo = UiUtils.getPackageInfo(this);
        String versionName = packageInfo.versionName;
        mSdvAppVersion.setContent(versionName);
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
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null && bluePeripheral.isConnected()) {
            bluePeripheral.disconnect();
            bluePeripheral.close();
        }
        Call<Unit> call = AppManager.getSdHttpService().logout(AVInstallation.getCurrentInstallation().getInstallationId());
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<Unit>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                ToastUtils.showShort(R.string.logout_failed_please_check_network);
            }

            @Override
            protected void onSuccess(Unit response) {
                AppUtil.logoutAndLaunchLoginActivity();
            }
        });
    }
}
