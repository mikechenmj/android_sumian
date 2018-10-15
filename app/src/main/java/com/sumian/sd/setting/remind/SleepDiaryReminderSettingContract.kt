package com.sumian.sd.setting.remind

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.setting.remind.bean.Reminder

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/29 14:42
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepDiaryReminderSettingContract {
    interface View : BaseShowLoadingView {
        fun updateReminder(reminder: Reminder?)
        fun rollbackReminderUI()
    }

    interface Presenter : IPresenter {
        fun queryReminder()
        fun addReminder(timeInSecond: Int)
        fun modifyReminder(reminderId: Int, timeInSecond: Int, enable: Boolean)
        fun release()
    }
}