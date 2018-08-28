package com.sumian.sd.setting

import android.view.View
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.widget.sheet.SelectTimeHHmmBottomSheet
import kotlinx.android.synthetic.main.activity_sleep_diary_remind_setting.*

class SleepDiaryRemindSettingActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_diary_remind_setting
    }

    override fun initWidget() {
        super.initWidget()
        sdv_sleep_diary_remind.setOnCheckedChangeListener { button, checked ->
            run {
                fl_remind_time.visibility = if (checked) View.VISIBLE else View.GONE
                if (checked) {
                    showTimePicker()
                }
            }
        }
        fl_remind_time.setOnClickListener { showTimePicker() }
    }

    private fun showTimePicker() {
        SelectTimeHHmmBottomSheet(this, R.string.set_remind_time, 8, 0, object : SelectTimeHHmmBottomSheet.OnTimePickedListener {
            override fun onTimePicked(hour: Int, minute: Int) {
                tv_time.text = getString(R.string.pattern_hh_mm).format(hour, minute)
            }
        }).show()
    }
}
