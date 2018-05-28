package com.sumian.sleepdoctor.app.delegate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.login.LoginActivity;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.pager.activity.WelcomeActivity;
import com.sumian.sleepdoctor.tab.fragment.GroupFragment;
import com.sumian.sleepdoctor.tab.fragment.MeFragment;

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

    private LinkedList<String> mBackFragments;

    //private Fragment mPreFragment;//上一个 fragment,可以提前预处理,比如在不可见时,预处理 ui, 当可见时,再去刷新数据

    public FragmentManagerDelegate(AppCompatActivity activity) {
        this.mActivityWeakReference = new WeakReference<>(activity);
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.mBackFragments = new LinkedList<>();
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
    }

    @Override
    public void replaceFragment(@NonNull Class<? extends Fragment> clx) {
        replaceFragment(clx, null);
    }

    @Override
    public void replaceFragment(@NonNull Class<? extends Fragment> clx, @Nullable Bundle args) {
        replaceFragment(R.id.lay_tab_container, clx, args);
    }

    @Override
    public void replaceFragment(@IdRes int containerId, @NonNull Class<? extends Fragment> clx, @Nullable Bundle args) {
        replace(containerId, clx, args);
    }

    @Override
    public void showNavTab() {
    }

    @Override
    public void hideNavTab() {
    }

    public void onBackPressedDelegate() {

        Log.e(TAG, "onBackPressedDelegate: ---------->" + mBackFragments.toString());

        AppCompatActivity appCompatActivity = this.mActivityWeakReference.get();
        if (appCompatActivity == null) return;

        //1.tag 栈中没有元素,
        //2.只有一个元素,
        //那么直接退出 app
        if (mBackFragments.isEmpty() || mBackFragments.size() <= 1) {
            appCompatActivity.finishAffinity();
            return;
        }

        //开始fragment事务回退栈
        String TopFName = mBackFragments.removeLast();//弹出栈顶元素,不可返回空

        //3.栈顶元素是 login
        //那么直接退出 app
        if (LoginActivity.class.getName().equals(TopFName)) {
            appCompatActivity.finishAffinity();
            return;
        }

        //4.弹出栈顶元素之后,检查回退栈中的大小,当为 null,
        //那么直接退出 app
        if (mBackFragments.isEmpty()) {
            appCompatActivity.finishAffinity();
            return;
        }

        String lastCurrentFragmentTag = mBackFragments.peekLast();//获取弹栈之后的回退栈中的栈顶元素,可返回空.但不删除该元素

        Class<? extends Fragment> clx = getFragmentClass(lastCurrentFragmentTag);

        replaceFragment(clx);
    }

    @Override
    public void pop(String fName) {
        if (TextUtils.isEmpty(fName)) {
            mBackFragments.pollLast();
        } else {
            mBackFragments.remove(fName);
        }
    }

    @Override
    public void pop() {
        pop(null);
    }

    public void goHome() {
        String fName = mBackFragments.peekFirst();
        Class<? extends Fragment> clx = getFragmentClass(fName);
        if (clx == null || clx.getName().equals(WelcomeActivity.class.getName())) {
            clx = GroupFragment.class;
        }
        //noinspection unchecked
        // removeAll(LoginActivity.class, WelcomeActivity.class);
        replaceFragment(clx);
    }

    @Override
    public void onRelease() {
        this.mActivityWeakReference.clear();
        this.mBackFragments.clear();
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void removeAll(Class<? extends Fragment>... args) {
        for (Class<? extends Fragment> arg : args) {
            mBackFragments.remove(arg.getName());
        }
    }

    private void replace(@IdRes int containerId, @NonNull Class<? extends Fragment> clx, @Nullable Bundle args) {

        String fName = clx.getName();

        if (mBackFragments.isEmpty()) {//第一次有元素入栈,直接添加
            this.mBackFragments.add(fName);
        } else {//进入回退栈

            int index = mBackFragments.indexOf(fName);// 查找元素

            switch (index) {
                case -1://回退栈没有该元素,直接添加/替换
                    if (fName.equals(GroupFragment.class.getName()) || fName.equals(MeFragment.class.getName())) {
                        mBackFragments.set(0, fName);
                    } else {
                        this.mBackFragments.add(fName);
                    }
                    break;
                //回退栈有该元素
                case 0://home 元素 直接替换掉
                    mBackFragments.set(0, fName);
                    break;
                default://已存在的 pager 元素,不进行更新
                    Log.e(TAG, "replace: -----------------is exist------->");
                    return;
            }
        }

        Fragment fragment = null;
        try {
            fragment = clx.getConstructor().newInstance();

            if (fragment instanceof HomeDelegate) {
                showNavTab();
            } else {
                hideNavTab();
            }

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
