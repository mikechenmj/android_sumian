package com.sumian.sd.diary.monitorrecord

import android.support.v4.widget.SwipeRefreshLayout
import android.view.ViewGroup
import android.widget.PopupWindow
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.report.base.BaseResultResponse
import com.sumian.hw.report.bean.DailyMeta
import com.sumian.hw.report.bean.DailyReport
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.diary.sleeprecord.calendar.custom.SleepCalendarViewWrapper
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_daily_report_v2.*
import kotlinx.android.synthetic.main.layout_diary_date_bar.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/11 19:57
 * desc   :
 * version: 1.0
 */
class DailyReportFragmentV2 : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, CalendarView.OnDateClickListener {

    companion object {
        private const val PAGE_SIZE = 3
    }

    private var mPopupWindow: PopupWindow? = null
    private val mCalendarViewWrapper: SleepCalendarViewWrapper by lazy {
        SleepCalendarViewWrapper(context)
    }
    private var mSelectedTime = System.currentTimeMillis()
    private val mDailyReportMap = HashMap<Long, DailyReport>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_daily_report_v2
    }

    override fun initWidget() {
        super.initWidget()
        iv_date_arrow.setOnClickListener { showDatePopup(true) }
        setTvDate(mSelectedTime)
    }

    override fun initData() {
        super.initData()
        queryDiary(mSelectedTime)
    }

    override fun onRefresh() {
        queryDiary(mSelectedTime)
    }

    private fun queryDiary(time: Long) {
        showLoading()
        val call = AppManager.getHwHttpService().getSleepReport((time / 1000).toInt(), 1, 1)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<BaseResultResponse<DailyReport, DailyMeta>>() {
            override fun onSuccess(response: BaseResultResponse<DailyReport, DailyMeta>?) {
                val data = response?.data
                if (data != null && data.size > 0) {
                    updateDailyReport(data.get(0))
                }
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

    private fun showDatePopup(show: Boolean) {
        val currentTimeMillis = System.currentTimeMillis()
        iv_date_arrow.isActivated = show
        if (show) {
            if (mPopupWindow == null) {
                mPopupWindow = PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mCalendarViewWrapper.setOnDateClickListener(this)
                mCalendarViewWrapper.setTodayTime(currentTimeMillis)
                mCalendarViewWrapper.setOnBgClickListener { v -> mPopupWindow?.dismiss() }
                mCalendarViewWrapper.setLoadMoreListener { time -> queryDataForCalendar(time, false) }
                mPopupWindow?.contentView = mCalendarViewWrapper
                mPopupWindow?.isOutsideTouchable = true
                mPopupWindow?.setBackgroundDrawable(null)
                mPopupWindow?.animationStyle = 0
                mPopupWindow?.isFocusable = true
                mPopupWindow?.setOnDismissListener {
                    iv_date_arrow.isActivated = false
                }
            }
            mPopupWindow?.showAsDropDown(rl_toolbar, 0, resources.getDimension(R.dimen.space_10).toInt())
            queryDataForCalendar(System.currentTimeMillis(), false)
        } else {
            mPopupWindow?.dismiss()
            mPopupWindow = null
        }
    }

    fun updateDailyReport(dailyReport: DailyReport) {
        refresh.isRefreshing = false
        refresh.setOnRefreshListener(this)
        day_sleep_histogram_view.setData(dailyReport)
        report_sleep_duration_view.setSleepTodayDuration(dailyReport.sleep_duration)
        report_sleep_duration_view.setLightSleepData(dailyReport.light_duration, dailyReport.light_duration_percent)
        report_sleep_duration_view.setDeepSleepData(dailyReport.deep_duration, dailyReport.deep_duration_percent)
    }

    private fun setTvDate(timeInMillis: Long) {
        tv_date.text = TimeUtil.formatDate("yyyy.MM.dd", timeInMillis)
    }

    private fun queryDataForCalendar(time: Long, isInit: Boolean) {
        val monthTimes = TimeUtil.createMonthTimes(time, PAGE_SIZE, isInit)
        if (isInit) {
            mCalendarViewWrapper.monthTimes = monthTimes
            mCalendarViewWrapper.setSelectDayTime(mSelectedTime)
        } else {
            mCalendarViewWrapper.addMonthTimes(monthTimes)
        }
        val call = AppManager.getHwHttpService().getSleepReport((time / 1000).toInt(), PAGE_SIZE * 31, if (isInit) 1 else 0)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<BaseResultResponse<DailyReport, DailyMeta>>() {
            override fun onSuccess(response: BaseResultResponse<DailyReport, DailyMeta>?) {
                val data = response?.data ?: return
                val hasDataDays = HashSet<Long>()
                for (dailyReport in data) {
                    mDailyReportMap[dailyReport.dateInMillis] = dailyReport
                    if (dailyReport.hasReport()) {
                        hasDataDays.add(dailyReport.dateInMillis)
                    }
                }
                mCalendarViewWrapper.addHasDataDays(hasDataDays)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    override fun onDateClick(time: Long) {
        if (time > TimeUtil.getStartTimeOfTheDay(System.currentTimeMillis())) {
            return
        }
        mPopupWindow?.dismiss()
        changeSelectTime(time)
    }

    private fun changeSelectTime(time: Long) {
        mSelectedTime = time
        setTvDate(time)
        val dailyReport = DailyReport()
        dailyReport.date = (time / 1000).toInt()
        updateDailyReport(mDailyReportMap[time] ?: dailyReport)
    }
}