package com.sumian.sleepdoctor.push

import android.os.Bundle

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 9:57
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class PushData(
        var action: String? = "",
        var alert: String? = "",
        var scheme: String? = ""
) {
    companion object {
        fun create(bundle: Bundle): PushData {
            return PushData(
                    bundle.getString("action"),
                    bundle.getString("alert"),
                    bundle.getString("scheme")
            )
        }
    }

}
