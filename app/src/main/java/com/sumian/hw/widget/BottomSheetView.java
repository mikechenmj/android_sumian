package com.sumian.hw.widget;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jzz
 * on 2017/10/05.
 * <p>
 * desc:底部弹窗
 */

public abstract class BottomSheetView extends BottomSheetDialogFragment {


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
