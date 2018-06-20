package com.sumian.sleepdoctor.onlinereport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.main.MainActivity;

public class OnlineReportDetailActivity extends BaseWebViewActivity {

    public static final String KEY_REPORT_URL = "report_url";
    public static final String KEY_REPORT_NAME = "report_name";
    private String mReportName;
    private String mReportUrl;

    public static void launch(Context context, String reportName, String reportUrl) {
        show(context, getLaunchIntent(context, reportName, reportUrl));
    }

    @NonNull
    public static Intent getLaunchIntent(Context context, String reportName, String reportUrl) {
        Intent intent = new Intent(context, OnlineReportDetailActivity.class);
        intent.putExtra(KEY_REPORT_NAME, reportName);
        intent.putExtra(KEY_REPORT_URL, reportUrl);
        return intent;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mReportName = bundle.getString(KEY_REPORT_NAME);
        mReportUrl = bundle.getString(KEY_REPORT_URL);
        return true;
    }

    @Override
    protected String initTitle() {
        return mReportName;
    }

    @Override
    protected String getUrlContentPart() {
        String onlineReport = H5Uri.ONLINE_REPORT;
        onlineReport = onlineReport.replace("{title}", mReportName)
                .replace("{pdfUrl}", mReportUrl);
        return onlineReport;
    }

    @Override
    protected void registerHandler(SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("analyseReport", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                MainActivity.launch(mActivity, 1);
            }
        });
    }
}
