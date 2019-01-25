package com.sumian.sd.diary.fillsleepdiary

import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/1/25 15:11
 * desc   :
 * version: 1.0
 */
class SleepDiaryActivity : BasePresenterActivity<IPresenter>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_diary
    }
}