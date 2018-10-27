package com.sumian.hw.report.adapter

import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.sumian.hw.report.bean.DailyReport
import com.sumian.hw.report.widget.ReportSleepDurationView
import com.sumian.hw.report.widget.SwitchDateView
import com.sumian.hw.report.widget.histogram.TouchDailySleepHistogramView
import com.sumian.hw.widget.refresh.BlueRefreshView
import com.sumian.sd.R
import java.util.*

/**
 * Created by jzz
 * on 2018/3/12.
 * desc:
 */

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    private val mData: ArrayList<DailyReport>  by lazy {
        ArrayList<DailyReport>(0)
    }

    private lateinit var mOnSwitchDateListener: SwitchDateView.OnSwitchDateListener
    private var mOnRefreshCallback: OnRefreshCallback? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hw_lay_item_today_repot, parent, false)
        val viewHolder = ViewHolder(itemView)
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyReport = mData[position]
        holder.initView(dailyReport, mOnSwitchDateListener, mOnRefreshCallback)
    }

    fun setOnSwitchDateListener(onSwitchDateListener: SwitchDateView.OnSwitchDateListener): DailyAdapter {
        mOnSwitchDateListener = onSwitchDateListener
        return this
    }

    fun setOnRefreshCallback(onRefreshCallback: OnRefreshCallback): DailyAdapter {
        mOnRefreshCallback = onRefreshCallback
        return this
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun addItem(dailyReport: DailyReport) {
        val size = mData.size
        mData.add(dailyReport)
        notifyItemInserted(size)
    }

    fun initAddAll(dailyReports: List<DailyReport>) {
        updateItem(0, dailyReports[dailyReports.size - 1])
        insertDataToHead(dailyReports.subList(0, dailyReports.size - 1))
    }

    fun addAll(dailyReports: List<DailyReport>) {
        val size = mData.size
        mData.addAll(dailyReports)
        notifyItemRangeInserted(size, dailyReports.size)
    }

    fun insertDataToHead(dailyReports: List<DailyReport>) {
        mData.addAll(0, dailyReports)
        notifyItemRangeInserted(0, dailyReports.size)
    }

    fun updateItem(dailyReport: DailyReport): Int {
        val position = getPosition(dailyReport.date)
        updateItem(position, dailyReport)
        return position
    }

    fun updateItem(position: Int, dailyReport: DailyReport) {
        if (position == -1) {
            return
        }
        mData[position] = dailyReport
        notifyItemChanged(position)
    }

    fun getPosition(unixTime: Int): Int {
        var i = 0
        val length = mData.size
        while (i < length) {
            val dailyReport = mData[i]
            if (dailyReport.date == unixTime) {
                return i
            }
            i++
        }
        return -1
    }

    fun getItem(position: Int): DailyReport {
        return mData[position]
    }

    fun getItemDate(position: Int): Long {
        return mData[position].date.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SwipeRefreshLayout.OnRefreshListener {

        private val mSwitchDateView: SwitchDateView  by lazy {
            itemView.findViewById<SwitchDateView>(R.id.switch_date_view)
        }
        private val mBlueRefreshView: BlueRefreshView by lazy {
            itemView.findViewById<BlueRefreshView>(R.id.refresh)
        }
        private val mNestedScrollView: NestedScrollView  by lazy {
            itemView.findViewById<NestedScrollView>(R.id.nested_scroll_view)
        }
        private val mDaySleepHistogramView: TouchDailySleepHistogramView  by lazy {
            itemView.findViewById<TouchDailySleepHistogramView>(R.id.day_sleep_histogram_view)
        }
        private val mReportSleepDurationView: ReportSleepDurationView  by lazy {
            itemView.findViewById<ReportSleepDurationView>(R.id.report_sleep_duration_view)
        }

        private lateinit var mDailyReport: DailyReport

        private var mOnRefreshCallback: OnRefreshCallback? = null

        fun initView(dailyReport: DailyReport, switchDateListener: SwitchDateView.OnSwitchDateListener, onRefreshCallback: OnRefreshCallback?) {
            this.mDailyReport = dailyReport

            this.mBlueRefreshView.isRefreshing = false
            this.mBlueRefreshView.setOnRefreshListener(this)

            this.mOnRefreshCallback = onRefreshCallback

            this.mSwitchDateView.unixTime = dailyReport.date.toLong()
            this.mSwitchDateView.setOnSwitchDateListener(switchDateListener)

            this.mDaySleepHistogramView.setData(dailyReport)
            this.mReportSleepDurationView.setSleepTodayDuration(dailyReport.sleep_duration)
            this.mReportSleepDurationView.setLightSleepData(dailyReport.light_duration, dailyReport.light_duration_percent)
            this.mReportSleepDurationView.setDeepSleepData(dailyReport.deep_duration, dailyReport.deep_duration_percent)

            if (this.mDailyReport.needScrollToBottom) {
                this.mNestedScrollView.post { mNestedScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                this.mDailyReport.needScrollToBottom = false
            }
        }

        override fun onRefresh() {
            mOnRefreshCallback?.onRefresh(adapterPosition, mDailyReport)
        }

        fun setRefreshing(refreshing: Boolean) {
            mBlueRefreshView.isRefreshing = refreshing
        }
    }

    interface OnRefreshCallback {

        fun onRefresh(position: Int, dailyReport: DailyReport)
    }
}
