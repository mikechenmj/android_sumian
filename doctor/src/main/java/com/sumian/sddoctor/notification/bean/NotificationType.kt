package com.sumian.sddoctor.notification.bean

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/2 14:00
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class NotificationType {
    companion object {
        const val CALL_BOOKING_NOTICE = "App\\Notifications\\CallBookingNotice"
        const val BOOKING_WAITING_CONFIRM = "App\\Notifications\\BookingWaitingConfirm"
        const val BOOKING_CONFIRMED = "App\\Notifications\\BookingConfirmed"
        const val BOOKING_CLOSED = "App\\Notifications\\BookingClosed"
    }
}