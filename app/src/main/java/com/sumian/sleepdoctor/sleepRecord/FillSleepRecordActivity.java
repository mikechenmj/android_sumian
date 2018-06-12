package com.sumian.sleepdoctor.sleepRecord;

import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.ActivityLauncher;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Uri;
import com.sumian.sleepdoctor.improve.widget.webview.SBridgeHandler;
import com.sumian.sleepdoctor.improve.widget.webview.SWebView;
import com.sumian.sleepdoctor.sleepRecord.bean.FillSleepRecordResponse;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecord;
import com.sumian.sleepdoctor.utils.JsonUtil;
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
public class FillSleepRecordActivity extends BaseWebViewActivity {
    public static final String SLEEP_RESPONSE = "sleep_response";
    public static final String KEY_TIME = "TIME";

    public static void launchForResult(ActivityLauncher launcher, long time, int requestCode) {
        Intent intent = new Intent(launcher.getActivity(), FillSleepRecordActivity.class);
        intent.putExtra(KEY_TIME, time);
        launcher.startActivityForResult(intent, requestCode);
    }

    @Override
    protected String initTitle() {
        return getString(R.string.tab_record);
    }

    @Override
    protected String getUrlContentPart() {
        long timeInMillis = getIntent().getLongExtra(KEY_TIME, System.currentTimeMillis());
        String dateStr = TimeUtil.formatDate("yyyy-MM-dd", timeInMillis);
        String format = String.format(Locale.getDefault(), "?date=%s", dateStr);
        return H5Uri.SLEEP_RECORD_RECORD_SLEEP + format;
    }

    @Override
    protected void registerHandler(SWebView sWebView) {
        sWebView.registerHandler("diaryFinishFilling", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
                if (TextUtils.isEmpty(data)) {
                    ToastUtils.showShort(R.string.empty_response);
                    return;
                }
                returnResult(data);
            }
        });
    }

    private void returnResult(String response) {
        Intent intent = new Intent();
        intent.putExtra(SLEEP_RESPONSE, response);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static SleepRecord resolveResultData(Intent data) {
        FillSleepRecordResponse fillSleepRecordResponse = JsonUtil.fromJson(data.getStringExtra(SLEEP_RESPONSE), FillSleepRecordResponse.class);
        if (fillSleepRecordResponse == null) {
            return null;
        }
        return fillSleepRecordResponse.getSleepRecord();
    }
}
