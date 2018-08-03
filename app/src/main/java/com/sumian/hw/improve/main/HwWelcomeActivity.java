package com.sumian.hw.improve.main;

import android.content.Context;
import android.content.Intent;

import com.sumian.hw.account.activity.LoginRouterActivity;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.improve.guideline.activity.UserGuidelineActivity;
import com.sumian.hw.improve.guideline.utils.GuidelineUtils;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class HwWelcomeActivity extends BaseActivity {

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
                    HwMainActivity.show(App.Companion.getAppContext());
                    boolean launchCustomerServiceActivity = getIntent().getBooleanExtra("key_launch_online_customer_service_activity", false);
                    if (launchCustomerServiceActivity) {
                        HwLeanCloudHelper.checkLoginEasemob(HwLeanCloudHelper::startEasemobChatRoom);
                    }
                } else {
                    LoginRouterActivity.show(this);
                }
            }
            finish();
        }, 500);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
