package com.sumian.sd.setting.remind

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.setting.remind.bean.Reminder
import com.sumian.sd.setting.remind.bean.ReminderListResponse
import kotlinx.android.synthetic.main.activity_remind_setting.*

class RemindSettingActivity : BaseActivity() {
    private var mReminder: Reminder? = null

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.startActivity(RemindSettingActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_remind_setting
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        sdv_sleep_diary_remind.setOnClickListener { SleepDiaryRemindSettingActivity.launch(Reminder.SLEEP_DIARY_TYPE) }
        sdv_relaxation_training.setOnClickListener { SleepDiaryRemindSettingActivity.launch(Reminder.RELAXATION_TRAINING_TYPE) }
    }

    override fun onStart() {
        super.onStart()
        querySleepDiaryReminder(Reminder.SLEEP_DIARY_TYPE)
        querySleepDiaryReminder(Reminder.RELAXATION_TRAINING_TYPE)
    }

    private fun querySleepDiaryReminder(reminderType: Int = Reminder.SLEEP_DIARY_TYPE) {
        AppManager
                .getSdHttpService()
                .getReminderList(reminderType)
                .enqueue(object : BaseSdResponseCallback<ReminderListResponse>() {
                    override fun onFailure(errorResponse: ErrorResponse) {
                        LogUtils.d(errorResponse.message)
                    }

                    override fun onSuccess(response: ReminderListResponse?) {
                        mReminder = response?.getReminder()
                        when (reminderType) {
                            Reminder.SLEEP_DIARY_TYPE -> {
                                sdv_sleep_diary_remind.setContent(formatReminder())
                            }
                            Reminder.RELAXATION_TRAINING_TYPE -> {
                                sdv_relaxation_training.setContent(formatReminder())
                            }
                        }
                    }
                })
    }

    private fun formatReminder(): String? {
        return if (mReminder == null || !mReminder!!.isEnable())
            resources.getString(R.string.not_set_yet)
        else
            mReminder!!.getReminderHHmm()
    }
}
