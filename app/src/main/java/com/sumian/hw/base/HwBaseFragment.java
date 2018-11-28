package com.sumian.hw.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.common.helper.ToastHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * created by jzz
 * <p>
 * on 2017/04/01
 * <p>
 * desc: base fragment
 */
public abstract class HwBaseFragment<Presenter extends HwBasePresenter> extends Fragment {

    private static final String TAG = "HwBaseFragment";

    public Bundle mBundle;
    protected View mRootView;
    protected Activity mHostActivity;

    public Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mHostActivity == null) {
            mHostActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        // Do something
        onBindViewBefore(rootView);
        // Get savedInstanceState
        if (savedInstanceState != null) {
            onRestartInstance(savedInstanceState);
        }
        this.mRootView = rootView;
        // Log.e("TAG", "onCreateView: ----->" + mRootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Init
        initPresenter();
        initWidget(view);
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (openEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (openEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onRelease();
        //this.mUnbinder.unbind();
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

    protected void initPresenter() {

    }

    protected void initData() {

    }

    protected void onRelease() {
        if (mPresenter != null) {
            mPresenter.release();
        }
    }

    protected void runOnUiThread(Runnable run) {
        runOnUiThread(run, 0);
    }

    protected void runOnUiThread(Runnable runnable, long delay) {
        View rootView = this.mRootView;
        if (rootView == null) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            rootView.postDelayed(runnable, delay);
        } else {
            if (delay > 0) {
                rootView.postDelayed(runnable, delay);
            } else {
                runnable.run();
            }
        }
    }

    protected void showToast(String message) {
        runOnUiThread(() -> ToastHelper.show(message));
    }

    protected void showToast(@StringRes int messageId) {
        showToast(getString(messageId));
    }

    protected void showCenterToast(String message) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread(() -> ToastHelper.show(getContext(), message, Gravity.CENTER));
        } else {
            ToastHelper.show(getContext(), message, Gravity.CENTER);
        }
    }

    protected void showCenterToast(@StringRes int messageId) {
        showCenterToast(getString(messageId));
    }

    protected boolean openEventBus() {
        return false;
    }
}
