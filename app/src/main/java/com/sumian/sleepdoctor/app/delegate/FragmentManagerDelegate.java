package com.sumian.sleepdoctor.app.delegate;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.fragment.LoginFragment;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.pager.fragment.WelcomeFragment;
import com.sumian.sleepdoctor.tab.fragment.TabGroupFragment;
import com.sumian.sleepdoctor.widget.nav.NavTab;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Created by sm
 * on 2018/1/23.
 * desc:
 */

public class FragmentManagerDelegate implements BaseFragmentManager<Fragment>, FragmentLifecycleCallback {

    private static final String TAG = FragmentManagerDelegate.class.getSimpleName();

    private FragmentManager mFragmentManager;

    private WeakReference<AppCompatActivity> mActivityWeakReference;

    private FragmentLifecycleDelegate mFragmentLifecycleDelegate;

    private LinkedList<String> mFragments;
    private LinkedList<String> mBackStacks;

    private Fragment mCurrentTabFragment;

    private Fragment mTopFragment;

    private Fragment mPreFragment;//上一个 fragment,可以提前预处理,比如在不可见时,预处理 ui, 当可见时,再去刷新数据

    private FrameLayout mFragmentContainer;

    private NavTab mNavTab;

    private boolean mIsLoginTop;

    public FragmentManagerDelegate(AppCompatActivity activity) {
        this.mActivityWeakReference = new WeakReference<>(activity);
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.mFragments = new LinkedList<>();
        this.mBackStacks = new LinkedList<>();
    }

    public FragmentManagerDelegate bindFragmentContainer(View fragmentContainer) {
        this.mFragmentContainer = (FrameLayout) fragmentContainer;
        return this;
    }

    public FragmentManagerDelegate bindNavTab(View navTab) {
        this.mNavTab = (NavTab) navTab;
        return this;
    }

    public FragmentManagerDelegate registerFragmentLifecycleCallback() {
        mFragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycleDelegate = new FragmentLifecycleDelegate(this), true);
        return this;
    }

    public void unRegisterFragmentLifecycleCallback() {
        mFragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentLifecycleDelegate);
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {

        mIsLoginTop = f instanceof LoginFragment;

        mCurrentTabFragment = mTopFragment = f;
        //当是 loginFragment 时,表示鉴权不通过,可直接触发back退出 app
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {

    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {

    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {

        if (f instanceof WelcomeFragment) {
            return;
        }

        if (f instanceof LoginFragment) {
            mIsLoginTop = false;
            return;
        }

        String fragmentTag = f.getClass().getName();
        Log.e(TAG, "onFragmentDetached: -------1---->fragmentTag=" + fragmentTag);

        if (this.mFragments.contains(fragmentTag)) {
            Log.e(TAG, "onFragmentDetached: ------------is exist----->");
            return;
        }

        if (fragmentTag.startsWith("Tab")) {
            // mCurrentTabFragment = f;
        }


        this.mFragments.offerLast(fragmentTag);
    }

    @Override
    public void replaceFragment(@NonNull Class<? extends Fragment> clx) {
        replaceFragment(clx, null);
    }

    @Override
    public void replaceFragment(@NonNull Class<? extends Fragment> clx, @Nullable Bundle args) {
        replaceFragment(R.id.lay_fragment_container, clx, args);
    }

    @Override
    public void replaceFragment(@IdRes int containerId, Class<? extends Fragment> clx, @Nullable Bundle args) {
        replace(containerId, clx, args);
    }

    @Override
    public void showNavTab() {
        mNavTab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNavTab() {
        mNavTab.setVisibility(View.GONE);

        boolean a = Looper.myLooper() == Looper.getMainLooper();
        Log.e(TAG, "hideNavTab: --------->" + a);
    }

    @UiThread
    public void onBackPressedDelegate() {

        Log.e(TAG, "onBackPressedDelegate: ---------->" + mFragments.toString());

        AppCompatActivity appCompatActivity = this.mActivityWeakReference.get();
        if (appCompatActivity == null) return;

        //tag 栈中没有元素,或者只有一个元素,说明已经在 home,直接退出 app
        if (mFragments.isEmpty() || mFragments.size() <= 1) {
            appCompatActivity.finishAffinity();
            return;
        }

        //开始fragment事务回退栈
        // String lastFragmentTag = mFragments.removeLast();//弹出栈顶元素,可返回空

        //弹出栈顶元素之后,检查回退栈中的大小,当为 null, 直接退出 app
        // if (mFragments.isEmpty()) {
        //   appCompatActivity.finishAffinity();
        //  return;
        //}

        String lastCurrentFragmentTag = mFragments.pollLast();//获取栈顶元素,可返回空.但不删除该元素

        Class<? extends Fragment> clx = getFragmentClass(lastCurrentFragmentTag);

        if (clx == null || clx.getSimpleName().contains(LoginFragment.class.getSimpleName())) {
            appCompatActivity.finishAffinity();
            return;
        }

        replaceFragment(clx);
    }

    @UiThread
    public void goHome() {
        if (mCurrentTabFragment != null) {
            mFragmentManager.beginTransaction().show(mCurrentTabFragment).commitNowAllowingStateLoss();
            showNavTab();
        } else {

            String fName = mFragments.peekFirst();
            Class<? extends Fragment> clx = getFragmentClass(fName);
            if (clx == null) {
                clx = TabGroupFragment.class;
            }
            replaceFragment(clx);
        }
    }

    @UiThread
    private void replace(@IdRes int containerId, @NonNull Class<? extends Fragment> clx, @Nullable Bundle args) {

        String peekLast = mFragments.peekLast();

        if (!TextUtils.isEmpty(peekLast) && peekLast.contains(mCurrentTabFragment.getClass().getSimpleName()))
            return;

        if (clx.getName().contains("Tab")) {
            showNavTab();
        } else {
            hideNavTab();
        }

        Fragment fragment = null;
        try {
            fragment = clx.getConstructor().newInstance();
            if (args != null) {
                args.setClassLoader(fragment.getClass().getClassLoader());
                fragment.setArguments(args);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        mFragmentManager.beginTransaction().replace(containerId, fragment, clx.getName()).commitNowAllowingStateLoss();
    }


    @SuppressWarnings("unchecked")
    private Class<? extends Fragment> getFragmentClass(String fName) {
        Class<? extends Fragment> clx = null;
        if (TextUtils.isEmpty(fName)) {
            clx = null;
        } else {
            try {
                clx = (Class<? extends Fragment>) App.Companion.getAppContext().getClassLoader().loadClass(fName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clx;
    }

}
