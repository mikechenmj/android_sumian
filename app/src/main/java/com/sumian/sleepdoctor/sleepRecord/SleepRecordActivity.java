package com.sumian.sleepdoctor.sleepRecord;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseWebViewActivity;
import com.sumian.sleepdoctor.h5.H5Url;

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
    protected int initTitle() {
        return R.string.tab_record;
    }

    @Override
    protected String getUrlContentPart() {
        return H5Url.H5_URI_SLEEP_RECORD_RECORD_SLEEP;
    }

}
