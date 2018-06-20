package com.sumian.sleepdoctor.sleepRecord;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Uri;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 20:00
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordActivity extends BaseWebViewActivity {

    @Override
    protected String initTitle() {
        return getString(R.string.tab_record);
    }

    @Override
    protected String getUrlContentPart() {
        return H5Uri.SLEEP_RECORD_RECORD_SLEEP;
    }

}
