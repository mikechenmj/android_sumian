package com.sumian.sd.main;

import android.annotation.SuppressLint;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.improve.main.HwWelcomeActivity;
import com.sumian.hw.utils.AppUtil;
import com.sumian.sd.R;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.account.userProfile.activity.ImproveUserProfileOneActivity;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.base.SdBaseActivity;

/**
 * @author Created by jzz
 * on 2018/1/16.
 * desc:
 */

@SuppressLint("Registered")
public class SdWelcomeActivity extends SdBaseActivity {

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
                ActivityUtils.startActivity(HwLoginActivity.class);
            } else if (token.is_new) {
                ImproveUserProfileOneActivity.show(SdWelcomeActivity.this, ImproveUserProfileOneActivity.class);
            } else {
                AppUtil.launchMain();
            }
            finish();
        }, WELCOME_SHOW_TIME);
    }
}
