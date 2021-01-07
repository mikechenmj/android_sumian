package com.sumian.sd.examine.main.me

import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.examine_news_layout.*

class ExamineNewActivity : BaseActivity() {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineNewActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_news_layout
    }

    override fun initWidget() {
        super.initWidget()
        examine_title_bar.setOnBackClickListener { finish() }
    }
}