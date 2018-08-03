package com.sumian.hw.account.activity;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sumian.hw.account.contract.OpenLoginContract;
import com.sumian.hw.account.presenter.OpenLoginPresenter;
import com.sumian.sleepdoctor.app.HwApp;
import com.sumian.sleepdoctor.app.HwApplicationDelegate;
import com.sumian.sleepdoctor.app.HwAppManager;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.widget.refresh.ActionLoadingDialog;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class LoginRouterActivity extends BaseActivity implements View.OnClickListener, UMAuthListener, OpenLoginContract.View {

    private static final String TAG = LoginRouterActivity.class.getSimpleName();

    ImageView mIvLogo;
    Button mBtLogin;
    Button mBtRegister;

    private OpenLoginContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context) {
        Intent intent;

        if (context == null) {
            context = HwApp.getAppContext();
        }

        intent = new Intent(context, LoginRouterActivity.class);

        if (context instanceof Application || context instanceof Service) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_login_router;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mIvLogo = findViewById(R.id.iv_logo);
        mBtLogin = findViewById(R.id.bt_login);
        mBtRegister = findViewById(R.id.tv_register);

        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.bt_wechat).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        OpenLoginPresenter.init(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_login) {
            HwLoginActivity.show(this, true);
        } else if (id == R.id.tv_register) {
            RegisterActivity.show(this);
        } else if (id == R.id.bt_wechat) {
            mPresenter.doLoginOpen(SHARE_MEDIA.WEIXIN, this, this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onFinish();
        HwApplicationDelegate.setIsLoginActivity(false);
        HwApplicationDelegate.exitApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HwAppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        //  Log.e(TAG, "onBegin: -------->" + share_media);
        switch (share_media) {
            case WEIXIN:
                ToastHelper.show(R.string.opening_wechat);
                break;
        }
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        HwAppManager.getOpenLogin().deleteWechatTokenCache(this, null);
        //Log.e(TAG, "onComplete: --------->" + share_media + "  i=" + i + "   map=" + map.toString());
        onFinish();
        mPresenter.bindOpen(share_media, map);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        //Log.e(TAG, "onError: ----------->" + share_media + "  i=" + i + "  " + throwable.getMessage());
        // if (i == UMAuthListener.ACTION_AUTHORIZE) {
        switch (share_media) {
            case WEIXIN:
                ToastHelper.show(R.string.no_have_wechat);
                break;
        }
        onFinish();
        // }
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        // Log.e(TAG, "onCancel: --------->" + share_media + "  i=" + i);
        onFinish();
    }

    @Override
    public void setPresenter(OpenLoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onFailure(String error) {
        runUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBegin() {
        if (!isFinishing()) {
            mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
        }
    }

    @Override
    public void onFinish() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }

    @Override
    public void onBindOpenSuccess(Token token) {
        HwApplicationDelegate.goHome(this);
        onFinish();
        finish();
    }

    @Override
    public void onNotBindCallback(String error, String openUserInfo) {
        OpenBindActivity.show(this, openUserInfo, SHARE_MEDIA.WEIXIN);
        // onFailure(error);
    }

}
