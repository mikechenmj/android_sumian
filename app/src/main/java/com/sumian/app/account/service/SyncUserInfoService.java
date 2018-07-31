package com.sumian.app.account.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sumian.app.account.contract.SyncUserInfoContract;
import com.sumian.app.account.presenter.SyncUserInfoPresenter;
import com.sumian.app.app.App;

/**
 * Created by jzz
 * on 2017/10/14.
 * desc:
 */

public class SyncUserInfoService extends IntentService {

    private static final String TAG = SyncUserInfoService.class.getSimpleName();

    private static final String ARGS_LOGIN_TYPE = "login_type";
    private static final String ARGS_IS_ONLY_SYNC = "is_only_sync";

    public static final int SUMIAN_LOGIN_TYPE = 0x00;
    public static final int OPEN_LOGIN_TYPE = 0x01;


    private SyncUserInfoContract.Presenter mPresenter;

    public SyncUserInfoService() {
        super(SyncUserInfoService.class.getSimpleName());
    }

    @SuppressWarnings("SameParameterValue")
    public static void startService(boolean isOnlySync) {
        Context context = App.getAppContext();
        Intent intent = new Intent(context, SyncUserInfoService.class);
        intent.putExtra(ARGS_IS_ONLY_SYNC, isOnlySync);
        context.startService(intent);
    }

    public static void startService(int loginType) {
        Context context = App.getAppContext();
        Intent intent = new Intent(context, SyncUserInfoService.class);
        intent.putExtra(ARGS_LOGIN_TYPE, loginType);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPresenter = SyncUserInfoPresenter.init();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int loginType = SUMIAN_LOGIN_TYPE;
        boolean isSync = false;
        if (intent != null) {
            loginType = intent.getIntExtra(ARGS_LOGIN_TYPE, SUMIAN_LOGIN_TYPE);
            isSync = intent.getBooleanExtra(ARGS_IS_ONLY_SYNC, false);
        }
        this.mPresenter.doSyncUserInfo(isSync, loginType);
        this.mPresenter.doSyncReminder();
        Log.e(TAG, "onHandleIntent: ------>");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: -------->");
    }
}
