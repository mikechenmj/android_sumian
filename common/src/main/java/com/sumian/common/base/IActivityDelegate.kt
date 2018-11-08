package com.sumian.common.base

import android.content.Intent
import android.os.Bundle

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 17:43
 * desc   :
 * version: 1.0
 */
interface IActivityDelegate {
    fun onCreate(savedInstanceState: Bundle?) {
    }

    fun onNewIntent(intent: Intent?) {
    }

    fun onStart() {
    }

    fun onResume() {
    }

    fun onPause() {
    }

    fun onStop() {
    }

    fun onDestroy() {
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

}