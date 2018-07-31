package com.sumian.app.improve.main;

import android.content.Context;
import android.content.Intent;

import com.sumian.sleepdoctor.R;
import com.sumian.app.account.activity.LoginRouterActivity;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.app.App;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.improve.guideline.activity.UserGuidelineActivity;
import com.sumian.app.improve.guideline.utils.GuidelineUtils;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.log.LogManager;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class WelcomeActivity extends BaseActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, WelcomeActivity.class));
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
                boolean login = HwAppManager.getAccountModel().isLogin();
                if (login) {
                    HomeActivity.show(App.getAppContext());
                    boolean launchCustomerServiceActivity = getIntent().getBooleanExtra("key_launch_online_customer_service_activity", false);
                    if (launchCustomerServiceActivity) {
                        LeanCloudHelper.checkLoginEasemob(LeanCloudHelper::startEasemobChatRoom);
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
