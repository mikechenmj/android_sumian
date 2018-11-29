package com.sumian.sd.base;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sumian.common.base.BaseActivityManager;
import com.sumian.common.base.IActivityDelegate;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sd.R;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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

    public static void show(Context context, Class<? extends SdBaseActivity> clx) {
        show(context, clx, null);
    }

    public static void show(Context context, Class<? extends SdBaseActivity> clx, Bundle extras) {
        Intent intent = new Intent(context, clx);
        if (extras != null) {
            intent.putExtras(extras);
        }
        show(context, intent);
    }

    public static void show(Context context, Intent intent) {
        if (context instanceof Application || context instanceof Service) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    public static void showClearTop(Context context, Class<? extends Activity> clx) {
        showClearTop(context, clx, null);
    }

    public static void showClearTop(Context context, Class<? extends Activity> clx, Bundle bundle) {
        Intent intent = new Intent(context, clx);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        showClearTop(context, intent);
    }

    public static void showClearTop(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, int color) {
//        // 设置状态栏透明
//        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        // 生成一个状态栏大小的矩形
//        View statusView = createStatusView(activity, color);
//        // 添加 statusView 到布局中
//        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//        decorView.addView(statusView);
//        // 设置根布局的参数
//        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
//        rootView.setFitsSystemWindows(true);
//        rootView.setClipToPadding(true);
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");

        float dimension = activity.getResources().getDimension(resourceId);

        int statusBarHeight = (int) (dimension <= 66.0f ? dimension + 4.0f : dimension + 0.5f);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

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
            mPresenter.release();
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
        if (backable()) {
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
            mPresenter.release();
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

    protected void showToast(String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastHelper.show(message);
        } else {
            runOnUiThread(() -> ToastHelper.show(message));
        }
    }

    protected void showToast(@StringRes int messageId) {
        showToast(getString(messageId));
    }

    protected void showCenterToast(String message) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread(() -> ToastHelper.show(this, message, Gravity.CENTER));
        } else {
            ToastHelper.show(this, message, Gravity.CENTER);
        }
    }

    protected void showCenterToast(@StringRes int messageId) {
        showCenterToast(getString(messageId));
    }

    protected boolean openEventBus() {
        return false;
    }

    protected boolean backable() {
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
