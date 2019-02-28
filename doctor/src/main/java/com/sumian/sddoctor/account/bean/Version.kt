package com.sumian.sddoctor.account.bean

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
                   var need_force_update: Boolean,     // 则是1（强制），如果中间版本中有推荐更新，则是0（推荐），否则为2（静默）
                   var mode: Int) {
    fun isForceUpdate(): Boolean {
        return mode == 1
    }

    fun showShowDialog(): Boolean {
        return mode != 2
    }
}