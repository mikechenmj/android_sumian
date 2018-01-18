package com.sumian.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/4/left.
 * <p>
 * desc:
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();
    private Unbinder mBind;
    private View mRoot;
    private List<Runnable> mRunnables;

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    public static void StatusBarLightMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

    }

    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
     */
    public static void StatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressWarnings("unchecked")
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
                @SuppressLint("PrivateApi") Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception ignored) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception ignored) {

            }
        }
        return result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarDarkMode(this, StatusBarLightMode(this));
        if (initBundle(getIntent().getExtras())) {
            setContentView(getLayoutId());
            initWindow();
            this.mBind = ButterKnife.bind(this);
            this.mRoot = getWindow().getDecorView();
            initWidget();
            initData();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        onRelease();
        this.mBind.unbind();
        List<Runnable> runnableList = this.mRunnables;
        if (runnableList != null && !runnableList.isEmpty()) {
            for (Runnable runnable : mRunnables) {
                this.mRoot.removeCallbacks(runnable);
            }
        }
        this.mRunnables = null;
        this.mRoot = null;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!initBundle(intent.getExtras())) {
            finish();
        }
    }

    protected void initWindow() {

    }

    protected abstract int getLayoutId();

    protected boolean initBundle(Bundle bundle) {

        return true;
    }

    protected void initWidget() {

    }

    protected void initData() {

    }


    protected void onRelease() {

    }

    protected void runUiThread(Runnable runnable) {
        runUiThread(runnable, 0);
    }

    protected void runUiThread(Runnable runnable, int delay) {
        View root = this.mRoot;
        if (root == null) return;
        root.postDelayed(runnable, delay);
        List<Runnable> runnableList = this.mRunnables;
        if (runnableList == null) {
            runnableList = new ArrayList<>();
        }
        runnableList.add(runnable);
        this.mRunnables = runnableList;
    }

}
