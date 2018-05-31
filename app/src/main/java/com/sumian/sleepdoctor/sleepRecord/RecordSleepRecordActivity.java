package com.sumian.sleepdoctor.sleepRecord;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Url;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.util.Locale;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 20:00
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RecordSleepRecordActivity extends BaseWebViewActivity {

    @Override
    protected int initTitle() {
        return R.string.tab_record;
    }

    @Override
    protected String getUrlContentPart() {
        long timeInMillis = System.currentTimeMillis();
        String dateStr = TimeUtil.formatDate("yyyy-MM-dd", timeInMillis);
        String format = String.format(Locale.getDefault(), "?date='%s'", dateStr);
//        return H5Url.H5_URI_SLEEP_RECORD_RECORD_SLEEP + format;
        return H5Url.H5_URI_SLEEP_RECORD_RECORD_SLEEP;
    }

    @Override
    protected void registerHandler(SWebView sWebView) {
        sWebView.registerHandler("diaryFinishFilling", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
            }
        });
    }
}
