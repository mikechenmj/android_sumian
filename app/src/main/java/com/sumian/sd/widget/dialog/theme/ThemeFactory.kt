package com.sumian.sd.widget.dialog.theme

/**
 * Created by dq
 *
 * on 2018/8/17
 *
 * desc:主题工厂
 */
object ThemeFactory {

    @JvmStatic
    fun create(clx: Class<out ITheme>): ITheme {
        return clx.newInstance()
    }

}