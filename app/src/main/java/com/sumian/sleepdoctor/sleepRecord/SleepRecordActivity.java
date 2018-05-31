package com.sumian.sleepdoctor.sleepRecord;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 20:00
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordActivity extends BaseWebViewActivity {
    @Override
    protected String h5HandlerName() {
        return "name";
    }

    @Override
    protected int initTitle() {
        return R.string.success;
    }

    @Override
    protected String queryParameter() {
        return "date='2018-4-24'";
    }

    @Override
    protected String appendUri() {
        return BuildConfig.H5_URI_QUESTION;
    }

    @Override
    protected void parseUrl(String url) {
        super.parseUrl(appendUri());
    }

    @Override
    protected void registerHandler(SWebView sWebView) {

    }

}
