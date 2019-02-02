package com.sumian.sd.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.KeyboardUtils;
import com.sumian.common.base.BaseActivityManager;
import com.sumian.common.base.IActivityDelegate;
import com.sumian.sd.R;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/4/left.
 * <p>
 * desc:
 */

public abstract class SdBaseActivity<Presenter extends SdBasePresenter> extends AppCompatActivity implements LifecycleOwner, DefaultLifecycleObserver {

    private static final String TAG = SdBaseActivity.class.getSimpleName();
    protected View mRoot;
    protected Presenter mPresenter;
    protected Activity mActivity;
    private Set<Call> mCalls = new HashSet<>();
    private LoadingDialog mLoadingDialog;
    private IActivityDelegate mActivityDelegate = BaseActivityManager.INSTANCE.createActivityDelegate(this);

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!initBundle(intent.getExtras())) {
            finish();
        }
        mActivityDelegate.onNewIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (initBundle(getIntent().getExtras())) {
            setContentView(getLayoutId());
            initWindow();
            this.mRoot = getWindow().getDecorView();
            initPresenter();
            initWidget(mRoot);
            initData();
            getLifecycle().addObserver(this);
        } else {
            finish();
        }
        mActivityDelegate.onCreate(savedInstanceState);
        ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0)
                .setOnClickListener(v -> KeyboardUtils.hideSoftInput(SdBaseActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (openEventBus()) {
            EventBus.getDefault().register(this);
        }
        mActivityDelegate.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityDelegate.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivityDelegate.onPause();
    }

    @Override
    protected void onStop() {
        if (openEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
        mActivityDelegate.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRelease();
        getLifecycle().removeObserver(this);
        //this.mBind.unbind();
        if (mPresenter != null) {
            mPresenter.onCleared();
        }
        this.mRoot = null;
        for (Call call : mCalls) {
            if (call.isExecuted()) {
                call.cancel();
            }
        }
        mCalls.clear();
        mActivityDelegate.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityDelegate.onActivityResult(requestCode, resultCode, data);
    }

    public void setStatusBar() {
//        StatusBarUtil.setTransparent(this);
//        StatusBarUtil.setTranslucent(this, 0);
    }

    protected boolean initBundle(Bundle bundle) {

        return true;
    }

    protected void initWindow() {

    }

    protected abstract int getLayoutId();

    protected void initWidget(View root) {
        if (showBackNav()) {
            TitleBar toolbar = findViewById(R.id.title_bar);
            if (toolbar != null) {
                toolbar.setOnBackClickListener(v -> finish());
            }
        }
    }

    protected void initPresenter() {

    }

    protected void initData() {

    }

    protected void onRelease() {
        if (mPresenter != null) {
            mPresenter.onCleared();
        }
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onCreate: -------->");
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onStart: --------->");
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onResume: -------->");
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onPause: ----------->");
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onStop: ----------->");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onDestroy: ----------->");
    }

    protected boolean openEventBus() {
        return false;
    }

    protected boolean showBackNav() {
        return false;
    }

    protected void addCall(Call call) {
        mCalls.add(call);
    }

    protected void removeCall(Call call) {
        mCalls.remove(call);
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }


}
