package com.sumian.sd.buz.setting.remind

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.R
import com.sumian.sd.buz.setting.remind.bean.Reminder
import com.sumian.sd.common.utils.TimeUtil
import com.sumian.sd.widget.sheet.SelectTimeHHmmBottomSheet
import kotlinx.android.synthetic.main.activity_sleep_diary_remind_setting.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class SleepDiaryRemindSettingActivity : BasePresenterActivity<SleepDiaryReminderSettingContract.Presenter>(),
        SleepDiaryReminderSettingContract.View {

    companion object {
        private const val KEY_REMINDER = "key_reminder"
        private const val DEFAULT_HOUR = 9
        private const val DEFAULT_MINUTE = 30

        @JvmStatic
        fun launch(reminderType: Int) {
            ActivityUtils.getTopActivity()?.let {
                val intent = Intent(it, SleepDiaryRemindSettingActivity::class.java)
                intent.putExtra(KEY_REMINDER, reminderType)
                it.startActivity(intent)
            }
        }

        @JvmStatic
        fun launch() {
            ActivityUtils.startActivity(SleepDiaryRemindSettingActivity::class.java)
        }
    }

    private var mReminder: Reminder? = null
    private var mReminderType: Int = Reminder.TYPE_SLEEP_DIARY
    private var mOnTimePicked = false
    private var mSwitchPendingOff = false  // switch 点击后，如果没有pick 时间，则会回滚。

    init {
        mPresenter = SleepDiaryReminderSettingPresenter(this)
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mReminderType = bundle.getInt(KEY_REMINDER, Reminder.TYPE_SLEEP_DIARY)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_diary_remind_setting
    }

    override fun initWidget() {
        super.initWidget()
        when (mReminderType) {
            Reminder.TYPE_SLEEP_DIARY -> {
                title_bar.setTitle(R.string.sleep_diary_remind)
                sdv_sleep_diary_remind.setLabel(getString(R.string.sleep_diary_remind))
                tv_open_sleep_diary_remind_hint.setText(R.string.open_sleep_diary_remind_hint)
            }
            Reminder.TYPE_RELAXATION_TRAINING -> {
                title_bar.setTitle(R.string.sd_relaxation_training)
                sdv_sleep_diary_remind.setLabel(getString(R.string.sd_relaxation_training))
                tv_open_sleep_diary_remind_hint.setText(R.string.open_relaxation_training_hint)
            }
            Reminder.TYPE_ANXIETY -> {
                title_bar.setTitle(R.string.anxiety_time_reminder)
                sdv_sleep_diary_remind.setLabel(getString(R.string.anxiety_time_reminder))
                tv_open_sleep_diary_remind_hint.setText(R.string.anxiety_time_reminder_hint)
            }
        }
        title_bar.setOnBackClickListener { onBackPressed() }
        sdv_sleep_diary_remind.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            fl_remind_time.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                mSwitchPendingOff = true
                showTimePicker()
            } else {
                if (mReminder != null) {
                    mPresenter?.modifyReminder(mReminder!!.id, mReminder!!.getRemindAtUnixTime(), false)
                }
            }
        })
        fl_remind_time.setOnClickListener { showTimePicker() }
    }

    override fun initData() {
        super.initData()
        mPresenter?.queryReminder(mReminderType)
    }

    override fun updateReminder(reminder: Reminder?) {
        mSwitchPendingOff = false
        mReminder = reminder
        val reminderEnable = reminder != null && reminder.isEnable()
        sdv_sleep_diary_remind.setSwitchCheckedWithoutCallback(reminderEnable)
        fl_remind_time.visibility = if (reminderEnable) View.VISIBLE else View.GONE
        tv_time.text = reminder?.getReminderHHmm()
    }

    override fun rollbackReminderUI() {
        updateReminder(mReminder)
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
            mPresenter?.addReminder(mReminderType, getUnixTime(hour, minute))
        }
    }

    private fun getUnixTime(hour: Int, minute: Int): Int {
        return TimeUtil.getUnixTimeFromHourAndMinute(hour, minute)
    }
}
