package com.sumian.sd.buz.setting.remind

import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.setting.remind.bean.Reminder
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import retrofit2.Call

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/29 14:56
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepDiaryReminderSettingPresenter(val view: SleepDiaryReminderSettingContract.View) : BaseViewModel() {
    private val mCalls = ArrayList<Call<*>>()

     fun release() {
        for (call in mCalls) {
            if (call.isExecuted) {
                call.cancel()
            }
        }
        mCalls.clear()
    }

     fun queryReminder(reminderType: Int) {
        val call = AppManager.getSdHttpService().getReminderList(reminderType)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<Reminder>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<Reminder>?) {
                view.updateReminder(response?.let { if (it.data.isEmpty()) null else it.data[0] })
            }
        })
    }

     fun addReminder(reminderType: Int, timeInSecond: Int) {
        val call = AppManager.getSdHttpService().addReminder(reminderType, remindAtInSecond = timeInSecond)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<Reminder>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
                view.rollbackReminderUI()
            }

            override fun onSuccess(response: Reminder?) {
                view.updateReminder(reminder = response)
            }
        })
    }

     fun modifyReminder(reminderId: Int, timeInSecond: Int, enable: Boolean) {
        val call = AppManager.getSdHttpService().modifyReminder(reminderId, timeInSecond, if (enable) 1 else 0)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<Reminder>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
                view.rollbackReminderUI()
            }

            override fun onSuccess(response: Reminder?) {
                view.updateReminder(reminder = response)
            }
        })
    }
}