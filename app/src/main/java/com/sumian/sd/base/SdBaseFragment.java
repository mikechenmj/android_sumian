package com.sumian.sd.base;

import android.app.Activity;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sd.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;

/**
 * created by jzz
 * <p>
 * on 2017/04/01
 * <p>
 * desc: base fragment
 */
public abstract class SdBaseFragment<Presenter extends SdBasePresenter> extends Fragment implements DefaultLifecycleObserver, LifecycleOwner {

    private static final String TAG = SdBaseFragment.class.getSimpleName();
    public Bundle mBundle;
    protected Activity mActivity;
    protected View mRootView;
    protected Presenter mPresenter;
    private Set<Call> mCalls = new HashSet<>();
    private LoadingDialog mLoadingDialog;

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
        return fragment;//Fragment.instantiate(App.getAppContext(), clx.getSimpleName(), args);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mActivity == null) {
            mActivity = (Activity) context;
        }
        getLifecycle().addObserver(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);

        //init presenter
        initPresenter();
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
        // PlayLog.e("TAG", "onCreateView: ----->" + mRootView);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Init
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
        if (openEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onRelease();
        for (Call call : mCalls) {
            if (call.isExecuted()) {
                call.cancel();
            }
        }
        mCalls.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getLifecycle().removeObserver(this);
    }

    protected boolean openEventBus() {
        return false;
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

    /**
     * 切记,如果是在 kotlin 当中 使用 kotlinx.android.synthetic.main.{layout}.*  initWidget 其实是在 onViewCreated() 之后执行的
     * <p>
     *
     * @param root rootView
     */
    protected void initWidget(View root) {

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

    protected void runOnUiThread(Runnable run, long delay) {
        View rootView = this.mRootView;
        if (rootView == null) {
            return;
        }
        rootView.postDelayed(run, delay);
    }


    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onCreate: -------->" + this.toString());
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
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastHelper.show(message);
        } else {
            runOnUiThread(() -> ToastHelper.show(message));
        }
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


    protected void addCall(Call call) {
        mCalls.add(call);
    }

    protected void removeCall(Call call) {
        mCalls.remove(call);
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mActivity);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void dismissLoading() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
