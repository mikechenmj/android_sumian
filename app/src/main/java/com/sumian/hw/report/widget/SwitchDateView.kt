package com.sumian.hw.report.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.hw_lay_switch_date_view.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

class SwitchDateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var unixTime: Long = 0
        set(unixTime) {
            field = unixTime
            if (todayUnixTime == unixTime) {
                iv_next.visibility = View.INVISIBLE
            } else {
                iv_next.visibility = View.VISIBLE
            }
            this.tv_date.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(unixTime * 1000L))
        }

    private var mOnSwitchDateListener: OnSwitchDateListener? = null

    val todayUnixTime: Long
        get() {
            val instance = Calendar.getInstance()
            val year = instance.get(Calendar.YEAR)
            val month = instance.get(Calendar.MONTH)
            val date = instance.get(Calendar.DATE)
            instance.set(year, month, date, 0, 0, 0)
            return instance.timeInMillis / 1000L
        }

    init {
        initView(context)
        tv_date.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.hw_lay_switch_date_view, this)
        iv_pre.setOnClickListener(this)
        iv_next.setOnClickListener(this)
    }

    fun setOnSwitchDateListener(onSwitchDateListener: OnSwitchDateListener): SwitchDateView {
        mOnSwitchDateListener = onSwitchDateListener
        return this
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_pre -> {
                mOnSwitchDateListener?.onScrollToTime(unixTime - 60 * 60 * 24)
            }
            R.id.iv_next -> {
                mOnSwitchDateListener?.onScrollToTime(unixTime + 60 * 60 * 24)
            }
        }
    }

    interface OnSwitchDateListener {

        fun onScrollToTime(unixTime: Long)

    }
}
