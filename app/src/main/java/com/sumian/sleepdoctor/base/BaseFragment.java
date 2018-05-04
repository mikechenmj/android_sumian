package com.sumian.sleepdoctor.base;

import android.app.Activity;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaeger.library.StatusBarUtil;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by jzz
 * <p>
 * on 2017/04/01
 * <p>
 * desc: base fragment
 */
public abstract class BaseFragment<Presenter extends BasePresenter> extends Fragment implements DefaultLifecycleObserver, LifecycleOwner {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected Activity mActivity;
    protected View mRootView;
    public Bundle mBundle;

    private Unbinder mUnBinder;

    protected Presenter mPresenter;

    public static Fragment newInstance(Class<? extends Fragment> clx) {
        return newInstance(clx, null);
    }

    public static Fragment newInstance(Class<? extends Fragment> clx, Bundle args) {
        Fragment fragment = null;
        try {
            fragment = clx.getConstructor().newInstance();
            if (args != null) {
                args.setClassLoader(fragment.getClass().getClassLoader());
                fragment.setArguments(args);
            }
        } catch (java.lang.InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mActivity == null)
            mActivity = (Activity) context;
        getLifecycle().addObserver(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        // Do something
        onBindViewBefore(rootView);
        // Bind view
        this.mUnBinder = ButterKnife.bind(this, rootView);
        // Get savedInstanceState
        if (savedInstanceState != null) {
            onRestartInstance(savedInstanceState);
        }
        // Init
        initWidget(rootView);
        this.mRootView = rootView;
        // Log.e("TAG", "onCreateView: ----->" + mRootView);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mUnBinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getLifecycle().removeObserver(this);
        onRelease();
    }

    protected void onRestartInstance(Bundle savedInstanceState) {

    }

    protected void onBindViewBefore(View rootView) {

    }

    protected void initBundle(Bundle bundle) {

    }

    protected abstract int getLayoutId();

    protected void initPresenter() {

    }

    protected void initWidget(View root) {

    }

    protected void initData() {

    }

    protected void onRelease() {
        mPresenter.release();
    }

    protected void runOnUiThread(Runnable run) {
        runOnUiThread(run, 0);
    }

    protected void runOnUiThread(Runnable run, long delay) {
        View rootView = this.mRootView;
        if (rootView == null) return;
        rootView.postDelayed(run, delay);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onCreate: -------->" + this.toString());
        initPresenter();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onStart: --------->" + this.toString());
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onResume: -------->" + this.toString());
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onPause: ----------->" + this.toString());
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onStop: ----------->" + this.toString());
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onDestroy: ----------->" + this.toString());
    }

    protected void showToast(String message) {
        runOnUiThread(() -> ToastHelper.show(message));
    }

    protected void showToast(@StringRes int messageId) {
        showToast(getString(messageId));
    }

    protected void setStatusBarColor() {
        StatusBarUtil.setColorNoTranslucent(getActivity(), getResources().getColor(R.color.colorPrimary));
    }

    protected void setStatusBarTranslucent() {
        StatusBarUtil.hideFakeStatusBarView(Objects.requireNonNull(getActivity()));
    }

}
