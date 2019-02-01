package com.sumian.sddoctor.service.cbti.sheet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by jzz
 * on 2017/10/05.
 * <p>
 * desc:底部弹窗
 */

public abstract class BaseBottomSheetView extends BottomSheetDialogFragment {

    protected Context mContext;

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
        initView(view);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
