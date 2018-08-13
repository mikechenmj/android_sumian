package com.sumian.hw.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.improve.feedback.FeedbackActivity;
import com.sumian.hw.improve.qrcode.activity.QrCodeActivity;
import com.sumian.hw.setting.sheet.LogoutBottomSheet;
import com.sumian.hw.upgrade.activity.VersionNoticeActivity;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/10/12.
 * <p>
 * updated by jzz
 * on 2018/08/13
 * <p>
 * desc:
 */

public class HwSettingActivity extends HwBaseActivity implements View.OnClickListener,
        TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    private int mCount;

    public static void show(Context context) {
        context.startActivity(new Intent(context, HwSettingActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        this.mTitleBar.addOnSpannerListener(v -> {
            mCount++;
            BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral != null && bluePeripheral.isConnected() && mCount >= 5) {
                DeviceLogActivity.show(this);
                mCount = 0;
            }
        });
        this.mTitleBar.addOnBackListener(this);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @OnClick({R.id.siv_hw_version, R.id.siv_change_bind, R.id.siv_feedback, R.id.siv_modify_password, R.id.tv_logout,})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.siv_hw_version:
                VersionNoticeActivity.show(v.getContext());
                break;
            case R.id.siv_change_bind:
                BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
                if (bluePeripheral == null || !bluePeripheral.isConnected()) {
                    ToastHelper.show(getString(R.string.not_show_monitor_todo));
                    return;
                }
                QrCodeActivity.show(this);
                break;
            case R.id.siv_feedback:
                FeedbackActivity.show(this);
                break;
            case R.id.siv_modify_password:
                ModifyPwdActivity.show(this);
                break;
            case R.id.tv_logout:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(LogoutBottomSheet.newInstance(), LogoutBottomSheet.class.getSimpleName())
                        .commit();
                break;
            default:
                break;
        }
    }

}