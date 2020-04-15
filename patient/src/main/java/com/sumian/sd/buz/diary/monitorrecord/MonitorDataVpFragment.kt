package com.sumian.sd.buz.diary.monitorrecord

import android.os.Build
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.LogUtils
import com.google.gson.JsonObject
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.TimeUtilV2
import com.sumian.common.widget.refresh.SumianSwipeRefreshLayout
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.helper.SyncSleepDataHelper
import com.sumian.device.util.LogManager
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.devicemanager.uploadsleepdata.UploadSleepDataFinishedEvent
import com.sumian.sd.buz.diary.event.UpdateMonitorDataEvent
import com.sumian.sd.buz.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.buz.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.buz.diary.sleeprecord.widget.SleepDataDateBar
import com.sumian.sd.buz.huaweihealth.BindHuaweiHealthActivity
import com.sumian.sd.buz.report.weeklyreport.CalendarItemSleepReport
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.fragment_monitor_data_vp.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/16 18:07
 * desc   :
 * version: 1.0
 */
class MonitorDataVpFragment : BaseFragment() {
    private val mAdapter by lazy { InnerPagerAdapter(fragmentManager!!) }
    private val mHandler: Handler = Handler()

    companion object {
        private const val PRELOAD_THRESHOLD = 6
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_monitor_data_vp
    }

    override fun initWidget() {
        super.initWidget()
        initDateBar()
        initViewPager()
        SdLogManager.logHuaweiHealth("Build.MANUFACTURER.toLowerCase(): ${Build.MANUFACTURER.toLowerCase()}")
        iv_bind_huawei_health.isVisible = Build.MANUFACTURER.toLowerCase().contains("huawei")
        iv_bind_huawei_health.setOnClickListener {
            if (this.activity != null) {
                BindHuaweiHealthActivity.start(this.activity!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DeviceManager.registerDeviceStatusListener(mDeviceStatusListener)
        tv_is_syncing_hint.isVisible = SyncSleepDataHelper.isSleepDataTypeSyncing()
    }

    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(type: String, data: Any?) {
            when (type) {
                DeviceManager.EVENT_ALL_SLEEP_DATA_UPLOADED -> {
                    EventBusUtil.postStickyEvent(UploadSleepDataFinishedEvent())
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_PREPARE -> {
                    tv_is_syncing_hint?.isVisible = true
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_FAIL -> {
                    tv_is_syncing_hint?.isVisible = false
                    tv_sync_fail_hint?.isVisible = false
                }
                DeviceManager.EVENT_SYNC_SLEEP_DATA_AND_UPLOAD_FINISH -> {
                    tv_is_syncing_hint?.isVisible = false
                    if (!(data as Boolean)) {
                        LogManager.uploadSleepDataLog("上传数据异常")
                        tv_sync_fail_hint?.isVisible = true
                        tv_sync_fail_hint?.postDelayed({ tv_sync_fail_hint.isVisible = false }, 3000)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(null)
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            updateCurrentTimeData()
        }
    }

    private fun initViewPager() {
        view_pager_monitor.adapter = mAdapter.apply { mRefresh = mRoot as SumianSwipeRefreshLayout }
        mAdapter.addDays(TimeUtilV2.getDayStartTime(getInitTime()), PRELOAD_THRESHOLD * 2, true)
        view_pager_monitor.setCurrentItem(mAdapter.count - 1, false)
        view_pager_monitor.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val currentItem = view_pager_monitor.currentItem
                date_bar.setCurrentTime(mAdapter.times[currentItem])
                if (state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE && currentItem < PRELOAD_THRESHOLD) {
                    mAdapter.addDays(mAdapter.times[0], PRELOAD_THRESHOLD, false)
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    /**
     * 监测数据是每天20：00开放 第二天的日期
     */
    private fun getInitTime(): Long {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 20) {
            TimeUtilV2.rollDay(calendar, 1)
        }
        return calendar.timeInMillis
    }

    private fun initDateBar() {
        val calendar = Calendar.getInstance()
        val previewDays = if (calendar.get(Calendar.HOUR_OF_DAY) >= 20) 1 else 0
        date_bar.setPreviewDays(previewDays)
        date_bar.setCurrentTime(getInitTime())
        date_bar.setOnSleepDataDateBarClickListener(object : SleepDataDateBar.OnSleepDataDateBarClickListener {
            override fun onClick(show: Boolean) {
                if (show && activity != null) {
                    var scrollView = nsv_fragment_monitor_data_vp as NestedScrollView
                    scrollView.scrollTo(scrollView.scrollX,
                            activity!!.resources.getDimension(R.dimen.device_card_view_height).toInt())
                }
            }
        })
        date_bar.setDataLoader(object : CalendarPopup.DataLoader {
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
                        date_bar.addMonthAndData(startMonthTime, hasDataDays, isInit)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        LogUtils.d(errorResponse.message)
                    }
                })
            }
        })
        date_bar.setOnDateClickListener(CalendarView.OnDateClickListener { time -> scrollToTime(time) })
        date_bar.setWeekIconClickListener(View.OnClickListener {
            StatUtil.event(StatConstants.click_monitor_data_page_weekly_report_icon)
            WeeklyReportActivity.launch(date_bar.getCurrentTime())
        })
    }

    private fun scrollToTime(time: Long) {
        val firstTime = mAdapter.times[0]
        if (time < firstTime) {
            val count = (firstTime - time) / DateUtils.DAY_IN_MILLIS + PRELOAD_THRESHOLD
            mAdapter.addDays(firstTime, count.toInt(), false)
        }
        view_pager_monitor.setCurrentItem(mAdapter.times.indexOf(time), false)
    }

    fun updateCurrentTimeData() {
        EventBusUtil.postEvent(UpdateMonitorDataEvent())
    }

    class InnerPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        val times = ArrayList<Long>()

        var mRefresh: SumianSwipeRefreshLayout? = null

        override fun getItem(position: Int): Fragment {
            return MonitorDataFragment.newInstance(times[position])
        }

        override fun getItemId(position: Int): Long {
            return times[position]
        }

        override fun getCount(): Int {
            return times.size
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            if (`object` is MonitorDataFragment) {
                `object`.setRefresh(mRefresh)
            }
            super.setPrimaryItem(container, position, `object`)
        }

        override fun getItemPosition(obj: Any): Int {
            return if (obj is MonitorDataFragment) {
                val positionByTime = times.indexOf(obj.getSelectedTime())
                positionByTime
            } else {
                super.getItemPosition(obj)
            }
        }

        private fun createDays(time: Long, dayCount: Int = PRELOAD_THRESHOLD, include: Boolean): ArrayList<Long> {
            return TimeUtilV2.createDays(time, dayCount, false, include)
        }

        fun addDays(time: Long, dayCount: Int = PRELOAD_THRESHOLD, include: Boolean) {
            times.addAll(0, createDays(time, dayCount, include))
            notifyDataSetChanged()
        }
    }
}