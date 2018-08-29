package com.sumian.sd.setting.remind

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseActivity
import com.sumian.hw.network.callback.BaseResponseCallback
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.setting.remind.bean.Reminder
import com.sumian.sd.setting.remind.bean.ReminderListResponse
import kotlinx.android.synthetic.main.activity_remind_setting.*

class RemindSettingActivity : BaseActivity() {
    private var mReminder: Reminder? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_remind_setting
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        sdv_sleep_diary_remind.setOnClickListener { SleepDiaryRemindSettingActivity.launch() }
    }

    override fun onStart() {
        super.onStart()
        querySleepDiaryReminder()
    }

    private fun querySleepDiaryReminder() {
        AppManager.getHttpService().getReminderList(2)
                .enqueue(object : BaseResponseCallback<ReminderListResponse>() {
                    override fun onSuccess(response: ReminderListResponse?) {
                        mReminder = response?.data?.get(0)
                        sdv_sleep_diary_remind.setContent(
                                if (mReminder == null || !mReminder!!.isEnable())
                                    resources.getString(R.string.not_set_yet)
                                else
                                    mReminder!!.getReminderHHmm())
                    }

                    override fun onFailure(code: Int, message: String?) {
                        LogUtils.d(message)
                    }
                })
    }
}
