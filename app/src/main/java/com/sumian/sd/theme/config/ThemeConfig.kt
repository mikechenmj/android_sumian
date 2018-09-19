package com.sumian.sd.theme.config

import android.content.Context
import com.sumian.sd.app.App
import com.sumian.sd.theme.ITheme

/**
 * Created by sm
 *
 * on 2018/9/13
 *
 * desc:
 *
 */
object ThemeConfig {

    private const val THEME_FILE_NAME = "ThemeConfig"

    private const val THEME_TYPE = "theme_type"

    fun applyThemeType(themeType: Int = ITheme.DAY_THEME) {
        synchronized(ThemeConfig::class.java) {
            val sp = App.getAppContext().getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE)
            sp.edit().putInt(THEME_TYPE, themeType).apply()
        }
    }

    fun isDayTheme(): Boolean {
        val sp = App.getAppContext().getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE)
        return sp.getInt(THEME_TYPE, ITheme.DAY_THEME) == ITheme.DAY_THEME
    }

}