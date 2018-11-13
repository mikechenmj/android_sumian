package com.sumian.sd.service.tel.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.service.tel.contract.TelBookingPublishContract
import com.sumian.sd.service.tel.presenter.TelBookingPublishPresenter
import com.sumian.sd.service.tel.sheet.TelBookingBottomSheet
import com.sumian.sd.service.util.TimeUtilV2
import com.sumian.sd.widget.adapter.SimpleTextWatchAdapter
import kotlinx.android.synthetic.main.activity_main_publish_tel_booking.*

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:电话预约服务
 *
 */
class TelBookingPublishActivity : BaseBackPresenterActivity<TelBookingPublishContract.Presenter>(), View.OnClickListener,
        TelBookingPublishContract.View, TelBookingBottomSheet.OnSelectTelBookingCallback {

    companion object {

        private const val EXTRA_TEL_BOOKING = "com.sumian.sd.extra.tel.booking"

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, TelBookingPublishActivity::class.java))
            }
        }

        @JvmStatic
        fun show(telBooking: TelBooking) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, TelBookingPublishActivity::class.java).apply {
                    putExtra(EXTRA_TEL_BOOKING, telBooking)
                })
            }
        }

    }

    private var mTelBooking: TelBooking? = null

    private var mTelBookingUnixTime: Int = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mTelBooking = bundle.getParcelable(EXTRA_TEL_BOOKING)
    }

    override fun getChildContentId(): Int {
        return R.layout.activity_main_publish_tel_booking
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = TelBookingPublishPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.tel_ask_detail)
        sdv_make_date.setOnClickListener(this)
        et_input_ask_question_more.addTextChangedListener(object : SimpleTextWatchAdapter() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_input_count.text = showEditContentLength(s.toString().trim())
            }
        })
        bt_submit.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        invalidTelBooking(mTelBooking)

        if (mTelBooking == null) {
            this.mPresenter?.getLatestTelBookingOrder()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sdv_make_date -> {
                TelBookingBottomSheet.show(fragmentManager = supportFragmentManager, telBookingTime = mTelBookingUnixTime, onSelectTelBookingCallback = this@TelBookingPublishActivity)
            }
            R.id.bt_submit -> {
                if (mTelBookingUnixTime <= 0) {
                    onCheckInputContentFailed("请选择预约时间")
                    return
                }
                mPresenter?.checkInputContent(et_input_ask_question.text.toString().trim(), et_input_ask_question_more.text.toString().trim())
            }
        }
    }

    override fun onGetLatestTelBookingOrderSuccess(latestTelBooking: TelBooking) {
        this.mTelBooking = latestTelBooking
        invalidTelBooking(telBooking = latestTelBooking)
    }

    override fun onGetLatestTelBookingOrderFailed(error: String) {
        onPublishTelBookingOrderFailed(error)
    }

    override fun onPublishTelBookingOrderSuccess(telBooking: TelBooking) {//publish success
        invalidTelBooking(telBooking)
        TelBookingDetailActivity.show(telBookingId = telBooking.id)
        finish()
    }

    override fun onPublishTelBookingOrderFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun onCheckInputContentFailed(error: String) {
        onPublishTelBookingOrderFailed(error)
    }

    override fun onCheckInputContentSuccess(consultingQuestion: String, add: String) {
        mPresenter?.publishTelBookingOrder(mTelBooking?.id!!, mTelBookingUnixTime, consultingQuestion, add, true)
    }

    override fun onSelectTelBookingTime(unixTime: Int): Boolean {
        this.mTelBookingUnixTime = unixTime
        sdv_make_date.setContent(TimeUtilV2.formatYYYYMMDDHHMM(unixTime))
        return true
    }

    private fun editSelectPosition(content: String?): Int {
        return if (TextUtils.isEmpty(content)) 0 else content?.length!!
    }

    private fun showEditContentLength(content: String?): String {
        return if (TextUtils.isEmpty(content)) "0/400" else "${content?.length}/400"
    }

    private fun invalidTelBooking(telBooking: TelBooking?) {
        telBooking?.let {
            if (it.plan_start_at > 0) {
                sdv_make_date.setContent(it.formatOrderPlanStartTimeYYYYMMDDHHMM())
            }
            sdv_duration.setContent(it.p_package.servicePackage.formatServiceLengthType())
            et_input_ask_question.setText(it.consulting_question)
            et_input_ask_question.setSelection(editSelectPosition(it.consulting_question))
            et_input_ask_question_more.setText(it.add)
            et_input_ask_question_more.setSelection(editSelectPosition(it.add))
            tv_input_count.text = showEditContentLength(it.add)
        }
    }
}