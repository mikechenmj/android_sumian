package com.sumian.sd.diary.monitorrecord

import android.arch.lifecycle.Observer
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.report.base.BaseResultResponse
import com.sumian.hw.report.bean.DailyMeta
import com.sumian.hw.report.bean.DailyReport
import com.sumian.hw.report.calendar.CalendarItemSleepReport
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.UploadSleepDataFinishedEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.synthetic.main.fragment_daily_report_v2.*
import kotlinx.android.synthetic.main.layout_diary_date_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/11 19:57
 * desc   :
 * version: 1.0
 */
class DailyReportFragmentV2 : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, CalendarView.OnDateClickListener {

    private var mSelectedTime = System.currentTimeMillis()
    private var mCalendarPopup: CalendarPopup? = null
    private val mHandler: Handler = Handler()

    override fun getLayoutId(): Int {
        return R.layout.fragment_daily_report_v2
    }

    override fun initWidget() {
        super.initWidget()
        tv_date.setOnClickListener { showDatePopup(true) }
        iv_date_arrow.setOnClickListener { showDatePopup(true) }
        iv_weekly_report.setOnClickListener { WeeklyReportActivity.launch(mSelectedTime) }
        setTvDate(mSelectedTime)
        DeviceManager.getMonitorLiveData().observe(this, Observer {
            updateSyncingTv(it?.isSyncing == true, DeviceManager.getIsUploadingSleepDataToServerLiveData().value == true)
        })
        DeviceManager.getIsUploadingSleepDataToServerLiveData().observe(this, Observer {
            LogUtils.d("getIsUploadingSleepDataToServerLiveData", DeviceManager.getIsUploadingSleepDataToServerLiveData().value)
            updateSyncingTv(DeviceManager.getMonitorLiveData().value?.isSyncing == true, it == true)
        })
    }

    private fun updateSyncingTv(isSyncMonitorData: Boolean, isUpload: Boolean) {
        LogUtils.d(isSyncMonitorData, isUpload)
        tv_is_syncing_hint.visibility = if (isSyncMonitorData || isUpload) View.VISIBLE else View.GONE
    }

    override fun initData() {
        super.initData()
        queryDiary(mSelectedTime)
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
        mHandler.removeCallbacks(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUploadSleepDataFailedEvent(event: UploadSleepDataFinishedEvent) {
        LogUtils.d(event)
        mHandler.removeCallbacks(mDismissBottomHintRunnable)
        if (event.success) {
            queryDiary(mSelectedTime) // refresh
        } else {
            tv_sync_fail_hint.visibility = View.VISIBLE
            mHandler.postDelayed(mDismissBottomHintRunnable, 3000)
        }
    }

    private val mDismissBottomHintRunnable = {
        tv_sync_fail_hint.visibility = View.GONE
    }

    override fun onRefresh() {
        queryDiary(mSelectedTime)
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }


    private fun queryDiary(time: Long) {
        showLoading()
        val call = AppManager.getHwHttpService().getSleepReport((time / 1000).toInt(), 1, 1)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<BaseResultResponse<DailyReport, DailyMeta>>() {
            override fun onSuccess(response: BaseResultResponse<DailyReport, DailyMeta>?) {
                val data = response?.data
                if (data != null && data.size > 0) updateDailyReport(data[0])
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                refresh.isRefreshing = false
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

    private fun queryDataForCalendar(monthTime: Long, monthCount: Int, isInit: Boolean) {
        val map = HashMap<String, Any>(0)
        map["date"] = (monthTime / 1000).toInt()
        map["page_size"] = monthCount
        map["is_include"] = if (isInit) 1 else 0
        val call = AppManager.getHwHttpService().getCalendarSleepReport(map)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<JsonObject>() {
            override fun onSuccess(response: JsonObject?) {
                val hasDataDays = HashSet<Long>()
                val jsonObject = JSON.parseObject(response.toString())
                val entries = jsonObject.entries
                for ((key, value) in entries) {
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
        queryDiary(time)
    }
}