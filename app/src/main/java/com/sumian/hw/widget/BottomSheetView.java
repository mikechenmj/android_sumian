package com.sumian.hw.widget;

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

public abstract class BottomSheetView extends BottomSheetDialogFragment {


    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments == null) return;
        initBundle(arguments);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        getView();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        release();
        super.onDestroyView();
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
