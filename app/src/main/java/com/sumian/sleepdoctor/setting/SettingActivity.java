package com.sumian.sleepdoctor.setting;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.avos.avoscloud.AVInstallation;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.h5.SimpleWebActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.setting.version.VersionActivity;
import com.sumian.sleepdoctor.utils.NotificationUtil;
import com.sumian.sleepdoctor.utils.UiUtils;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingActivity extends BaseActivity implements TitleBar.OnBackClickListener, View.OnClickListener {

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

    @OnClick({R.id.version, R.id.about_me, R.id.bt_logout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                NotificationUtil.Companion.cancelAllNotification(App.Companion.getAppContext());
                AppManager.getAccountViewModel().updateToken(null);
                AppManager.getOpenLogin().deleteWeiXinOauth(SettingActivity.this);
//                HwLoginActivity.showClearTop(SettingActivity.this, HwLoginActivity.class);
                ActivityUtils.finishAllActivities();
                ActivityUtils.startActivity(HwLoginActivity.class);
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                ToastUtils.showShort(R.string.logout_failed_please_check_network);
            }
        });
    }
}
