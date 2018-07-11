package com.sumian.sleepdoctor.widget.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jzz
 * on 2017/10/05.
 * <p>
 * desc:底部弹窗
 */

public abstract class BaseBottomSheetView extends BottomSheetDialogFragment {

    protected Context mContext;
    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        Bundle arguments = getArguments();
        if (arguments == null) return;
        initBundle(arguments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        getView();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initView(view);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        release();
    }

    protected void initBundle(Bundle arguments) {

    }

    protected abstract int getLayout();

    protected void initView(View rootView) {

    }

    protected void initData() {

    }

    protected void release() {

    }

    protected void runUiThread(Runnable run) {
        View view = getView();
        if (view == null) return;
        view.postDelayed(run, 0);
    }
}
