package com.sumian.hw.report.weeklyreport

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils
import android.view.View
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager
import com.sumian.hw.base.HwBaseFragment
import com.sumian.hw.log.LogManager
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.main.OnEnterListener
import com.sumian.hw.report.weeklyreport.bean.SleepDurationReport
import kotlinx.android.synthetic.main.hw_fragment_week_report.*
import java.util.*

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:
 */

class WeeklyReportFragment : HwBaseFragment<WeeklyReportPresenter>(), WeeklyReportContact.View,
        RecyclerViewPager.OnPageChangedListener, WeeklyReportAdapter.OnWeekReportCallback, OnEnterListener {

    companion object {

        private const val PRELOAD_THRESHOLD = 5
        private const val KEY_SCROLL_TIME = "scroll_time"

        fun newInstance(): WeeklyReportFragment {
            return WeeklyReportFragment()
        }

        fun newInstance(time: Long): WeeklyReportFragment {
            val bundle = Bundle()
            bundle.putLong(KEY_SCROLL_TIME, time)
            val weeklyReportFragment = WeeklyReportFragment()
            weeklyReportFragment.arguments = bundle
            return weeklyReportFragment
        }
    }

    private var mCurrentReport: SleepDurationReport? = null

    private val mAdapter: WeeklyReportAdapter  by lazy {
        WeeklyReportAdapter().setWeekReportCallback(this)
    }

    //    private val mBroadcastReceiver: BroadcastReceiver  by lazy {
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent) {
//                when (intent.action) {
//                    CalendarDialog.ACTION_SELECT_DATE -> {
//                        val selectUnixTime = intent.getLongExtra(CalendarDialog.EXTRA_DATE, 0)
//                        scrollToTime(selectUnixTime * 1000L)
//                    }
//                }
//            }
//        }
//    }
    private var mNeedScrollToBottom: Boolean = false
    private var mCurrentPosition: Int = 0

    private var mNeedUpdateWhenLoad: Boolean = false

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_week_report
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        recycler.addOnPageChangedListener(this)
        recycler.adapter = mAdapter
        recycler.post {
            val time = arguments?.getLong(KEY_SCROLL_TIME, 0L) ?: 0L
            if (time != 0L) {
                scrollToTime(time)
            }
        }
    }

    override fun initData() {
        super.initData()
        initReceiver()
        initAdapter()
        val currentTimeMillis = System.currentTimeMillis()
        mPresenter.getInitReports(currentTimeMillis)
    }

    override fun initPresenter() {
        super.initPresenter()
        WeeklyReportPresenter.init(this)
    }

    override fun onEnter(data: String?) {
        LogManager.appendUserOperationLog("点击 '周报告' 界面")
        AppManager.getSleepDataUploadManager().checkPendingTaskAndRun()
    }

    override fun onRelease() {
        super.onRelease()
//        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
    }

    override fun setReportsData(reports: List<SleepDurationReport>?) {
        if (reports != null && !reports.isEmpty()) {
            mAdapter.addAllDataAtHead(reports)
        }
    }

    override fun updateReportData(reports: SleepDurationReport) {
        mCurrentReport = reports
        mCurrentReport!!.needScrollToBottom = mNeedScrollToBottom
        mNeedScrollToBottom = false
        mAdapter.updateItem(reports)
    }

    override fun insertReportDataAtHead(reports: List<SleepDurationReport>?) {
        if (reports == null || reports.isEmpty()) {
            showCenterToast("没有更多睡眠数据")
        } else {
            mAdapter.addAllDataAtHead(reports)
        }
    }

    override fun showReportAtTime(time: Long) {
        scrollToTime(time)
    }

    override fun setPresenter(presenter: WeeklyReportContact.Presenter) {
        this.mPresenter = presenter as WeeklyReportPresenter
    }

    override fun onFailure(error: String) {
        showToast(error)
    }

    override fun OnPageChanged(positionBeforeScroll: Int, position: Int) {
        mCurrentPosition = position
        this.mCurrentReport = mAdapter.getItem(position)
        if (mNeedUpdateWhenLoad) {
            mNeedUpdateWhenLoad = false
            mPresenter.refreshReport(mCurrentReport!!.start_date_show * 1000L)
            mNeedScrollToBottom = true
        }

        if (position < PRELOAD_THRESHOLD) {
            preloadData()
        }
    }

    override fun onSwitchWeek(v: View, position: Int, item: SleepDurationReport) {
        val i = v.id
        if (i == R.id.iv_pre) {
            if (position > 0) {
                recycler.scrollToPosition(position - 1)
            }
        } else if (i == R.id.iv_next) {
            recycler.scrollToPosition(position + 1)
        }
    }

    override fun onRefreshWeekReport(v: View, position: Int, item: SleepDurationReport) {
        mPresenter.refreshReport(item.end_date_show * 1000L)
    }

    override fun onShowSleepAdvice(v: View, position: Int, item: SleepDurationReport) {
        SleepAdviceDialog(activity!!).setAdvice(mAdapter.getItem(position).advice).show()
    }

    override fun onFinish() {
        stopRefreshing()
    }

    private fun stopRefreshing() {
        val viewHolder = getViewHolder(mCurrentPosition)
        viewHolder?.setRefreshing(false)
    }

    private fun initAdapter() {
        val sleepDurationReport = SleepDurationReport.createPlaceHoldData(System.currentTimeMillis())
        val list = ArrayList<SleepDurationReport>()
        list.add(sleepDurationReport)
        mAdapter.addAllDataAtHead(list)
    }

    private fun initReceiver() {
//        val filter = IntentFilter()
//        filter.addAction(CalendarDialog.ACTION_SELECT_DATE)
//        LocalBroadcastManager.getInstance(activity!!).registerReceiver(mBroadcastReceiver, filter)
    }

    private fun scrollToTime(time: Long) {
        val position = mAdapter.getPosition(time)
        if (position == -1) {
            val firstItemTime: Long = if (mAdapter.itemCount == 0) {
                System.currentTimeMillis()
            } else {
                mAdapter.getItem(0).start_date_show * 1000L
            }
            val pageCount = ((firstItemTime - time) / (DateUtils.DAY_IN_MILLIS * 7)).toInt()
            mPresenter.getReportsAndGotoTargetTime(firstItemTime, pageCount, false, time)
        } else {
            val report = mAdapter.getItem(position)
            report.needScrollToBottom = mNeedScrollToBottom
            mNeedScrollToBottom = false
            recycler.scrollToPosition(position)
        }
    }

    private fun getViewHolder(position: Int): WeeklyReportAdapter.ViewHolder? {
        return recycler.findViewHolderForAdapterPosition(position) as? WeeklyReportAdapter.ViewHolder
    }

    private fun preloadData() {
        mPresenter.getPreloadReports(mAdapter.getItem(0).start_date_show * 1000L)
    }
}
