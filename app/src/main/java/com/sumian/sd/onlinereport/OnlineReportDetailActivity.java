package com.sumian.sd.onlinereport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.main.MainActivity;
import com.sumian.sd.widget.webview.SBridgeHandler;
import com.sumian.sd.widget.webview.SWebView;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReportDetailActivity extends SdBaseWebViewActivity {

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
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("analyseReport", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                MainActivity.Companion.launch(MainActivity.TAB_SD_1, null);
            }
        });
    }
}
