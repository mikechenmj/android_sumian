package com.sumian.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by jzz
 * <p>
 * on 2017/04/01
 * <p>
 * desc: base fragment
 */
public abstract class BaseFragment<T> extends Fragment {

    private static final String TAG = "BaseFragment";

    public Bundle mBundle;
    protected View mRootView;
    private Activity mMainActivity;
    private Unbinder mUnbinder;
    private Runnable mPreRunnable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mMainActivity == null)
            mMainActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBundle = getArguments();
        initBundle(mBundle);
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
    public void onDestroyView() {
        onRelease();
        super.onDestroyView();
        this.mUnbinder.unbind();
    }

    protected void onRestartInstance(Bundle savedInstanceState) {

    }

    protected void onBindViewBefore(View rootView) {

    }

    protected void initBundle(Bundle bundle) {

    }

    protected abstract int getLayoutId();

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

}
