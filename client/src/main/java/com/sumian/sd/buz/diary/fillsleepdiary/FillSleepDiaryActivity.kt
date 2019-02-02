package com.sumian.sd.buz.diary.fillsleepdiary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.buz.diary.event.SleepRecordFilledEvent
import com.sumian.sd.buz.diary.fillsleepdiary.fragment.*
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.common.utils.EventBusUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 11:50
 * desc   :
 * version: 1.0
 */
class FillSleepDiaryActivity : BaseActivity(), FillDiaryViewModel.ProgressListener {
    companion object {
        private const val KEY_TIME = "time"
        private const val KEY_RESPONSE_DATA = "response_data"

        fun startForResult(fragment: Fragment, time: Long, requestCode: Int) {
            val intent = getLaunchIntent(time)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun startForResult(activity: Activity, time: Long, requestCode: Int) {
            val intent = getLaunchIntent(time)
            activity.startActivityForResult(intent, requestCode)
        }

        private fun getLaunchIntent(time: Long): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), FillSleepDiaryActivity::class.java)
            intent.putExtra(KEY_TIME, time)
            return intent
        }

        fun getResponseData(intent: Intent): SleepRecord? {
            return JsonUtil.fromJson(intent.getStringExtra(KEY_RESPONSE_DATA), SleepRecord::class.java)
        }
    }

    private val mFillDiaryViewViewModel: FillDiaryViewModel by lazy {
        ViewModelProviders.of(this).get(FillDiaryViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fill_sleep_diary
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        addFragment(createFragment(0))
        setTitle(R.string.sleep_diary)
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mFillDiaryViewViewModel.mDayTime = bundle.getLong(KEY_TIME)
    }

    override fun initData() {
        super.initData()
        mFillDiaryViewViewModel.mProgressListener = this
    }

    override fun onProgressChange(index: Int, next: Boolean) {
        if (index < 0 || index == FillDiaryViewModel.TOTAL_PAGE) {
            finish()
            return
        }
        if (next) {
            addFragment(createFragment(index))
        } else {
            popFragment()
        }
    }

    override fun finishWithResult(sleepRecord: SleepRecord?) {
        EventBusUtil.postStickyEvent(SleepRecordFilledEvent())
        val intent = Intent()
        intent.putExtra(KEY_RESPONSE_DATA, JsonUtil.toJson(sleepRecord))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun createFragment(index: Int): Fragment {
        return when (index) {
            0 -> ChooseSleepTimeFragment.newInstance(index, ChooseSleepTimeFragment.TYPE_SLEEP_TIME)
            1 -> ChooseSleepTimeFragment.newInstance(index, ChooseSleepTimeFragment.TYPE_FALL_ASLEEP_TIME)
            2 -> ChooseSleepTimeFragment.newInstance(index, ChooseSleepTimeFragment.TYPE_WAKEUP_TIME)
            3 -> ChooseSleepTimeFragment.newInstance(index, ChooseSleepTimeFragment.TYPE_GET_UP_TIME)
            4 -> NightWakeOrDaySleepFragment.newInstance(index, NightWakeOrDaySleepFragment.TYPE_NIGHT_WAKE)
            5 -> NightWakeOrDaySleepFragment.newInstance(index, NightWakeOrDaySleepFragment.TYPE_DAY_SLEEP)
            6 -> SleepPillsFragment.newInstance(index)
            7 -> MorningFeelingFragment.newInstance(index)
            8 -> RemarkFragment.newInstance(index)
            else -> throw IllegalArgumentException("invalid index")
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.fl_fill_diary, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    private fun popFragment() {
        supportFragmentManager.popBackStack()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }

}