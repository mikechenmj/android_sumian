package com.sumian.devicedemo.dfuDemo

import android.util.Log
import kotlinx.coroutines.channels.Channel

fun <T : Any?> T.log(tag: String): T {
    Log.i("MCJ", tag + this)
    return this
}

fun <T : Any?> T.negativeLog(tag: String): T {
    if (this == null) {
        Log.i("MCJ", tag + this)
    }
    return this
}

fun Boolean.negativeLog(tag: String): Boolean {
    if (!this) {
        Log.i("MCJ", tag)
    }
    return this

}

fun Channel<out Any>.isClose(): Boolean {
    return isClosedForSend || isClosedForReceive
}