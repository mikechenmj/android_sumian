package com.sumian.sd.diary.sleeprecord;

import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.common.h5.handler.SBridgeHandler;
import com.sumian.common.h5.widget.SWebView;
import com.sumian.sd.R;
import com.sumian.sd.base.SdBaseWebViewActivity;
import com.sumian.sd.diary.sleeprecord.bean.FillSleepRecordResponse;
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord;
import com.sumian.sd.event.EventBusUtil;
import com.sumian.sd.event.SleepRecordFilledEvent;
import com.sumian.sd.h5.H5Uri;
import com.sumian.common.utils.JsonUtil;
import com.sumian.sd.utils.TimeUtil;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 20:00
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FillSleepRecordActivity extends SdBaseWebViewActivity {
    public static final String SLEEP_RESPONSE = "sleep_response";
    public static final String KEY_TIME = "TIME";

    public static void launchForResult(Fragment launcher, long time, int requestCode) {
        Intent intent = new Intent(ActivityUtils.getTopActivity(), FillSleepRecordActivity.class);
        intent.putExtra(KEY_TIME, time);
        launcher.startActivityForResult(intent, requestCode);
//        ActivityUtils.startActivity(new Intent(ActivityUtils.getTopActivity(), FillSleepDiaryActivity.class));
    }

    public static SleepRecord resolveResultData(Intent data) {
        FillSleepRecordResponse fillSleepRecordResponse = JsonUtil.fromJson(data.getStringExtra(SLEEP_RESPONSE), FillSleepRecordResponse.class);
        if (fillSleepRecordResponse == null) {
            return null;
        }
        return fillSleepRecordResponse.getSleepRecord();
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
    protected void registerHandler(@NonNull SWebView sWebView) {
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
        EventBusUtil.postStickyEvent(new SleepRecordFilledEvent());
        Intent intent = new Intent();
        intent.putExtra(SLEEP_RESPONSE, response);
        setResult(RESULT_OK, intent);
        finish();
    }
}
