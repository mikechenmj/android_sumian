package com.sumian.sd.buz.tel.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.buz.tel.bean.TelBooking
import com.sumian.sd.buz.tel.contract.TelBookingPublishContract
import com.sumian.sd.buz.tel.presenter.TelBookingPublishPresenter
import com.sumian.sd.buz.tel.sheet.TelBookingBottomSheet
import com.sumian.sd.buz.tel.widget.ServiceSuccessStateView
import kotlinx.android.synthetic.main.activity_main_publish_tel_booking.*
import java.util.*

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:电话预约服务
 *
 */
@Suppress("DEPRECATION")
class TelBookingPublishActivity : BaseViewModelActivity<TelBookingPublishPresenter>(), View.OnClickListener,
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

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
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
        et_input_ask_question.addTextChangedListener(object : EmptyTextWatcher() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                showEditContentLength(s, 20, tv_input_ask_count)
            }
        })
        et_input_ask_question_more.addTextChangedListener(object : EmptyTextWatcher() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                showEditContentLength(s, 400, tv_input_count)
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
        service_state_view.setOnServiceSuccessCallback(object : ServiceSuccessStateView.OnServiceSuccessCallback {
            override fun showServiceDetailCallback() {
                TelBookingDetailActivity.show(telBookingId = telBooking.id)
                finish()
            }

            override fun goBackHome() {
                finish()
            }

        }).show()
    }

    override fun onPublishTelBookingOrderFailed(error: String) {
        showCenterToast(error)
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

    private fun showEditContentLength(content: CharSequence?, maxLength: Int, textView: TextView) {
        val inputLength = content?.length ?: 0
        if (inputLength > maxLength) {
            textView.setTextColor(resources.getColor(R.color.t4_color))
        } else {
            textView.setTextColor(resources.getColor(R.color.t2_color))
        }
        textView.text = String.format(Locale.getDefault(), "%d%s%d", inputLength, '/', maxLength)
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
            showEditContentLength(it.add, 20, tv_input_ask_count)
            showEditContentLength(it.add, 400, tv_input_count)
        }
    }

    private fun showCenterToast(message: String) {
        ToastHelper.show(this, message, Gravity.CENTER)
    }
}