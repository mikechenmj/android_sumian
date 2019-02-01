package com.sumian.sddoctor.service.report.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.LogUtils
import com.google.gson.JsonObject
import com.sumian.common.base.BaseFragment
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.report.bean.DailyReport
import com.sumian.sddoctor.service.report.widget.calendar.CalendarItemSleepReport
import com.sumian.sddoctor.service.report.widget.calendar.calendarView.CalendarView
import com.sumian.sddoctor.service.report.widget.calendar.custom.CalendarPopup
import com.sumian.sddoctor.util.TimeUtil
import kotlinx.android.synthetic.main.fragment_main_today_repot.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/11 19:57
 * desc   :
 * version: 1.0
 */
class DailyReportFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, CalendarView.OnDateClickListener {

    companion object {
        const val ARGS_PATIENT_ID = "com.sumian.sddoctor.extras.patient.id"
        const val ARGS_PATIENT_SLEEP_DATA_TIME = "com.sumian.sddoctor.extras.patient.sleep.data.time"

        @JvmStatic
        fun newInstance(patientId: Int, sleepDateTime: Long): DailyReportFragment {
            return DailyReportFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARGS_PATIENT_ID, patientId)
                    putLong(ARGS_PATIENT_SLEEP_DATA_TIME, sleepDateTime)
                }
            }
        }
    }

    private var mSelectedTime = System.currentTimeMillis()
    private var mCalendarPopup: CalendarPopup? = null
    private var mPatientId = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPatientId = bundle.getInt(ARGS_PATIENT_ID, 0)
        this.mSelectedTime = bundle.getLong(ARGS_PATIENT_SLEEP_DATA_TIME, 0)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_today_repot
    }

    override fun initWidget() {
        super.initWidget()
        tv_date.setOnClickListener { showDatePopup(true) }
        iv_date_arrow.setOnClickListener { showDatePopup(true) }
        refresh.setOnRefreshListener(this)
        setTvDate(mSelectedTime)
    }

    override fun initData() {
        super.initData()
        updateCurrentTimeData()
    }

    override fun onRefresh() {
        updateCurrentTimeData()
    }

    private fun updateCurrentTimeData() {
        queryPatientSleepData(mSelectedTime)
    }

    override fun showLoading() {
        //super.showLoading()
        refresh.showRefreshAnim()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        refresh.hideRefreshAnim()
    }

    private fun queryPatientSleepData(time: Long) {
        showLoading()
        val call = AppManager
                .getHttpService()
                .getSleepReport((time / 1000).toInt(), mPatientId)

        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DailyReport>() {
            override fun onSuccess(response: DailyReport?) {
                response?.let {
                    updateDailyReport(it)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                val dailyReport = DailyReport()
                dailyReport.user_id = mPatientId
                dailyReport.date = (time / 1000L).toInt()
                updateDailyReport(dailyReport)
                ToastHelper.show(activity!!, errorResponse.message, Gravity.CENTER)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    private fun showDatePopup(show: Boolean) {
        iv_date_arrow.isActivated = show
        if (show) {
            mCalendarPopup = CalendarPopup(activity!!, object : CalendarPopup.DataLoader {
                override fun loadData(startMonthTime: Long, monthCount: Int, isInit: Boolean) {
                    queryDataForCalendar(startMonthTime, monthCount, isInit)
                }
            })
            mCalendarPopup?.setOnDateClickListener(this)
            mCalendarPopup?.setOnDismissListener { iv_date_arrow.isActivated = false }
            mCalendarPopup?.setSelectDayTime(mSelectedTime)
            mCalendarPopup?.showAsDropDown(rl_toolbar, 0, resources.getDimension(R.dimen.space_10).toInt())
        }
    }

    fun updateDailyReport(dailyReport: DailyReport) {
        day_sleep_histogram_view.setData(dailyReport)
        report_sleep_duration_view.setSleepTodayDuration(dailyReport.sleep_duration)
        report_sleep_duration_view.setLightSleepData(dailyReport.light_duration, dailyReport.light_duration_percent)
        report_sleep_duration_view.setDeepSleepData(dailyReport.deep_duration, dailyReport.deep_duration_percent)
    }

    private fun setTvDate(timeInMillis: Long) {
        tv_date.text = TimeUtil.formatDate("yyyy.MM.dd", timeInMillis)
    }

    private fun queryDataForCalendar(monthTime: Long, monthCount: Int, isInit: Boolean) {
        val map = mutableMapOf<String, Any>()
        map["date"] = (monthTime / 1000).toInt()
        map["page_size"] = monthCount
        map["is_include"] = if (isInit) 1 else 0
        val call = AppManager.getHttpService().getCalendarSleepReport(mPatientId, map)
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
                mCalendarPopup?.addMonthAndData(monthTime, hasDataDays, isInit)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse.message)
            }
        })
    }

    override fun onDateClick(time: Long) {
        changeSelectTime(time)
    }

    private fun changeSelectTime(time: Long) {
        mSelectedTime = time
        setTvDate(time)
        queryPatientSleepData(time)
    }
}