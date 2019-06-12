package com.sumian.sd.buz.version.bean

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 14:31
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */

data class Version(var version: String?,
                   var description: String?,
                   var show_update_mode: Int)// 则是1（强制），如果中间版本中有推荐更新，则是0（推荐），否则为2（静默）
{
    fun isForceUpdate(): Boolean {
        return show_update_mode == 1
    }

    fun showShowDialog(): Boolean {
        return show_update_mode != 2
    }
}