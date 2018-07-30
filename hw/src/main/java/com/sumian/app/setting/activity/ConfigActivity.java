package com.sumian.app.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;

import com.sumian.app.R;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.network.response.ConfigInfo;
import com.sumian.app.setting.contract.ConfigContract;
import com.sumian.app.setting.presenter.ConfigPresenter;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.refresh.BlueRefreshView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class ConfigActivity extends BaseActivity implements TitleBar.OnBackListener, ConfigContract.View, SwipeRefreshLayout.OnRefreshListener {

    private static final String CONFIG_TYPE = "config_type";
    public static final int ABOUT_TYPE = 0x01;
    public static final int REGISTER_USER_AGREEMENT_TYPE = 0x02;
    public static final int REGISTER_PRIVACY_POLICY_TYPE = 0x03;

    TitleBar mTitleBar;
    BlueRefreshView mRefresh;
    WebView mWeb;

    private int mConfigType;

    private ConfigContract.Presenter mPresenter;

    public static void show(Context context, int configType) {
        Intent intent = new Intent(context, ConfigActivity.class);
        intent.putExtra(CONFIG_TYPE, configType);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mConfigType = bundle.getInt(CONFIG_TYPE, ABOUT_TYPE);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_config;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mRefresh = findViewById(R.id.refresh);
        mWeb = findViewById(R.id.web);

        ConfigPresenter.init(this);
        this.mTitleBar.addOnBackListener(this);
        @StringRes int resType;
        switch (mConfigType) {
            case ABOUT_TYPE:
                resType = R.string.setting_about_me_hint;
                break;
            case REGISTER_USER_AGREEMENT_TYPE:
                resType = R.string.register_rule_user_agreement_title;
                break;
            case REGISTER_PRIVACY_POLICY_TYPE:
                resType = R.string.register_rule_privacy_policy_title;
                break;
            default:
                resType = R.string.setting_about_me_hint;
                break;
        }
        this.mTitleBar.setTitle(resType);
        this.mRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.doSyncConfigInfo(mConfigType);
    }


    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void setPresenter(ConfigContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        runUiThread(() -> mRefresh.setRefreshing(true));
    }

    @Override
    public void onFinish() {
        runUiThread(() -> mRefresh.setRefreshing(false));
    }

    @Override
    public void onSyncConfigInfoSuccess(List<ConfigInfo> configs) {
        runUiThread(() -> {
            ConfigInfo configInfo = configs.get(2);
            mWeb.loadDataWithBaseURL(null, configInfo.getValue(), "text/html", "utf-8", null);
        });
    }

    @Override
    public void onSyncConfigInfoFailed(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onSyncUrl(String url) {
        runUiThread(() -> mWeb.loadUrl(url));
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
