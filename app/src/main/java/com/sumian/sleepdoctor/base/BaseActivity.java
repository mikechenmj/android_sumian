package com.sumian.sleepdoctor.base;

import android.app.Activity;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jaeger.library.StatusBarUtil;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.account.activity.LoginActivity;
import com.sumian.sleepdoctor.account.model.AccountViewModel;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/4/left.
 * <p>
 * desc:
 */

public abstract class BaseActivity<Presenter> extends AppCompatActivity implements DefaultLifecycleObserver, Observer<Boolean>, LifecycleOwner {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Unbinder mBind;

    private LiveData<Boolean> mTokenInvalidStateLiveData;
    protected View mRoot;
    private boolean mIsTopLogin;

    protected Presenter mPresenter;

    public static void show(Context context, Class<? extends BaseActivity> clx) {
        show(context, clx, null);
    }

    public static void show(Context context, Class<? extends BaseActivity> clx, Bundle extras) {
        Intent intent = new Intent(context, clx);
        if (extras != null)
            intent.putExtras(extras);
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
            if (mTokenInvalidStateLiveData == null) {
                mTokenInvalidStateLiveData = new AccountViewModel(getApplication()).getLiveDataTokenInvalidState();
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

    }

    //public void commitReplace(@NonNull Class<? extends Fragment> clx) {

//        FragmentTransaction fragmentTransaction = getFragmentTransaction();
//
//        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
//        if (fragmentByTag != null) {
//            // Log.e(TAG, "commitReplace: ---------->" + fragmentByTag.toString());
//            return;
//        }
//
//        fragmentTransaction.replace(R.id.lay_tab_container, fragment, fragment.getClass().getSimpleName());
//
//        if (!(fragment instanceof GroupFragment || fragment instanceof MeFragment)) {
//            addToBackStack(fragment, fragmentTransaction);
//        }
//
//        Fragment welcomeFragment = getSupportFragmentManager().findFragmentByTag("WelcomeActivity");
//        if (welcomeFragment != null)
//            fragmentTransaction.remove(welcomeFragment);

    //commitTransaction(fragmentTransaction);
    // }

    // public void commitReplacePagerFragment(@NonNull Class<? extends Fragment> clx) {
//        this.mIsTopLogin = fragment instanceof LoginActivity;
//
//        FragmentTransaction fragmentTransaction = getFragmentTransaction();
//
//        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
//        if (fragmentByTag != null) {
//            // Log.e(TAG, "commitReplace: ---------->" + fragmentByTag.toString());
//            return;
//        }
//
//        fragmentTransaction.replace(R.id.lay_page_container, fragment, fragment.getClass().getSimpleName());
//
//        if (!(fragment instanceof WelcomeActivity)) {
//            addToBackStack(fragment, fragmentTransaction);
//        }
//
//        commitTransaction(fragmentTransaction);
    //  }

//    public void goHome() {
    //this.mIsTopLogin = false;
    //int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
    //Log.e(TAG, "goHome: ------------>backStackEntryCount=" + backStackEntryCount);
    //for (int i = 0; i < backStackEntryCount; i++) {
    //getFragmentManager().popBackStackImmediate();
    // FragmentManager.BackStackEntry backStackEntry =
    //   Log.e(TAG, "goHome: --------->" + backStackEntry.toString());
    // getSupportFragmentManager().popBackStackImmediate(backStackEntry.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    //}
    //  }

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

//    @Override
//    public void onBackPressed() {
//
//        // int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
//        // Log.e(TAG, "onBackPressedDelegate: -----1--->" + backStackEntryCount);
//        //List<Fragment> fragments = getFragmentManager().getFragments();
//        // Log.e(TAG, "onBackPressedDelegate: -----2----->" + fragments.toString());
//
//        //  fragments.isEmpty() || isGroupFragment(fragments) || isMeFragment(fragments) || isLoginFragment(fragments) ||
//        // if (backStackEntryCount <= 0) {
//        //   finish();
//        //} else {
//        // super.onBackPressed();
//        //}
//
//        //   Log.e(TAG, "onBackPressedDelegate: ------3---->" + fragments.toString());
//    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onChanged(@Nullable Boolean tokenIsInvalid) {
        if (tokenIsInvalid && !mIsTopLogin) LoginActivity.show(this, LoginActivity.class);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
}
