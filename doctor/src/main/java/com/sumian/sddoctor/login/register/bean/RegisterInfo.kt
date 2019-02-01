package com.sumian.sddoctor.login.register.bean

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 16:52
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class RegisterInfo(
        var name: String, // 2..12
        var hospital: String,// 4..18
        var department: String,
        var title: String
)