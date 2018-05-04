package com.sumian.sleepdoctor.base;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jaeger.library.StatusBarUtil;
import com.sumian.common.helper.ToastHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/4/left.
 * <p>
 * desc:
 */

public abstract class BaseActivity<Presenter extends BasePresenter> extends AppCompatActivity implements LifecycleOwner, DefaultLifecycleObserver {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Unbinder mBind;

    protected View mRoot;

    protected Presenter mPresenter;

    public static void show(Context context, Class<? extends BaseActivity> clx) {
        show(context, clx, null);
    }

    public static void show(Context context, Class<? extends BaseActivity> clx, Bundle extras) {
        Intent intent = new Intent(context, clx);
        if (extras != null)
            intent.putExtras(extras);

        if (context instanceof Application || context instanceof Service) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    public static void showClearTop(Context context, Class<? extends BaseActivity> clx) {
        Intent intent = new Intent(context, clx);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!initBundle(intent.getExtras())) {
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (initBundle(getIntent().getExtras())) {
            setContentView(getLayoutId());
            initWindow();
            this.mBind = ButterKnife.bind(this);
            this.mRoot = getWindow().getDecorView();
            initWidget(mRoot);
            initPresenter();
            initData();
            getLifecycle().addObserver(this);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRelease();
        getLifecycle().removeObserver(this);
        this.mBind.unbind();
        this.mRoot = null;
    }

    public void setStatusBar() {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setTranslucent(this, 0);
    }

    protected boolean initBundle(Bundle bundle) {

        return true;
    }

    protected void initWindow() {

    }

    protected abstract int getLayoutId();

    protected void initWidget(View root) {

    }

    protected void initPresenter() {

    }

    protected void initData() {

    }

    protected void onRelease() {
        mPresenter.release();
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

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, int color) {
        // 设置状态栏透明
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 生成一个状态栏大小的矩形
        View statusView = createStatusView(activity, color);
        // 添加 statusView 到布局中
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(statusView);
        // 设置根布局的参数
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
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

    protected void showToast(String message) {
        runOnUiThread(() -> ToastHelper.show(message));
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
}
