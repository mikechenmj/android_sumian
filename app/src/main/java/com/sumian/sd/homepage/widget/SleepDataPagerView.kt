package com.sumian.sd.homepage.widget

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.sumian.hw.report.bean.DailyReport
import com.sumian.sd.R
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.diary.sleeprecord.widget.SimpleSleepRecordView
import kotlinx.android.synthetic.main.lay_sleep_data_pager.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/13 15:34
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepDataPagerView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    private val mAdapter by lazy {
        MyAdapter(context)
    }

    private val pagerMaxMargin by lazy {
        resources.getDimension(R.dimen.home_page_pager_max_margin)
    }
    private val pagerMinMargin by lazy {
        resources.getDimension(R.dimen.home_page_pager_min_margin)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.lay_sleep_data_pager, this, true)
        clipChildren = false
        view_pager.adapter = mAdapter
//        view_pager.pageMargin = resources.getDimension(R.dimen.space_10).toInt()
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    changePagerMargin(pagerMinMargin, pagerMaxMargin)
                } else {
                    changePagerMargin(pagerMaxMargin, pagerMinMargin)
                }
            }
        })
    }

    private fun changePagerMargin(marginStart: Float, marginEnd: Float) {
        val layoutParams = view_pager.layoutParams
        when (layoutParams) {
            is FrameLayout.LayoutParams -> {
                layoutParams.marginStart = marginStart.toInt()
                layoutParams.marginEnd = marginEnd.toInt()
            }
            is LinearLayout.LayoutParams -> {
                layoutParams.marginStart = marginStart.toInt()
                layoutParams.marginEnd = marginEnd.toInt()
            }
            is RelativeLayout.LayoutParams -> {
                layoutParams.marginStart = marginStart.toInt()
                layoutParams.marginEnd = marginEnd.toInt()
            }
        }
        view_pager.layoutParams = layoutParams
    }

    fun setSleepRecord(sleepRecord: SleepRecord?) {
        mAdapter.setSleepRecord(sleepRecord)
    }

    fun querySleepRecord() {
        mAdapter.mSleepRecordView.querySleepRecord()
    }

    fun queryDailyReport() {
        // mAdapter.mHardwareSleepDataView.queryDailyReport()
    }

    class MyAdapter(context: Context) : PagerAdapter() {
        val mSleepRecordView by lazy {
            val simpleSleepRecordView = SimpleSleepRecordView(context)
            simpleSleepRecordView
        }
//        val mHardwareSleepDataView by lazy {
//            val hardwareSleepDataView = HardwareSleepDataView(context)
//            hardwareSleepDataView
//        }

        fun setSleepRecord(sleepRecord: SleepRecord?) {
            mSleepRecordView.setSleepRecord(sleepRecord)
        }

        fun setDailyReport(dailyReport: DailyReport?) {
            // mHardwareSleepDataView.setDailyReport(dailyReport)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return 1
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = when (position) {
                0 -> mSleepRecordView
                // 1 -> mHardwareSleepDataView
                else -> throw RuntimeException("invalid position")
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeViewAt(position)
        }
    }
}