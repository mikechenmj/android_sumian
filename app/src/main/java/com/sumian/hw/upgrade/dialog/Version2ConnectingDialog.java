package com.sumian.hw.upgrade.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sumian.hw.widget.BaseDialogFragment;
import com.sumian.sd.R;

import butterknife.ButterKnife;

/**
 * DFU 升级时,监测仪断开,速眠仪连接阶段的 dialog
 */
public class Version2ConnectingDialog extends BaseDialogFragment {

    public static Version2ConnectingDialog newInstance() {
        return new Version2ConnectingDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SumianDialog);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        return rootView;
    }

    @Override
    protected int getLayout() {
        setCancelable(false);
        return R.layout.hw_lay_dialog_version_connectting;
    }
}
