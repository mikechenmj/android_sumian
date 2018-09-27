package com.sumian.sd.widget.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumian.common.h5.widget.SWebViewLayout;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;
import com.sumian.sd.h5.H5Uri;

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
    @BindView(R.id.tv_title)
    TextView mTvTitle;

    private Unbinder mBind;
    private String mUrl;
    private String mTitle;

    public SumianWebDialog(String url) {
        this(url, null);
    }

    public SumianWebDialog(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    public static SumianWebDialog createWithCompleteUrl(String completeUrl) {
        return new SumianWebDialog(completeUrl);
    }

    public static SumianWebDialog createWithCompleteUrl(String completeUrl, String title) {
        return new SumianWebDialog(completeUrl, title);
    }

    public static SumianWebDialog createWithPartUrl(String partUrl) {
        return createWithCompleteUrl(BuildConfig.BASE_H5_URL + partUrl);
    }

    public static SumianWebDialog createWithPartUrl(String partUrl, String title) {
        return createWithCompleteUrl(BuildConfig.BASE_H5_URL + partUrl, title);
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
        mWebView.loadRequestUrl(mUrl);
        mTvTitle.setVisibility(TextUtils.isEmpty(mTitle) ? View.GONE : View.VISIBLE);
        mTvTitle.setText(mTitle);
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
