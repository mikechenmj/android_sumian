package com.sumian.sd.diary.monitorrecord

import android.arch.lifecycle.Observer
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.text.format.DateUtils
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.diary.sleeprecord.bean.SleepRecordSummary
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.event.UploadSleepDataFinishedEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.fragment_monitor_data_vp.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
        DeviceManager.getMonitorLiveData().observe(this, Observer {
            tv_is_syncing_hint.visibility = if (it?.isSyncing == true) View.VISIBLE else View.GONE
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUploadSleepDataFinishedEvent(event: UploadSleepDataFinishedEvent) {
        LogUtils.d(event)
        mHandler.removeCallbacks(mDismissBottomHintRunnable)
        if (event.success) {
        } else {
            tv_sync_fail_hint.visibility = View.VISIBLE
            mHandler.postDelayed(mDismissBottomHintRunnable, 3000)
        }
    }

    private val mDismissBottomHintRunnable = {
        tv_sync_fail_hint.visibility = View.GONE
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(null)
        super.onDestroy()
    }

    private fun initViewPager() {
        view_pager_monitor.adapter = mAdapter
        mAdapter.addDays(TimeUtilV2.getDayStartTime(System.currentTimeMillis()), PRELOAD_THRESHOLD * 2, true)
        view_pager_monitor.setCurrentItem(mAdapter.count - 1, false)
        view_pager_monitor.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val currentItem = view_pager_monitor.currentItem
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
                val call = AppManager.getSdHttpService().getSleepDiarySummaryList((startMonthTime / 1000).toInt(), 1, monthCount, 0)
                addCall(call)
                call.enqueue(object : BaseSdResponseCallback<Map<String, List<SleepRecordSummary>>>() {
                    override fun onFailure(errorResponse: ErrorResponse) {}

                    override fun onSuccess(response: Map<String, List<SleepRecordSummary>>?) {
                        if (response == null) {
                            return
                        }
                        val hasDataDays = HashSet<Long>()
                        for ((_, value) in response) {
                            for (summary in value) {
                                val summaryDate = summary.dateInMillis
                                hasDataDays.add(summaryDate)
                            }
                        }
                        date_bar.addMonthAndData(startMonthTime, hasDataDays, isInit)
                    }
                })
            }
        })
        date_bar.setOnDateClickListener(CalendarView.OnDateClickListener { time -> scrollToTime(time) })
        date_bar.setWeekIconClickListener(View.OnClickListener { WeeklyReportActivity.launch(date_bar.getCurrentTime()) })
    }

    private fun scrollToTime(time: Long) {
        val firstTime = mAdapter.times[0]
        if (time < firstTime) {
            val count = (firstTime - time) / DateUtils.DAY_IN_MILLIS + PRELOAD_THRESHOLD
            mAdapter.addDays(firstTime, count.toInt(), false)
        }
        view_pager_monitor.setCurrentItem(mAdapter.times.indexOf(time), false)
    }

    class InnerPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        val times = ArrayList<Long>()

        override fun getItem(position: Int): Fragment {
            return MonitorDataFragment.newInstance(times[position])
        }

        override fun getItemId(position: Int): Long {
            return times[position]
        }

        override fun getCount(): Int {
            return times.size
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