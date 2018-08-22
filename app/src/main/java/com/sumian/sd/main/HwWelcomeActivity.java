package com.sumian.sd.main;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.improve.guideline.activity.UserGuidelineActivity;
import com.sumian.hw.improve.guideline.utils.GuidelineUtils;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.utils.AppUtil;
import com.sumian.sd.R;
import com.sumian.sd.account.login.LoginActivity;
import com.sumian.sd.app.AppManager;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class HwWelcomeActivity extends HwBaseActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, HwWelcomeActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_welcome;
    }

    @Override
    protected void initData() {
        super.initData();
        LogManager.appendUserOperationLog("用户启动 app.......");
        runUiThread(() -> {
            if (GuidelineUtils.needShowWelcomeUserGuide()) {
                UserGuidelineActivity.show(this);
            } else {
                boolean login = AppManager.getAccountViewModel().isLogin();
                if (login) {
                    AppUtil.launchMain();
                    boolean launchCustomerServiceActivity = getIntent().getBooleanExtra("key_launch_online_customer_service_activity", false);
                    if (launchCustomerServiceActivity) {
                        HwLeanCloudHelper.checkLoginEasemob(HwLeanCloudHelper::startEasemobChatRoom);
                    }
                } else {
                    ActivityUtils.startActivity(LoginActivity.class);
                }
            }
        }, 500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 之所以在onStop finish，是因为在启动MainActivity 时马上finish 会导致无法执行Activity转场动画，会显示APP下面的内容。
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
