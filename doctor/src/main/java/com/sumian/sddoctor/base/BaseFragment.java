package com.sumian.sddoctor.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sddoctor.util.EventBusUtil;
import com.sumian.sddoctor.widget.LoadingDialog;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;

/**
 * Fragment基类
 */

@SuppressWarnings({"WeakerAccess", "unchecked"})
public abstract class BaseFragment extends Fragment implements BaseView {
    protected Context mContext;
    protected View mRoot;
    protected Bundle mBundle;
    protected LayoutInflater mInflater;
    protected Set<Call> mCalls = new HashSet<>();
    private LoadingDialog mLoadingDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) {
                parent.removeView(mRoot);
            }
        } else {
            mRoot = inflater.inflate(getLayoutId(), container, false);
            mInflater = inflater;
            // Do something
            onBindViewBefore(mRoot);
            // Bind view
            initPresenter();
        }
        return mRoot;
    }

    protected void initPresenter() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        initData();
    }

    protected void onBindViewBefore(View root) {

    }

    protected abstract int getLayoutId();

    protected void initBundle(Bundle bundle) {

    }

    protected void initWidget(View root) {

    }

    protected void initData() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (openEventBus()) {
            EventBusUtil.Companion.register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (openEventBus()) {
            EventBusUtil.Companion.unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        for (Call call : mCalls) {
            cancelCallIfPossible(call);
        }
        super.onDestroy();
    }

    protected void cancelCallIfPossible(Call call) {
        if (call == null) {
            return;
        }
        call.cancel();
    }

    protected void addCall(Call call) {
        mCalls.add(call);
    }

    @Override
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected boolean openEventBus() {
        return false;
    }

}
