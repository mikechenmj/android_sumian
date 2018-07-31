package com.sumian.sleepdoctor.main;

import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.hw.improve.main.HwWelcomeActivity;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.login.LoginActivity;
import com.sumian.sleepdoctor.account.userProfile.activity.ImproveUserProfileOneActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.leancloud.LeanCloudManager;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class SdWelcomeActivity extends BaseActivity {

    public static final int WELCOME_SHOW_TIME = 500;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_welcome;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        setStatusBar();
        ActivityUtils.startActivity(HwWelcomeActivity.class);
        finish();
    }

    @Override
    protected void initData() {
        super.initData();
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> {
            LogUtils.d(token);
            onToken(token);
        });
    }

    public void onToken(Token token) {
        mRoot.postDelayed(() -> {
            if (token == null) {
                LoginActivity.show(SdWelcomeActivity.this, LoginActivity.class);
            } else if (token.is_new) {
                ImproveUserProfileOneActivity.show(SdWelcomeActivity.this, ImproveUserProfileOneActivity.class);
            } else {
                MainActivity.show(SdWelcomeActivity.this, MainActivity.class);
            }
            finish();
        }, WELCOME_SHOW_TIME);
        if (token != null) {
            LeanCloudManager.getAndUploadCurrentInstallation();
        }
    }
}
