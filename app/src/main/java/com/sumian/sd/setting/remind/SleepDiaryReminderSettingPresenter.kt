package com.sumian.sd.setting.remind

import com.blankj.utilcode.util.LogUtils
import com.sumian.hw.network.callback.BaseResponseCallback
import com.sumian.sd.app.AppManager
import com.sumian.sd.setting.remind.bean.Reminder
import com.sumian.sd.setting.remind.bean.ReminderListResponse
import retrofit2.Call

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/29 14:56
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepDiaryReminderSettingPresenter(val view: SleepDiaryReminderSettingContract.View) : SleepDiaryReminderSettingContract.Presenter {
    private val mCalls = ArrayList<Call<*>>()

    override fun release() {
        for (call in mCalls) {
            call.cancel()
        }
        mCalls.clear()
    }

    override fun queryReminder() {
        val call = AppManager.getHttpService().getReminderList(2)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<ReminderListResponse>() {
            override fun onSuccess(response: ReminderListResponse?) {
                view.updateReminder(response?.data?.get(0))
            }

            override fun onFailure(code: Int, message: String?) {
                LogUtils.d(message)
            }
        })
    }

    override fun addReminder(timeInSecond: Int) {
        val call = AppManager.getHttpService().addReminder(remindAtInSecond = timeInSecond)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<Reminder>() {
            override fun onSuccess(response: Reminder?) {
                view.updateReminder(reminder = response)
            }

            override fun onFailure(code: Int, message: String?) {
                LogUtils.d(message)
            }
        })
    }

    override fun modifyReminder(reminderId: Int, timeInSecond: Int, enable: Boolean) {
        val call = AppManager.getHttpService().modifyReminder(reminderId, timeInSecond, if (enable) 1 else 0)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<Reminder>() {
            override fun onSuccess(response: Reminder?) {
                view.updateReminder(reminder = response)
            }

            override fun onFailure(code: Int, message: String?) {
                LogUtils.d(message)
            }
        })
    }
}