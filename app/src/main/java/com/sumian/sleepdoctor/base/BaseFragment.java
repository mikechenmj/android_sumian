package com.sumian.sleepdoctor.base;

import android.app.Activity;
import android.app.Fragment;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.main.MainActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by jzz
 * <p>
 * on 2017/04/01
 * <p>
 * desc: base fragment
 */
public abstract class BaseFragment<Presenter> extends Fragment implements DefaultLifecycleObserver, LifecycleOwner {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected Activity mActivity;
    protected View mRootView;
    public Bundle mBundle;

    private Unbinder mUnbinder;

    protected Presenter mPresenter;

    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

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
        setRetainInstance(true);
        mBundle = getArguments();
        initBundle(mBundle);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        // Do something
        onBindViewBefore(rootView);
        // Bind view
        this.mUnbinder = ButterKnife.bind(this, rootView);
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
    public void onStart() {
        super.onStart();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onPause() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        this.mUnbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        onRelease();
        getLifecycle().removeObserver(this);
        super.onDetach();
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
        Log.e(TAG, "onCreate: -------->" + this.toString());
        initPresenter();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onStart: --------->" + this.toString());
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onResume: -------->" + this.toString());
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onPause: ----------->" + this.toString());
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onStop: ----------->" + this.toString());
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.e(TAG, "onDestroy: ----------->" + this.toString());
    }

    protected void commitReplaceTab(Fragment fragment) {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).commitReplaceTabFragment(fragment);
        }
    }

    protected void commitReplacePager(Fragment fragment) {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).commitReplacePagerFragment(fragment);
        }
    }

    protected void popBackPressed() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        if (mActivity instanceof MainActivity) {
            mActivity.onBackPressed();
        }
    }

    protected void goHome() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).goHome();
        }
    }

    protected void showToast(String message) {
        runOnUiThread(() -> ToastHelper.show(message));
    }

    protected void showToast(@StringRes int messageId) {
        showToast(getString(messageId));
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    public void setStatusBar() {
        if (mActivity == null) {
            mActivity = getActivity();
        }

        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).setTransparentForImageViewInFragment(null);
        }
    }
}
