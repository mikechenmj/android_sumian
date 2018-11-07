package com.sumian.hw.report.weeklyreport

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.report.weeklyreport.bean.WeeklyReportResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.hw.report.weeklyreport.bean.SleepDurationReport
import com.sumian.sd.R.string.data
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_weekly_report_fragment_v2.*
import kotlinx.android.synthetic.main.weekly_report_calendar_bar.*
import java.util.*


class WeeklyReportFragmentV2 : BaseFragment() {
    private var mCurrentTime = System.currentTimeMillis()

    override fun getLayoutId(): Int {
        return R.layout.fragment_weekly_report_fragment_v2
    }

    companion object {

        private const val KEY_SCROLL_TIME = "scroll_time"

        fun newInstance(): WeeklyReportFragmentV2 {
            return WeeklyReportFragmentV2()
        }

        fun newInstance(time: Long): WeeklyReportFragmentV2 {
            val bundle = Bundle()
            bundle.putLong(KEY_SCROLL_TIME, time)
            val weeklyReportFragment = WeeklyReportFragmentV2()
            weeklyReportFragment.arguments = bundle
            return weeklyReportFragment
        }
    }

    override fun initWidget() {
        super.initWidget()
        refresh_layout.setOnRefreshListener { queryData(mCurrentTime) }
        iv_pre.setOnClickListener { nextOrPrePage(false) }
        iv_next.setOnClickListener { nextOrPrePage(true) }
    }

    private fun nextOrPrePage(next: Boolean) {
        queryData(mCurrentTime + DateUtils.WEEK_IN_MILLIS * if (next) 1 else -1)
    }

    override fun initData() {
        super.initData()
        val initTime = getInitTime()
        updateCurrentTime(initTime)
        queryData(initTime)
    }

    private fun queryData(time: Long) {
        val call = AppManager.getSdHttpService().getWeeksSleepReportV2((time / 1000).toInt())
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<WeeklyReportResponse>() {
            override fun onSuccess(response: WeeklyReportResponse?) {
                val data: SleepDurationReport =
                        if ((response == null) || (response.list.isEmpty())) {
                            createEmptyData(time)
                        } else {
                            response.list[0]
                        }
                weekly_report_view.setData(data)
                updateCurrentTime(time)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                refresh_layout.hideRefreshAnim()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentTime(time: Long) {
        mCurrentTime = time
        val weekStartDayTime = TimeUtil.getWeekStartDayTime(time)
        val weekEndDayTime = TimeUtil.getWeekEndDayTime(time)
        tv_week_quantum.text = "${formatDate(weekStartDayTime)} - ${formatDate(weekEndDayTime)}"
    }

    private fun formatDate(time: Long): CharSequence? {
        return DateFormat.format("MM-dd", Date(time))
    }

    private fun createEmptyData(time: Long): SleepDurationReport {
        return SleepDurationReport.createFromTime(time)
    }

    private fun getInitTime(): Long {
        return arguments?.getLong(KEY_SCROLL_TIME) ?: System.currentTimeMillis()
    }
}
