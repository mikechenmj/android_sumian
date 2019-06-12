package com.sumian.sd.buz.version.delegate

import android.content.Context
import com.sumian.sd.app.App
import com.sumian.sd.common.utils.TimeUtil
import java.util.*

/**
 * Created by  sm
 *
 * on 2018/9/28
 *
 *desc:
 *
 */
object VersionDialogAlertUtils {

    private const val VERSION_ALERT_TIME = "version_alert_time"
    private const val VERSION_ALERT_TIME_KEY = "version_alert_time_key"

    fun saveAlertTime() {
        synchronized(VersionDialogAlertUtils::class.java) {
            val sp = App.getAppContext().getSharedPreferences(VERSION_ALERT_TIME, Context.MODE_PRIVATE)
            sp.edit().putLong(VERSION_ALERT_TIME_KEY, System.currentTimeMillis()).apply()
        }
    }

    fun clearAlertTime() {
        synchronized(VersionDialogAlertUtils::class.java) {
            val sp = App.getAppContext().getSharedPreferences(VERSION_ALERT_TIME, Context.MODE_PRIVATE)
            sp.edit().clear().apply()
        }
    }

    fun isCanAlert(): Boolean {
        synchronized(VersionDialogAlertUtils::class.java) {
            val sp = App.getAppContext().getSharedPreferences(VERSION_ALERT_TIME, Context.MODE_PRIVATE)
            val alertTime = sp.getLong(VERSION_ALERT_TIME_KEY, 0)
            val date = Date(alertTime)
            return alertTime == 0L || !TimeUtil.isInTheSameDay(date.time, alertTime)
        }
    }

}