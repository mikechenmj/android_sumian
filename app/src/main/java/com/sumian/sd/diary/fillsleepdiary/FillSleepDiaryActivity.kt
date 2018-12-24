package com.sumian.sd.diary.fillsleepdiary

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import com.sumian.sd.diary.fillsleepdiary.fragment.ChooseSleepTimeFragment
import com.sumian.sd.diary.fillsleepdiary.fragment.MorningFeelingFragment
import com.sumian.sd.diary.fillsleepdiary.fragment.NightWakeOrDaySleepFragment
import com.sumian.sd.diary.fillsleepdiary.fragment.PillsFragment

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 11:50
 * desc   :
 * version: 1.0
 */
class FillSleepDiaryActivity : BasePresenterActivity<IPresenter>(), FillDiaryViewModel.SwitchProgressListener {
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

    override fun initData() {
        super.initData()
        mFillDiaryViewViewModel.mSwitchProgressListener = this
    }

    override fun switchProgress(index: Int, next: Boolean) {
        if (index < 0) {
            finish()
        }
        if (next) {
            addFragment(createFragment(index))
        } else {
            popFragment()
        }
    }

    private fun createFragment(index: Int): Fragment {
        return when (index) {
            0 -> ChooseSleepTimeFragment.newInstance(0, ChooseSleepTimeFragment.TYPE_SLEEP_TIME)
            1 -> ChooseSleepTimeFragment.newInstance(1, ChooseSleepTimeFragment.TYPE_FALL_ASLEEP_TIME)
            2 -> ChooseSleepTimeFragment.newInstance(2, ChooseSleepTimeFragment.TYPE_WAKEUP_TIME)
            3 -> ChooseSleepTimeFragment.newInstance(3, ChooseSleepTimeFragment.TYPE_GET_UP_TIME)
            4 -> NightWakeOrDaySleepFragment.newInstance(4, NightWakeOrDaySleepFragment.TYPE_NIGHT_WAKE)
            5 -> NightWakeOrDaySleepFragment.newInstance(5, NightWakeOrDaySleepFragment.TYPE_DAY_SLEEP)
            6 -> PillsFragment.newInstance(6)
            7 -> MorningFeelingFragment.newInstance(7)
            8 -> ChooseSleepTimeFragment.newInstance(1, ChooseSleepTimeFragment.TYPE_GET_UP_TIME)
            else -> ChooseSleepTimeFragment.newInstance(0, ChooseSleepTimeFragment.TYPE_SLEEP_TIME)
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