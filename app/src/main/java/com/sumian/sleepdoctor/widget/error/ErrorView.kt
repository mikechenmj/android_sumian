package com.sumian.sleepdoctor.widget.error

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_empty_and_error_view.view.*

class ErrorView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_empty_and_error_view, this)
    }

    fun showNotFoundError() {
        iv_error.setImageResource(R.mipmap.ic_empty_state_404)
        tv_error_title.setText(R.string.error_apology)
        tv_error_desc.setText(R.string.error_not_found_desc)
        show()
    }

    fun showReportError() {
        iv_error.setImageResource(R.mipmap.ic_empty_state_report)
        tv_error_title.setText(R.string.error_report)
        tv_error_desc.setText(R.string.error_not_found_desc)
        show()
    }

    fun showMessageCenterError() {
        iv_error.setImageResource(R.mipmap.ic_empty_state_news)
        tv_error_title.setText(R.string.error_message_center)
        tv_error_desc.setText(R.string.error_message_center_desc)
        show()
    }

    fun showNetError() {
        iv_error.setImageResource(R.mipmap.ic_empty_state_network_anomaly)
        tv_error_title.setText(R.string.error_network)
        tv_error_desc.setText(R.string.error_network_desc)
        show()
    }

    fun showAdvisoryError() {
        iv_error.setImageResource(R.mipmap.ic_empty_state_advisory)
        tv_error_title.setText(R.string.error_advisory)
        tv_error_desc.setText(R.string.error_advisory_desc)
        show()
    }

    fun hide() {
        iv_error.setImageResource(R.mipmap.ic_group_synchronizing)
        tv_error_title.setText(R.string.error_apology)
        tv_error_desc.setText(R.string.error_request_failed_hint)
        visibility = View.GONE
    }

    fun show() {
        visibility = View.VISIBLE
    }
}