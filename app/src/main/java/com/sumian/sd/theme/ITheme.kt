package com.sumian.sd.theme

import android.content.res.Resources
import android.view.View

/**
 * Created by sm
 *
 * on 2018/9/13
 *
 * desc:
 *
 */
interface ITheme {

    companion object {

        const val DAY_THEME = 0x01
        const val NIGHT_THEME = 0x02
    }

    fun getView(): View

    fun setTheme(theme: Resources.Theme)

}