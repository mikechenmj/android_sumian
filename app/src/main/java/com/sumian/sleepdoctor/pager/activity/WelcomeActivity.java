package com.sumian.sleepdoctor.pager.activity;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.userProfile.ImproveUserProfileOneActivity;
import com.sumian.sleepdoctor.account.login.LoginActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.app.delegate.OtherDelegate;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

public class WelcomeActivity extends BaseActivity implements OtherDelegate {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_welcome;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        setStatusBar();
    }

    @Override
    protected void initData() {
        super.initData();
        AppManager.getAccountViewModel().getLiveDataToken().observe(this, token -> mRoot.postDelayed(() -> {
            if (token == null) {
                LoginActivity.show(WelcomeActivity.this, LoginActivity.class);
            } else if (token.is_new) {
                ImproveUserProfileOneActivity.show(WelcomeActivity.this, ImproveUserProfileOneActivity.class);
            } else {
                MainActivity.show(WelcomeActivity.this, MainActivity.class);
            }
            finish();
        }, 50));
    }
}
