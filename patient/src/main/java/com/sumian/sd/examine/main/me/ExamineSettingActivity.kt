package com.sumian.sd.examine.main.me

import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.examine_setting.*

class ExamineSettingActivity : BaseActivity() {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineSettingActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_setting
    }

    override fun initWidget() {
        super.initWidget()
        lay_modify_pwd.setOnClickListener {
            Log.i("MCJ","lay_modify_pwd")
        }
        lay_unbind_sleepy.setOnClickListener {
            Log.i("MCJ","lay_unbind_sleepy")
        }
    }
}