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
                   var need_force_update: Boolean, var show_update_mode: Boolean) {
}