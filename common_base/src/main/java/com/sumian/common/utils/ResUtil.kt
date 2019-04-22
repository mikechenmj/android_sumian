package com.sumian.common.utils

import android.net.Uri
import com.blankj.utilcode.util.AppUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/4/22 10:18
 * desc   :
 * version: 1.0
 */

class ResUtil {
    companion object {
        fun resIdToUrl(res: Int): String {
            return Uri.parse("android.resource://" + AppUtils.getAppPackageName() + "/" + res).toString()
        }
    }
}