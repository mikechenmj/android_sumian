package com.sumian.sleepdoctor.homepage

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.ActivityLauncher
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.sleepRecord.FillSleepRecordActivity
import com.sumian.sleepdoctor.sleepRecord.RecordFragment.REQUEST_CODE_FILL_SLEEP_RECORD
import com.sumian.sleepdoctor.sleepRecord.SleepRecordDetailActivity
import kotlinx.android.synthetic.main.fragment_homepage.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/10 15:22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HomepageFragment : BaseFragment<HomepageContract.Presenter>(), HomepageContract.View, ActivityLauncher {
    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    companion object {
        const val REQUEST_CODE_FILL_SLEEP_RECORD = 1
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        sleep_record_view.setOnClickRightArrowListener { ActivityUtils.startActivity(SleepRecordDetailActivity::class.java) }
        sleep_record_view.setOnClickFillSleepRecordBtnListener { FillSleepRecordActivity.launchForResult(this, System.currentTimeMillis(), REQUEST_CODE_FILL_SLEEP_RECORD) }
    }
}