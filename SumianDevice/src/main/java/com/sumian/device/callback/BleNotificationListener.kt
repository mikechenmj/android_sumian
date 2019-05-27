package com.sumian.device.callback

interface BleNotificationListener {
    fun onNotification(data: ByteArray, hexString: String)
}