package com.sumian.sleepdoctor.widget.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.widget.webview.SWebViewLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */
@SuppressWarnings("ALL")
public class SumianWebDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.web_view)
    SWebViewLayout mWebView;

    private Unbinder mBind;

    public static SumianWebDialog create() {
        return new SumianWebDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View inflate = inflater.inflate(R.layout.lay_alert_dialog_web, container, false);
        mBind = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String h5Url = BuildConfig.BASE_H5_URL + H5Uri.ADVISORY_GUIDE;
        mWebView.loadRequestUrl(h5Url);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.resumeWebView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.pauseWebView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.destroyWebView();
        mBind.unbind();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getClass().getSimpleName());
    }

    @OnClick(R.id.iv_close)
    @Override
    public void onClick(View v) {
        dismiss();
    }
}
