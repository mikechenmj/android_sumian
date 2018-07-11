package com.sumian.sleepdoctor.record

import com.sumian.common.base.BaseActivity
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.activity_refund.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/11 9:47
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepRecordDetailActivity : BaseActivity() {
    override fun getContentView(): Int {
        return R.layout.activity_sleep_record_detail
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
    }
}