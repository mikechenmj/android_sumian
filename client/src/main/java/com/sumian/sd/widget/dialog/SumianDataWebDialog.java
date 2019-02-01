package com.sumian.sd.widget.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumian.common.h5.widget.SWebViewLayout;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;
import com.sumian.sd.common.h5.H5Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 *
 */
@SuppressWarnings("ALL")
public class SumianDataWebDialog extends DialogFragment implements View.OnClickListener {

    private SWebViewLayout mWebView;
    private TextView mTvTitle;

    private String mUrl;
    private String mTitle;
    private String mWebData;

    public SumianDataWebDialog(String url, String title, String webData) {
        this.mUrl = url;
        this.mTitle = title;
        this.mWebData = webData;
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
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.web_view);
        mTvTitle = view.findViewById(R.id.tv_title);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        String h5Url = BuildConfig.BASE_H5_URL + H5Uri.ADVISORY_GUIDE;
        mWebView.getSWebView().loadDataWithBaseURL("", mWebData, "text/html", "utf-8", "");
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
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getClass().getSimpleName());
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
