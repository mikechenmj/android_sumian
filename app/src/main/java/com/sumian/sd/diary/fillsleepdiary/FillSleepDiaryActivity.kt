package com.sumian.sd.diary.fillsleepdiary

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 11:50
 * desc   :
 * version: 1.0
 */
class FillSleepDiaryActivity : BasePresenterActivity<IPresenter>(), FillDiaryModel.SwitchProgressListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_fill_sleep_diary
    }

    override fun initWidget() {
        super.initWidget()
    }

    override fun initData() {
        super.initData()
        val model = ViewModelProviders.of(this).get(FillDiaryModel::class.java)
        model.mSwitchProgressListener = this
    }

    override fun switchProgress(index: Int, next: Boolean) {
        if (next) {
            addFragment(createFragment(index))
        } else {
            popFragment()
        }
    }

    private fun createFragment(index: Int): Fragment {
        return when (index) {
            0 -> ChooseSleepTimeFragment.newInstance(ChooseSleepTimeFragment.TYPE_SLEEP_TIME)
            1 -> ChooseSleepTimeFragment.newInstance(ChooseSleepTimeFragment.TYPE_FALL_ASLEEP_TIME)
            2 -> ChooseSleepTimeFragment.newInstance(ChooseSleepTimeFragment.TYPE_WAKEUP_TIME)
            3 -> ChooseSleepTimeFragment.newInstance(ChooseSleepTimeFragment.TYPE_GET_UP_TIME)
            else -> ChooseSleepTimeFragment.newInstance(0)
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
}