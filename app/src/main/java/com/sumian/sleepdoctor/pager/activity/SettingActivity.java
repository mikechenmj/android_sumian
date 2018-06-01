package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.view.View;

import com.sumian.common.operator.AppOperator;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.cache.AccountCache;
import com.sumian.sleepdoctor.account.login.LoginActivity;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import butterknife.BindView;
import butterknife.OnClick;
import kotlin.Unit;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public class SettingActivity extends BaseActivity implements TitleBar.OnBackListener, View.OnClickListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.version)
    SettingDividerView mVersion;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_setting;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
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

                break;
            case R.id.about_me:
                Bundle extras = new Bundle();
                extras.putInt(ConfigActivity.ARGS_CONFIG_TYPE, ConfigActivity.ABOUT_ME);
                ConfigActivity.show(this, ConfigActivity.class, extras);
                break;
            case R.id.bt_logout:
                AppManager.getHttpService().logout().enqueue(new BaseResponseCallback<Unit>() {
                    @Override
                    protected void onSuccess(Unit response) {

                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {

                    }

                });
                //                    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                    if (activityManager != null) {
//boolean applicationUserData = activityManager.clearApplicationUserData();
//  if (applicationUserData)
// }
                AppOperator.runOnThread(AccountCache::clearCache);
                AppManager.getGroupViewModel().notifyGroups(null);
                AppManager.getChatEngine().logoutImServer();
                AppManager.getAccountViewModel().updateToken(null);
                LoginActivity.showClearTop(this, LoginActivity.class);
                break;
            default:
                break;
        }
    }
}
