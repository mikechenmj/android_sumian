package com.sumian.sleepdoctor.widget.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.pager.activity.PayGroupActivity;

import net.qiujuer.genius.ui.widget.Button;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by sm
 * on 2018/1/25.
 * desc:
 */

public class PayDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.iv_pay_status)
    ImageView mIvPayStatus;
    @BindView(R.id.tv_pay_desc)
    TextView mTvPayDesc;
    @BindView(R.id.bt_join)
    Button mBtJoin;
    private Unbinder mUnbinder;

    private WeakReference<PayGroupActivity> mPayGroupActivityWeakReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_pay, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    public PayDialog setPayGroupActivityWeakReference(PayGroupActivity payGroupActivity) {
        this.mPayGroupActivityWeakReference = new WeakReference<>(payGroupActivity);
        return this;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    public PayDialog show(FragmentManager fragmentManager) {
        show(fragmentManager, this.getClass().getSimpleName());
        return this;
    }


    @OnClick(R.id.bt_join)
    @Override
    public void onClick(View v) {


    }
}
