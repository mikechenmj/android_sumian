package com.sumian.sd.diary.monitorrecord

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.text.format.DateUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.JsonObject
import com.sumian.common.base.BaseBackActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.hw.report.weeklyreport.CalendarItemSleepReport
import com.sumian.hw.report.weeklyreport.WeeklyReportFragmentV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.diary.sleeprecord.calendar.custom.SleepCalendarViewWrapper.PRELOAD_THRESHOLD
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_weekly_report.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 16:37
 * desc   :
 * version: 1.0
 */
class WeeklyReportActivity : BaseBackActivity() {
    private val mAdapter by lazy { InnerPagerAdapter(supportFragmentManager!!) }

    companion object {
        private const val KEY_SCROLL_TIME = "scroll_time"

        fun launch(time: Long) {
            val intent = Intent(ActivityUtils.getTopActivity(), WeeklyReportActivity::class.java)
            intent.putExtra(KEY_SCROLL_TIME, time)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getChildContentId(): Int {
        return R.layout.activity_weekly_report
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.weekly_data)
        initDateBar()
        initViewPager()
    }

    private fun initViewPager() {
        view_pager.adapter = mAdapter
        mAdapter.addDays(TimeUtilV2.getDayStartTime(System.currentTimeMillis()), PRELOAD_THRESHOLD * 2, true)
        view_pager.setCurrentItem(mAdapter.count - 1, false)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val currentItem = view_pager.currentItem
                date_bar.setCurrentTime(mAdapter.times[currentItem])
                if (state == ViewPager.SCROLL_STATE_IDLE && currentItem < PRELOAD_THRESHOLD) {
                    mAdapter.addDays(mAdapter.times[0], PRELOAD_THRESHOLD, false)
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    private fun initDateBar() {
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
    }

    private fun scrollToTime(time: Long) {
        val firstTime = mAdapter.times[0]
        if (time < firstTime) {
            val count = (firstTime - time) / DateUtils.WEEK_IN_MILLIS + PRELOAD_THRESHOLD
            mAdapter.addDays(firstTime, count.toInt(), false)
        }
        view_pager.setCurrentItem(mAdapter.times.indexOf(time), false)
    }

    class InnerPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        val times = ArrayList<Long>()

        override fun getItem(position: Int): Fragment {
            return WeeklyReportFragmentV2.newInstance(times[position])
        }

        override fun getItemId(position: Int): Long {
            return times[position]
        }

        override fun getCount(): Int {
            return times.size
        }

        override fun getItemPosition(obj: Any): Int {
            return if (obj is WeeklyReportFragmentV2) {
                val positionByTime = times.indexOf(obj.getSelectedTime())
                positionByTime
            } else {
                super.getItemPosition(obj)
            }
        }

        private fun createWeeks(time: Long, dayCount: Int = PRELOAD_THRESHOLD, include: Boolean): ArrayList<Long> {
            return TimeUtilV2.createWeeks(time, dayCount, false, include)
        }

        fun addDays(time: Long, dayCount: Int = PRELOAD_THRESHOLD, include: Boolean) {
            times.addAll(0, createWeeks(time, dayCount, include))
            notifyDataSetChanged()
        }
    }
}