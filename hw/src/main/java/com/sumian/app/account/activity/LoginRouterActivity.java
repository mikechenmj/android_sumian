package com.sumian.app.account.activity;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sumian.app.R;
import com.sumian.app.account.contract.OpenLoginContract;
import com.sumian.app.account.presenter.OpenLoginPresenter;
import com.sumian.app.app.AppManager;
import com.sumian.app.app.App;
import com.sumian.app.app.delegate.ApplicationDelegate;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.network.response.Token;
import com.sumian.app.widget.refresh.ActionLoadingDialog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class LoginRouterActivity extends BaseActivity implements View.OnClickListener, UMAuthListener, OpenLoginContract.View {

    private static final String TAG = LoginRouterActivity.class.getSimpleName();

    @BindView(R.id.iv_logo)
    ImageView mIvLogo;

    @BindView(R.id.bt_login)
    Button mBtLogin;
    @BindView(R.id.tv_register)
    Button mBtRegister;

    private OpenLoginContract.Presenter mPresenter;

    private ActionLoadingDialog mActionLoadingDialog;

    public static void show(Context context) {
        Intent intent;

        if (context == null) {
            context = App.getAppContext();
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
    protected void initData() {
        super.initData();
        OpenLoginPresenter.init(this);
    }

    @OnClick({R.id.bt_login, R.id.tv_register, R.id.bt_wechat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                LoginActivity.show(this, true);
                break;
            case R.id.tv_register:
                RegisterActivity.show(this);
                break;
            case R.id.bt_wechat:
                mPresenter.doLoginOpen(SHARE_MEDIA.WEIXIN, this, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onFinish();
        ApplicationDelegate.setIsLoginActivity(false);
        ApplicationDelegate.exitApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data);
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
        AppManager.getOpenLogin().deleteWechatTokenCache(this, null);
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
        ApplicationDelegate.goHome(this);
        onFinish();
        finish();
    }

    @Override
    public void onNotBindCallback(String error, String openUserInfo) {
        OpenBindActivity.show(this, openUserInfo, SHARE_MEDIA.WEIXIN);
        // onFailure(error);
    }

}
