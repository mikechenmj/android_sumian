package com.sumian.sleepdoctor.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.sleepdoctor.main.WelcomeFragment;
import com.sumian.sleepdoctor.main.tab.group.fragment.GroupFragment;
import com.sumian.sleepdoctor.main.tab.me.MeFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/4/left.
 * <p>
 * desc:
 */

public abstract class BaseActivity extends Activity implements DefaultLifecycleObserver, Observer<Boolean>, LifecycleOwner {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Unbinder mBind;
    protected View mRoot;
    private LiveData<Boolean> mTokenInvalidStateLiveData;
    private boolean mIsTopLogin;

    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!initBundle(intent.getExtras())) {
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (initBundle(getIntent().getExtras())) {
            setContentView(getLayoutId());
            setStatusBar();
            initWindow();
            this.mBind = ButterKnife.bind(this);
            this.mRoot = getWindow().getDecorView();
            initWidget();
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
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
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

        Fragment fragmentByTag = getFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
        if (fragmentByTag != null) {
            // Log.e(TAG, "commitReplaceTabFragment: ---------->" + fragmentByTag.toString());
            return;
        }

        fragmentTransaction.replace(R.id.lay_tab_container, fragment, fragment.getClass().getSimpleName());

        if (!(fragment instanceof GroupFragment || fragment instanceof MeFragment)) {
            addToBackStack(fragment, fragmentTransaction);
        }

        Fragment welcomeFragment = getFragmentManager().findFragmentByTag("WelcomeFragment");
        if (welcomeFragment != null)
            fragmentTransaction.remove(welcomeFragment);

        commitTransaction(fragmentTransaction);
    }

    public void commitReplacePagerFragment(Fragment fragment) {
        this.mIsTopLogin = fragment instanceof LoginFragment;

        FragmentTransaction fragmentTransaction = getFragmentTransaction();

        Fragment fragmentByTag = getFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
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
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        Log.e(TAG, "goHome: ------------>backStackEntryCount=" + backStackEntryCount);
        //for (int i = 0; i < backStackEntryCount; i++) {
        getFragmentManager().popBackStackImmediate();
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
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        // Log.e(TAG, "onBackPressed: -----1--->" + backStackEntryCount);
        //List<Fragment> fragments = getFragmentManager().getFragments();
        // Log.e(TAG, "onBackPressed: -----2----->" + fragments.toString());

        //  fragments.isEmpty() || isGroupFragment(fragments) || isMeFragment(fragments) || isLoginFragment(fragments) ||
        if (backStackEntryCount <= 0) {
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
        return getFragmentManager().beginTransaction();//.setCustomAnimations(com.qmuiteam.qmui.arch.R.anim.scale_enter, com.qmuiteam.qmui.arch.R.anim.scale_exit);
    }

    private void addToBackStack(Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
    }

    private void commitTransaction(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.commit();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    public void setTransparentForImageViewInFragment(@Nullable View needOffsetView) {
        StatusBarUtil.setTransparentForImageViewInFragment(this, needOffsetView);
    }
}
