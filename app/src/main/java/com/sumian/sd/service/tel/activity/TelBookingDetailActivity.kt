@file:Suppress("DEPRECATION")

package com.sumian.sd.service.tel.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.service.tel.contract.TelBookingDetailContract
import com.sumian.sd.service.tel.presenter.TelBookingDetailPresenter
import kotlinx.android.synthetic.main.activity_main_publish_tel_booking.*
import kotlinx.android.synthetic.main.activity_tel_booking_detail.*

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:电话预约详情
 *
 */
class TelBookingDetailActivity : BasePresenterActivity<TelBookingDetailContract.Presenter>(), TelBookingDetailContract.View {

    companion object {

        private const val EXTRA_TEL_BOOKING_ID = "com.sumian.sd.extra.tel.booking.id"

        fun show(telBookingId: Int) {
            ActivityUtils.startActivity(getLaunchIntent(telBookingId))
        }

        fun getLaunchIntent(telBookingId: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), TelBookingDetailActivity::class.java)
            intent.putExtra(EXTRA_TEL_BOOKING_ID, telBookingId)
            return intent
        }
    }

    private var mTelBookingId = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mTelBookingId = bundle.getInt(EXTRA_TEL_BOOKING_ID, 0)
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_tel_booking_detail
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = TelBookingDetailPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle("预约详情")
        sdv_border_status.goneMoreIcon()
        sdv_make_date.goneMoreIcon()
        divider.visibility = View.VISIBLE
        et_input_ask_question.isFocusable = false
        et_input_ask_question_more.isFocusable = false
        bt_submit.visibility = View.GONE
        tv_input_count.visibility = View.GONE
    }

    override fun initData() {
        super.initData()
        this.mPresenter?.getTelBookingDetail(mTelBookingId)
    }

    override fun onGetTelBookingDetailSuccess(telBooking: TelBooking) {
        if (telBooking.status == TelBooking.STATUS_7_CANCELED && telBooking.isNotUsed()) {
            tv_top_bar.text = ""
            tv_top_bar.setBackgroundColor(resources.getColor(R.color.b4_color_day))
            tv_top_bar.visibility = View.VISIBLE
            empty_error_view.visibility = View.VISIBLE
        } else {
            tv_top_bar.visibility = if (telBooking.showTopTips()) {
                tv_top_bar.text = telBooking.topMsg()
                tv_top_bar.setBackgroundColor(resources.getColor(R.color.b5_color_day))
                View.VISIBLE
            } else {
                tv_top_bar.text = ""
                View.GONE
            }
            scroll_view.visibility = View.VISIBLE
            sdv_border_status.visibility = View.VISIBLE
            sdv_border_status.setContent(telBooking.formatStatus())
            sdv_make_date.setContent(telBooking.formatOrderTimeYYYYMMDDHHMM())
            sdv_duration.setContent(telBooking.p_package.servicePackage.formatServiceLengthType())
            et_input_ask_question.setText(telBooking.consulting_question)
            et_input_ask_question_more.setText(telBooking.add)
        }
    }

    override fun onGetTelBookingDetailFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }
}