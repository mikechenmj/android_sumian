package com.sumian.sddoctor.booking.bean

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/2 18:07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class BookingStatus {
    // 状态 0:待确认，1：已确认 2：进行中 3：通话中 4：已完成 5：已关闭 6：已挂起 7：已取消 8：已结束
    companion object {
        const val WAITING_CONFIRM = 0
        const val CONFIRMED = 1
        const val GOING = 2
        const val CALLING = 3
        const val COMPLETE = 4
        const val CLOSED = 5
        const val HANGING = 6
        const val CANCELED = 7
        const val FINISHED = 8
    }
}