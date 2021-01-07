package com.sumian.sd.examine.main.me

import android.os.Handler
import android.util.Log
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import kotlinx.android.synthetic.main.examine_sleep_remind.*

class ExamineSleepRemindActivity : BaseActivity() {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineSleepRemindActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_sleep_remind
    }

    override fun initWidget() {
        super.initWidget()
        if (SPUtils.getInstance().getBoolean("examine_sleep_remind", false)) tb_reminder.setToggleOn() else tb_reminder.setToggleOff()
        lay_timer_container.isVisible = tb_reminder.isToggleOn
        tb_reminder.setOnToggleChanged {
            SPUtils.getInstance().put("examine_sleep_remind", it)
            SPUtils.getInstance().getInt("examine_sleep_remind_hours", -1)
            lay_timer_container.isVisible = it
        }
        examine_title_bar.setOnBackClickListener { finish() }
        val hours = SPUtils.getInstance().getInt("examine_sleep_remind_hours", -1)
        val minutes = SPUtils.getInstance().getInt("examine_sleep_remind_minutes", -1)
        if (hours >= 0) {
            picker_one.minValue = 0
            picker_one.maxValue = 23
            picker_one.value = hours
        }
        if (minutes >= 0) {
            picker_two.maxValue = 0
            picker_two.maxValue = 59
            picker_two.value = minutes
        }
        picker_one.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.i("MCJ", "one $newVal")
            if (oldVal != newVal) {
                SPUtils.getInstance().put("examine_sleep_remind_hours", newVal)
            }
            ToastHelper.show("已设置成功")
        }
        picker_two.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.i("MCJ", "two $newVal")
            if (oldVal != newVal) {
                SPUtils.getInstance().put("examine_sleep_remind_minutes", newVal)
            }
            ToastHelper.show("已设置成功")
        }
    }

}