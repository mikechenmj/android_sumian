package com.sumian.sd.buz.diary.sleeprecord

import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepRecordSummary
import com.sumian.sd.buz.diary.sleeprecord.calendar.calendarView.CalendarView
import com.sumian.sd.buz.diary.sleeprecord.calendar.custom.CalendarPopup
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.fragment_sleep_diary_vp.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/16 10:10
 * desc   :
 * version: 1.0
 */
class SleepDiaryVpFragment : BaseFragment() {
    private val mAdapter by lazy { InnerDiaryPagerAdapter(fragmentManager!!) }

    companion object {
        private const val PRELOAD_THRESHOLD = 6
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sleep_diary_vp
    }

    override fun initWidget() {
        super.initWidget()
        initDateBar()
        view_pager.adapter = mAdapter
        mAdapter.addDays(TimeUtilV2.getDayStartTime(System.currentTimeMillis()), PRELOAD_THRESHOLD * 2, true)
        view_pager.setCurrentItem(mAdapter.count - 1, false)
        view_pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val currentItem = view_pager.currentItem
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
        tv_sleep_restriction_hint.setOnClickListener { SleepRestrictionIntroductionDialogActivity.start() }
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
        date_bar.setWeekIconClickListener(View.OnClickListener {
            val selectTimeInSecond = (date_bar.getCurrentTime() / 1000).toInt()
            val urlContentPart = H5Uri.SLEEP_RECORD_WEEKLY_REPORT.replace("{date}", selectTimeInSecond.toString())
            SimpleWebActivity.launch(activity!!, urlContentPart)
        })
    }

    private fun scrollToTime(time: Long) {
        val firstTime = mAdapter.times[0]
        if (time < firstTime) {
            val count = (firstTime - time) / DateUtils.DAY_IN_MILLIS + PRELOAD_THRESHOLD
            mAdapter.addDays(firstTime, count.toInt(), false)
        }
        view_pager.setCurrentItem(mAdapter.times.indexOf(time), false)
    }

    class InnerDiaryPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        val times = ArrayList<Long>()

        override fun getItem(position: Int): Fragment {
            return SleepDiaryFragment.newInstance(times[position])
        }

        override fun getItemId(position: Int): Long {
            return times[position]
        }

        override fun getCount(): Int {
            return times.size
        }

        override fun getItemPosition(obj: Any): Int {
            return if (obj is SleepDiaryFragment) {
                val positionByTime = times.indexOf(obj.selectedTime)
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