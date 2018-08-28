package com.sumian.sd.setting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_remind_setting.*

class RemindSettingActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_remind_setting
    }

    override fun initWidget() {
        super.initWidget()
        sdv_sleep_diary_remind.setOnClickListener { ActivityUtils.startActivity(SleepDiaryRemindSettingActivity::class.java) }
    }


}
