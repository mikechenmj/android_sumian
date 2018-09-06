package com.sumian.sd.setting.remind

import android.content.Context
import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.R
import com.sumian.sd.setting.remind.bean.Reminder
import com.sumian.sd.utils.TimeUtil
import com.sumian.sd.widget.sheet.SelectTimeHHmmBottomSheet
import kotlinx.android.synthetic.main.activity_sleep_diary_remind_setting.*

class SleepDiaryRemindSettingActivity :
        BasePresenterActivity<SleepDiaryReminderSettingContract.Presenter>(),
        SleepDiaryReminderSettingContract.View {

    companion object {
        private const val KEY_REMINDER = "key_reminder"
        private const val DEFAULT_HOUR = 9
        private const val DEFAULT_MINUTE = 30

        fun launch(context: Context, reminder: Reminder?) {
            val intent = Intent(context, SleepDiaryRemindSettingActivity::class.java)
            intent.putExtra(KEY_REMINDER, reminder)
            ActivityUtils.startActivity(intent)
        }

        fun launch() {
            ActivityUtils.startActivity(SleepDiaryRemindSettingActivity::class.java)
        }
    }

    private var mReminder: Reminder? = null
    private var mOnTimePicked = false
    private var mSwitchPendingOff = false  // switch 点击后，如果没有pick 时间，则会回滚。

    init {
        mPresenter = SleepDiaryReminderSettingPresenter(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_diary_remind_setting
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        sdv_sleep_diary_remind.setOnCheckedChangeListener { button, checked ->
            run {
                fl_remind_time.visibility = if (checked) View.VISIBLE else View.GONE
                if (checked) {
                    mSwitchPendingOff = true
                    showTimePicker()
                } else {
                    if (mReminder != null) {
                        mPresenter?.modifyReminder(mReminder!!.id, mReminder!!.getRemindAtUnixTime(), false)
                    }
                }
            }
        }
        fl_remind_time.setOnClickListener { showTimePicker() }
    }

    override fun initData() {
        super.initData()
        mPresenter?.queryReminder()
    }

    override fun updateReminder(reminder: Reminder?) {
        mReminder = reminder
        val reminderEnable = reminder != null && reminder.isEnable()
        sdv_sleep_diary_remind.setSwitchCheckedWithoutCallback(reminderEnable)
        fl_remind_time.visibility = if (reminderEnable) View.VISIBLE else View.GONE
        tv_time.text = reminder?.getReminderHHmm()
    }

    private fun showTimePicker() {
        mOnTimePicked = false
        val initHour = mReminder?.getRemindAtHour() ?: DEFAULT_HOUR
        val initMinute = mReminder?.getReminderAtMinute() ?: DEFAULT_MINUTE
        val bottomSheet = SelectTimeHHmmBottomSheet(this, R.string.set_remind_time, initHour, initMinute,
                object : SelectTimeHHmmBottomSheet.OnTimePickedListener {
                    override fun onTimePicked(hour: Int, minute: Int) {
                        tv_time.text = getString(R.string.pattern_hh_mm).format(hour, minute)
                        addOrUpdateReminder(hour, minute)
                        mOnTimePicked = true
                    }
                })
        bottomSheet.setOnDismissListener {
            if (!mOnTimePicked && mSwitchPendingOff) {
                sdv_sleep_diary_remind.setSwitchCheckedWithoutCallback(false)
                fl_remind_time.visibility = View.GONE
            }
        }
        bottomSheet.show()
    }

    fun addOrUpdateReminder(hour: Int, minute: Int) {
        if (mReminder != null) {
            mPresenter?.modifyReminder(mReminder!!.id, getUnixTime(hour, minute), true)
        } else {
            mPresenter?.addReminder(getUnixTime(hour, minute))
        }
    }

    private fun getUnixTime(hour: Int, minute: Int): Int {
        return TimeUtil.getUnixTimeFromHourAndMinute(hour, minute)
    }
}
