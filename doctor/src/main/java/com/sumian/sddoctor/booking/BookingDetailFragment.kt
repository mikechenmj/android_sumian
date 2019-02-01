package com.sumian.sddoctor.booking

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseFragment
import com.sumian.sddoctor.booking.bean.BookingDetail
import com.sumian.sddoctor.booking.bean.BookingStatus
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.CALLING
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.CANCELED
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.CLOSED
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.COMPLETE
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.CONFIRMED
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.FINISHED
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.GOING
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.HANGING
import com.sumian.sddoctor.booking.bean.BookingStatus.Companion.WAITING_CONFIRM
import com.sumian.sddoctor.event.BookingStatusChangeEvent
import com.sumian.sddoctor.homepage.FreeCallResponse
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.EventBusUtil
import com.sumian.sddoctor.util.TimeUtil
import com.sumian.sddoctor.widget.SumianAlertDialog
import kotlinx.android.synthetic.main.fragment_booking_detail.*

@Suppress("DEPRECATION")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/2 16:37
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class BookingDetailFragment : BaseFragment() {
    private val bookingId by lazy {
        arguments?.getInt(KEY_BOOKING_ID)
                ?: throw RuntimeException("Booking id not set yet")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_booking_detail
    }

    companion object {
        private const val KEY_BOOKING_ID = "KEY_BOOKING_ID"

        fun newInstance(bookingId: Int): BookingDetailFragment {
            val bundle = Bundle()
            bundle.putInt(KEY_BOOKING_ID, bookingId)
            val bookingInfoFragment = BookingDetailFragment()
            bookingInfoFragment.arguments = bundle
            return bookingInfoFragment
        }
    }

    override fun initData() {
        super.initData()
        loadBookingDetail()
    }

    private fun loadBookingDetail() {
        val call = AppManager.getHttpService().getBookingDetail(bookingId)
        addCall(call)
        showLoading()
        call.enqueue(object : BaseSdResponseCallback<BookingDetail>() {
            override fun onSuccess(response: BookingDetail?) {
                if (response == null) {
                    ToastUtils.showShort(R.string.error_unknown)
                    return
                }
                updateUI(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    private fun updateUI(bookingDetail: BookingDetail) {
        tv_order_status.setText(getStatusString(bookingDetail.status))
        tv_order_status.setTextColor(getStatusColor(bookingDetail.status))
        sdv_patient_name.setContentText(bookingDetail.user.getNameOrNickname())
        sdv_booking_time.setContentText(TimeUtil.formatDate("yyyy/MM/dd HH:mm", bookingDetail.getPlanStartAtInMillis()))
        sdv_booking_duration.setContentText(getString(R.string.xx_min, bookingDetail.getBookingDurationInMin()))
        tv_advisory_question.text = bookingDetail.consultingQuestion
        tv_supplementary_instruction.text = bookingDetail.add
        tv_anonymous_call.visibility = if (bookingDetail.status == BookingStatus.HANGING) VISIBLE else GONE
        updateRejectConfirmBtnVisibility(bookingDetail.status == WAITING_CONFIRM)
        tv_anonymous_call.setOnClickListener { showCallDialog(bookingDetail) }
        tv_reject.setOnClickListener { modifyBookingStatus(bookingDetail.id, false) }
        tv_confirm.setOnClickListener { modifyBookingStatus(bookingDetail.id, true) }
        tv_contact_support_staff.visibility = if (bookingDetail.status == CONFIRMED) VISIBLE else GONE
        tv_contact_support_staff.setOnClickListener { showContactSupportStaffDialog() }
    }

    private fun showContactSupportStaffDialog() {
        SumianAlertDialog(activity)
                .setTitle(R.string.contact_support_staff)
                .setMessage(R.string.update_booking_hint)
                .setRightBtn(R.string.confirm, null)
                .show()
    }

    private fun updateRejectConfirmBtnVisibility(isWaitingConfirm: Boolean) {
        tv_reject.visibility = if (isWaitingConfirm) VISIBLE else GONE
        tv_confirm.visibility = if (isWaitingConfirm) VISIBLE else GONE
    }

    private fun modifyBookingStatus(bookingId: Int, confirm: Boolean) {
        val status = if (confirm) 1 else 5
        val call = AppManager.getHttpService().modifyBookingStatus(bookingId, status)
        addCall(call)
        showLoading()
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                loadBookingDetail()
                EventBusUtil.postStickyEvent(BookingStatusChangeEvent())
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    private fun showCallDialog(bookingDetail: BookingDetail) {
        SumianAlertDialog(activity)
                .setTitle(R.string.call_or_not)
                .setMessage(getString(R.string.are_your_sure_to_call_the_patient, bookingDetail.user.getNameOrNickname()))
                .setLeftBtn(R.string.cancel, null)
                .whitenLeft()
                .setRightBtn(R.string.confirm) { callPatient(bookingDetail.userId) }
                .show()
    }

    private fun callPatient(userId: Int) {
        val call = AppManager.getHttpService().recallPatient(userId)
        addCall(call)
        showLoading()
        call.enqueue(object : BaseSdResponseCallback<FreeCallResponse>() {
            override fun onSuccess(response: FreeCallResponse?) {
                SumianAlertDialog(activity)
                        .setTitle(R.string.waiting_for_calling)
                        .setMessage(R.string.waiting_for_calling_hint)
                        .setLeftBtn(R.string.confirm, null)
                        .show()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    /**
     * 1.医生看到的订单状态
     *  ①待确认：【待确认】
     *  ②已确认：【已确认】【进行中】【通话中】
     *  ③已关闭：【已关闭】【已结束】
     *  ④挂起中：【已挂起】
     *  ⑤已完成：【已完成】【已取消】
     */
    private fun getStatusString(status: Int): Int {
        return when (status) {
            WAITING_CONFIRM -> R.string.waiting_confirm
            CONFIRMED, GOING, CALLING -> R.string.already_confirmed
            CLOSED, FINISHED -> R.string.already_closed
            HANGING -> R.string.hanging
            COMPLETE, CANCELED -> R.string.already_finish
            else -> throw RuntimeException("Wrong status")
        }
    }

    /**
     * 不同颜色对应的状态：
     * 灰色（#EDEFF0/L1）:对应【已确认】【进行中】【通话中】状态；
     * 蓝色（#6595F4/B3）：对应【待确认】【已结束】【已取消】【已关闭】【已完成】状态。
     * 红色（B8/#F0918E）：【已挂起】
     */
    private fun getStatusColor(status: Int): Int {
        val colorRes = when (status) {
            CONFIRMED, GOING, CALLING -> R.color.l2_color
            HANGING -> R.color.b8_color
            else -> R.color.b3_color
        }
        return resources.getColor(colorRes)
    }
}