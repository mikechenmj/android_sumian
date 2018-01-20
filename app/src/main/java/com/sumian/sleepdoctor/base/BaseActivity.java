package com.sumian.sleepdoctor.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.sleepdoctor.main.WelcomeFragment;
import com.sumian.sleepdoctor.main.tab.group.fragment.GroupFragment;
import com.sumian.sleepdoctor.main.tab.me.MeFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/4/left.
 * <p>
 * desc:
 */

public abstract class BaseActivity extends AppCompatActivity implements DefaultLifecycleObserver, Observer<Boolean> {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Unbinder mBind;
    protected View mRoot;
    private LiveData<Boolean> mTokenInvalidStateLiveData;
    private boolean mIsTopLogin;

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!initBundle(intent.getExtras())) {
            finish();
        }
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
            getLifecycle().addObserver(this);
            if (mTokenInvalidStateLiveData == null) {
                mTokenInvalidStateLiveData = ViewModelProviders.of(this).get(AccountViewModel.class).getLiveDataTokenInvalidState();
            }
            mTokenInvalidStateLiveData.observe(this, this);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        onRelease();
        if (mTokenInvalidStateLiveData != null && (mTokenInvalidStateLiveData.hasActiveObservers() || mTokenInvalidStateLiveData.hasObservers())) {
            mTokenInvalidStateLiveData.removeObservers(this);
        }
        getLifecycle().removeObserver(this);
        this.mBind.unbind();
        this.mRoot = null;
        super.onDestroy();
    }

    protected boolean initBundle(Bundle bundle) {

        return true;
    }

    protected void initWindow() {

    }

    protected abstract int getLayoutId();

    protected void initWidget() {

    }

    protected void initData() {

    }

    protected void onRelease() {

    }

    public void commitReplaceTabFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentTransaction();

        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
        if (fragmentByTag != null) {
            // Log.e(TAG, "commitReplaceTabFragment: ---------->" + fragmentByTag.toString());
            return;
        }


        fragmentTransaction.replace(R.id.lay_tab_container, fragment, fragment.getClass().getSimpleName());
        if (!(fragment instanceof GroupFragment || fragment instanceof MeFragment)) {
            addToBackStack(fragment, fragmentTransaction);
        }
        commitTransaction(fragmentTransaction);
    }

    public void commitReplacePagerFragment(Fragment fragment) {
        this.mIsTopLogin = fragment instanceof LoginFragment;

        FragmentTransaction fragmentTransaction = getFragmentTransaction();

        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
        if (fragmentByTag != null) {
            // Log.e(TAG, "commitReplaceTabFragment: ---------->" + fragmentByTag.toString());
            return;
        }

        fragmentTransaction.replace(R.id.lay_page_container, fragment, fragment.getClass().getSimpleName());
        if (!(fragment instanceof WelcomeFragment)) {
            addToBackStack(fragment, fragmentTransaction);
        }
        commitTransaction(fragmentTransaction);
    }

    public void goHome() {
        this.mIsTopLogin = false;
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.e(TAG, "goHome: ------------>backStackEntryCount=" + backStackEntryCount);
        //for (int i = 0; i < backStackEntryCount; i++) {
        getSupportFragmentManager().popBackStackImmediate();
        // FragmentManager.BackStackEntry backStackEntry =
        //   Log.e(TAG, "goHome: --------->" + backStackEntry.toString());
        // getSupportFragmentManager().popBackStackImmediate(backStackEntry.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //}
    }

    public static void show(Context context, Class<? extends BaseActivity> clx) {
        context.startActivity(new Intent(context, clx));
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

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        // Log.e(TAG, "onBackPressed: -----1--->" + backStackEntryCount);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        // Log.e(TAG, "onBackPressed: -----2----->" + fragments.toString());

        if (fragments.isEmpty() || isGroupFragment(fragments) || isMeFragment(fragments) || isLoginFragment(fragments) || backStackEntryCount <= 1) {
            finish();
        } else {
            super.onBackPressed();
        }
        //   Log.e(TAG, "onBackPressed: ------3---->" + fragments.toString());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onChanged(@Nullable Boolean tokenIsInvalid) {
        if (tokenIsInvalid && !mIsTopLogin) commitReplacePagerFragment(LoginFragment.newInstance());
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isLoginFragment(List<Fragment> fragments) {
        if (fragments == null || fragments.isEmpty()) return false;
        for (Fragment fragment : fragments) {
            return fragment instanceof LoginFragment;
        }
        return false;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isGroupFragment(List<Fragment> fragments) {
        if (fragments == null || fragments.isEmpty()) return false;
        for (Fragment fragment : fragments) {
            return fragment instanceof GroupFragment;
        }
        return false;
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private boolean isMeFragment(List<Fragment> fragments) {
        if (fragments == null || fragments.isEmpty()) return false;
        for (Fragment fragment : fragments) {
            return fragment instanceof MeFragment;
        }
        return false;
    }

    @SuppressLint("CommitTransaction")
    private FragmentTransaction getFragmentTransaction() {
        return getSupportFragmentManager().beginTransaction().setCustomAnimations(com.qmuiteam.qmui.arch.R.anim.scale_enter, com.qmuiteam.qmui.arch.R.anim.scale_exit);
    }

    private void addToBackStack(Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
    }

    private void commitTransaction(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.commit();
    }
}
