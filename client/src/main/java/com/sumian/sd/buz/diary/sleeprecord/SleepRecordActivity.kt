package com.sumian.sd.buz.diary.sleeprecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
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
class SleepRecordActivity : BaseActivity() {

    override fun initData() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_record_detail
    }

    companion object {
        private const val KEY_SLEEP_RECORD_TIME = "key_sleep_record_time"
        private const val KEY_SCROLL_TO_BOTTOM = "key_scroll_to_bottom"

        fun getLaunchIntent(context: Context, sleepRecordTime: Long): Intent {
            val bundle = Bundle()
            bundle.putLong(KEY_SLEEP_RECORD_TIME, sleepRecordTime)
            bundle.putBoolean(KEY_SCROLL_TO_BOTTOM, true)
            val intent = Intent(context, SleepRecordActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }

        fun launch(context: Context) {
            ActivityUtils.startActivity(getLaunchIntent(context, System.currentTimeMillis()))
        }
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        val sleepRecordFragment = SleepDiaryFragment.newInstance(
                intent?.getLongExtra(KEY_SLEEP_RECORD_TIME, 0L) ?: 0L)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, sleepRecordFragment)
                .commit()
    }
}