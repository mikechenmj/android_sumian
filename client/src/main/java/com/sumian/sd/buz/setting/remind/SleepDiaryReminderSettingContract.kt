package com.sumian.sd.buz.setting.remind

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.sd.buz.setting.remind.bean.Reminder

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

}