package com.sumian.sddoctor.booking.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sumian.sddoctor.R
import com.sumian.sddoctor.booking.bean.Booking
import com.sumian.sddoctor.util.TimeUtil
import kotlinx.android.synthetic.main.view_book_item.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 14:22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class BookingItemView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_book_item, this, true)
    }

    fun showTimeLineTop(show: Boolean) {
        v_time_line_top.visibility = if (show) VISIBLE else INVISIBLE
    }

    fun showTimeLineBottom(show: Boolean) {
        v_time_line_bottom.visibility = if (show) VISIBLE else INVISIBLE
    }

    fun showBottomDivider(show: Boolean) {
        v_bottom_divider.visibility = if (show) VISIBLE else INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun setBookingData(booking: Booking) {
        tv_time.text = getTime(booking)
        tv_name.text = booking.user.getNameOrNickname()
        v_time_line_dot.isActivated = isTimeLineDotActivate(booking)
    }

    private fun getTime(booking: Booking): String {
        val pattern = "HH:mm"
        val planStartTimeStr = TimeUtil.formatDate(pattern, booking.getPlanStartAtInMillis())
        val planEnfTimeStr = TimeUtil.formatDate(pattern, booking.getPlanEndAtInMillis())
        return "$planStartTimeStr-$planEnfTimeStr"
    }

    //    const val STATUS_WAIT_CONFIRM = 0;
    //    const val STATUS_ALREADY_CONFIRM = 1;
    //    const val STATUS_GOING = 2;
    //    const val STATUS_CALLING = 3;
    //    const val STATUS_COMPLETE = 4;
    //    const val STATUS_CLOSE = 5;
    //    const val STATUS_HANG = 6;
    //    const val STATUS_CANCELED = 7;
    //    const val STATUS_FINISH = 8;
    //    不同颜色对应的状态：
    //    灰色（#EDEFF0/L1）:对应【已确认】【进行中】【通话中】【已挂起】状态；
    //    蓝色（#6595F4/B3）：对应【已结束】【已取消】【已完成】状态。
    private fun isTimeLineDotActivate(booking: Booking): Boolean {
        return when (booking.status) {
            Booking.STATUS_FINISH, Booking.STATUS_CANCELED, Booking.STATUS_COMPLETE, Booking.STATUS_WAIT_CONFIRM -> true
            else -> false
        }
    }
}