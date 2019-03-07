package com.sumian.sd.buz.sleepertalk

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/7 09:35
 * desc   :
 * version: 1.0
 */
class SleeperTalkListActivity : BaseViewModelActivity<BaseViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleeper_talk_list
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.sleeper_talk)
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(Intent(ActivityUtils.getTopActivity(), SleeperTalkListActivity::class.java))
        }
    }
}