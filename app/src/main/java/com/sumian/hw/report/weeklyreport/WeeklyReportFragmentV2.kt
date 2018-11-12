package com.sumian.hw.report.weeklyreport

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.report.weeklyreport.bean.SleepDurationReport
import com.sumian.hw.report.weeklyreport.bean.WeeklyReportResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_weekly_report_fragment_v2.*
import kotlinx.android.synthetic.main.weekly_report_calendar_bar.*
import java.util.*


class WeeklyReportFragmentV2 : BaseFragment() {
    private var mSelectTime = System.currentTimeMillis()
    private var mCalendarPopup: CalendarPopup? = null

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
        refresh_layout.setOnRefreshListener { queryData(mSelectTime) }
        iv_pre.setOnClickListener { nextOrPrePage(false) }
        iv_next.setOnClickListener { nextOrPrePage(true) }
        tv_week_quantum.setOnClickListener { showCalendarPop() }
    }

    private fun showCalendarPop() {
        mCalendarPopup = CalendarPopup(activity!!, object : CalendarPopup.DataLoader {
            override fun loadData(startMonthTime: Long, monthCount: Int, isInit: Boolean) {
                val map = HashMap<String, Any>(0)
                map["date"] = (startMonthTime / 1000).toInt()
                map["page_size"] = monthCount
                map["is_include"] = if (isInit) 1 else 0
                val call = AppManager.getSdHttpService().getCalendarSleepReport(map)
                addCall(call)
                call.enqueue(object : BaseSdResponseCallback<JsonObject>() {
                    override fun onSuccess(response: JsonObject?) {
                        val hasDataDays = HashSet<Long>()
                        val jsonObject = JSON.parseObject(response.toString())
                        val entries = jsonObject.entries
                        for ((_, value) in entries) {
                            if (value is JSONArray) {
                                val calendarItemSleepReports = value.toJavaList(CalendarItemSleepReport::class.java)
                                for (report in calendarItemSleepReports) {
                                    hasDataDays.add(report.dateInMillis)
                                }
                            }
                        }
                        mCalendarPopup?.addMonthAndData(startMonthTime, hasDataDays, isInit)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        LogUtils.d(errorResponse.message)
                    }
                })
            }
        })
        mCalendarPopup?.let {
            it.setOnDateClickListener(object : CalendarView.OnDateClickListener {
                override fun onDateClick(time: Long) {
                    queryData(time)
                }
            })
            it.setSelectDayTime(mSelectTime)
            it.setWeekMode(true)
            it.showAsDropDown(vg_calendar_bar, 0, resources.getDimension(R.dimen.space_10).toInt())
        }

    }


    private fun nextOrPrePage(next: Boolean) {
        queryData(mSelectTime + DateUtils.WEEK_IN_MILLIS * if (next) 1 else -1)
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
        mSelectTime = time
        val dayStartTime = TimeUtil.getDayStartTime(System.currentTimeMillis())
        val weekStartDayTime = TimeUtil.getWeekStartDayTime(time)
        val weekEndDayTime = TimeUtil.getWeekEndDayTime(time)
        tv_week_quantum.text = "${formatDate(weekStartDayTime)} - ${formatDate(weekEndDayTime)}"
        iv_next.visibility = if (dayStartTime < weekEndDayTime) View.GONE else View.VISIBLE
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
