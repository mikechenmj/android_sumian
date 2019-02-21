package com.sumian.sd.buz.diary.fillsleepdiary

import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.buz.diary.sleeprecord.SleepRestrictionIntroductionDialogActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/25 15:11
 * desc   :
 * version: 1.0
 */
class SleepDiaryActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_diary
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.sleep_diary)
        SleepRestrictionIntroductionDialogActivity.start()
    }

    override fun showBackNav(): Boolean {
        return true
    }
}