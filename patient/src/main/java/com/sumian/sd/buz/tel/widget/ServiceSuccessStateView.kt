package com.sumian.sd.buz.tel.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.widget.voice.IVisible
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_service_submit_success.view.*

@Suppress("DEPRECATION")
class ServiceSuccessStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), View.OnClickListener, IVisible {

    private var onServiceSuccessCallback: OnServiceSuccessCallback? = null

    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
        setBackgroundColor(resources.getColor(R.color.b1_color_day))
        setPadding(resources.getDimensionPixelOffset(R.dimen.space_36), 0, resources.getDimensionPixelOffset(R.dimen.space_36), 0)
        View.inflate(context, R.layout.lay_service_submit_success, this)
        bt_show_service.setOnClickListener(this)
        tv_go_back_home.setOnClickListener(this)
        hide()
    }

    fun setOnServiceSuccessCallback(onServiceSuccessCallback: OnServiceSuccessCallback): ServiceSuccessStateView {
        this.onServiceSuccessCallback = onServiceSuccessCallback
        return this
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_show_service -> {
                onServiceSuccessCallback?.showServiceDetailCallback()
            }
            R.id.tv_go_back_home -> {
                onServiceSuccessCallback?.goBackHome()
            }
        }
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }

    interface OnServiceSuccessCallback {

        fun showServiceDetailCallback()

        fun goBackHome()
    }

}