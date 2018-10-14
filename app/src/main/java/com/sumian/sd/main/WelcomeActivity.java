package com.sumian.sd.main;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.blankj.utilcode.util.ActivityUtils;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.common.base.BaseActivity;
import com.sumian.hw.log.LogManager;
import com.sumian.sd.R;
import com.sumian.sd.account.login.LoginActivity;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.kefu.KefuManager;
import com.sumian.sd.leancloud.LeanCloudManager;
import com.sumian.sd.theme.three.SkinConfig;
import com.sumian.sd.utils.AppUtil;
import com.sumian.sd.utils.StatusBarUtil;


/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class WelcomeActivity extends BaseActivity {

    private static final int SPLASH_DURATION = 500;

    public static void show(Context context) {
        context.startActivity(new Intent(context, WelcomeActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_welcome;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        StatusBarUtil.Companion.setStatusBarTextColorDark(this, true);
    }

    @Override
    protected void initData() {
        super.initData();
        LogManager.appendUserOperationLog("用户启动 app.......");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            boolean login = AppManager.getAccountViewModel().isLogin();
            if (login) {
                LeanCloudManager.uploadPushId();
                AppUtil.launchMain();
                boolean launchCustomerServiceActivity = getIntent().getBooleanExtra("key_launch_online_customer_service_activity", false);
                if (launchCustomerServiceActivity) {
                    UIProvider.getInstance().setThemeMode(SkinConfig.isInNightMode(App.getAppContext()) ? 0x02 : 0x01);
                    KefuManager.Companion.launchKefuActivity();
                }
            } else {
                ActivityUtils.startActivity(LoginActivity.class);
            }
        }, SPLASH_DURATION);
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
