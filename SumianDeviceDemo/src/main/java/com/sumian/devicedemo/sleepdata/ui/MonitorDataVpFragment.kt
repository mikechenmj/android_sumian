package com.sumian.devicedemo.sleepdata

import android.os.Handler
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.JsonUtil
import com.sumian.devicedemo.R
import com.sumian.devicedemo.base.BaseFragment
import com.sumian.devicedemo.sleepdata.data.CalendarItemSleepReport
import com.sumian.devicedemo.sleepdata.event.UpdateMonitorDataEvent
import com.sumian.devicedemo.sleepdata.event.UploadSleepDataFinishedEvent
import com.sumian.devicedemo.sleepdata.ui.WeeklyReportActivity
import com.sumian.devicedemo.sleepdata.util.TimeUtilV2
import com.sumian.devicedemo.sleepdata.widget.calendarView.CalendarView
import com.sumian.devicedemo.sleepdata.widget.custom.CalendarPopup
import com.sumian.devicedemo.util.EventBusUtil
import kotlinx.android.synthetic.main.fragment_monitor_data_vp.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
//        DeviceManager.getMonitorLiveData().observe(this, Observer {
//            tv_is_syncing_hint.visibility = if (it?.isSyncing == true) View.VISIBLE else View.GONE
//        })
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            updateCurrentTimeData()
        }
    }

    private fun initViewPager() {
        view_pager_monitor.adapter = mAdapter
        mAdapter.addDays(TimeUtilV2.getDayStartTime(getInitTime()), PRELOAD_THRESHOLD * 2, true)
        view_pager_monitor.setCurrentItem(mAdapter.count - 1, false)
        view_pager_monitor.addOnPageChangeListener(object :
                androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val currentItem = view_pager_monitor.currentItem
                date_bar.setCurrentTime(mAdapter.times[currentItem])
                if (state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE && currentItem < PRELOAD_THRESHOLD) {
                    mAdapter.addDays(mAdapter.times[0], PRELOAD_THRESHOLD, false)
                }
            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
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
        date_bar.setDataLoader(object : CalendarPopup.DataLoader {
            override fun loadData(startMonthTime: Long, monthCount: Int, isInit: Boolean) {
                val map = HashMap<String, Any>(0)
                map["date"] = (startMonthTime / 1000).toInt()
                map["page_size"] = monthCount
                map["is_include"] = if (isInit) 1 else 0
                val call = NetworkManager.getApi().getCalendarSleepReport(map)
                addCall(call)
                call.enqueue(object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    }

                    override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val jo = response.body()!!
                            /**
                             * {"1525104000":[{"id":163,"date":1525449600,"is_today":false},{"id":168,"date":1525536000,"is_today":false},{"id":172,"date":1525622400,"is_today":false},{"id":195,"date":1526227200,"is_today":false},{"id":199,"date":1526313600,"is_today":false}],"1522512000":[{"id":109,"date":1524067200,"is_today":false},{"id":125,"date":1523635200,"is_today":false}],"1519833600":[],"1517414400":[],"1514736000":[],"1512057600":[],"1509465600":[],"1506787200":[],"1504195200":[],"1501516800":[],"1498838400":[],"1496246400":[],"earliest_month":1522512000}
                             */
                            val hasDataDays = HashSet<Long>()
//                            val jsonObject = JsonParser().parse(response.toString())
                            val entries = jo.entrySet()
                            for ((_, value) in entries) {
                                if (value is JSONArray) {
                                    val calendarItemSleepReports =
                                            JsonUtil.fromJson<List<CalendarItemSleepReport>>(
                                                    value.toString(),
                                                    object :
                                                            TypeToken<List<CalendarItemSleepReport>>() {}.type
                                            )
                                    for (report in calendarItemSleepReports!!) {
                                        hasDataDays.add(report.dateInMillis)
                                    }
                                }
                            }
                            date_bar.addMonthAndData(startMonthTime, hasDataDays, isInit)
                        } else {
                            ToastUtils.showShort(response.message())
                        }
                    }
                })
            }
        })
        date_bar.setOnDateClickListener(CalendarView.OnDateClickListener { time -> scrollToTime(time) })
        date_bar.setWeekIconClickListener(View.OnClickListener {
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

    class InnerPagerAdapter(fragmentManager: FragmentManager) :
            FragmentPagerAdapter(fragmentManager) {
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

        private fun createDays(
                time: Long,
                dayCount: Int = PRELOAD_THRESHOLD,
                include: Boolean
        ): ArrayList<Long> {
            return TimeUtilV2.createDays(time, dayCount, false, include)
        }

        fun addDays(time: Long, dayCount: Int = PRELOAD_THRESHOLD, include: Boolean) {
            times.addAll(0, createDays(time, dayCount, include))
            notifyDataSetChanged()
        }
    }
}