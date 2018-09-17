package com.sumian.hw.report.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager
import com.sumian.hw.base.HwBaseFragment
import com.sumian.hw.log.LogManager
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.report.adapter.DailyAdapter
import com.sumian.hw.report.bean.DailyReport
import com.sumian.hw.report.bean.SleepPackage
import com.sumian.hw.report.calendar.CalendarDialog
import com.sumian.hw.report.contract.DailyReportContract
import com.sumian.hw.report.presenter.DailyReportPresenter
import com.sumian.hw.report.viewModel.ReportModel
import com.sumian.hw.report.widget.SwitchDateView
import com.sumian.hw.widget.refresh.ActionLoadingDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.main.OnEnterListener
import kotlinx.android.synthetic.main.hw_fragment_daily_report.*
import java.util.*

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

class DailyReportFragment : HwBaseFragment<DailyReportPresenter>(), DailyReportContract.View, SwitchDateView.OnSwitchDateListener, RecyclerViewPager.OnPageChangedListener, DailyAdapter.OnRefreshCallback, ReportModel.OnSyncCallback, OnEnterListener {

    companion object {

        private const val EXTRA_SCROLL = "com.sumian.app.extra.SCROLL"

        private const val PRELOAD_THRESHOLD = 5

        @JvmStatic
        fun newInstance(): DailyReportFragment {
            return DailyReportFragment()
        }

    }

    private val mDailyAdapter: DailyAdapter  by lazy {
        DailyAdapter().setOnSwitchDateListener(this).setOnRefreshCallback(this)
    }

    private var mCurrentDailyReport: DailyReport? = null
    private var mNeedScrollToBottom: Boolean = false

    private val mActionLoadingDialog: ActionLoadingDialog  by lazy {
        ActionLoadingDialog()
    }
    private var mCurrentPosition: Int = 0

    private var mIsSyncing: Boolean = false

    private val mBroadcastReceiver: BroadcastReceiver by lazy {
        return@lazy object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    CalendarDialog.ACTION_SELECT_DATE -> {
                        val selectUnixTime = intent.getLongExtra(CalendarDialog.EXTRA_DATE, 0)
                        mNeedScrollToBottom = intent.getBooleanExtra(EXTRA_SCROLL, false)
                        onScrollToTime(selectUnixTime)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    val currentReportTime: Long
        get() = if (mCurrentDailyReport == null) {
            0
        } else mCurrentDailyReport!!.dateInMillis

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_daily_report
    }

    override fun initWidget(root: View) {
        super.initWidget(root)

        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = mDailyAdapter
        recycler.addOnPageChangedListener(this)
    }

    override fun initPresenter() {
        super.initPresenter()
        DailyReportPresenter.init(this)
    }

    override fun initData() {
        super.initData()
        initReceiver()
        initAdapter()
        val showDailyPushReport = showPushReportIfNeeded()
        if (!showDailyPushReport) {
            mPresenter.getInitReports(getInitToday())
        }
    }

    override fun setPresenter(presenter: DailyReportContract.Presenter) {
        this.mPresenter = presenter as DailyReportPresenter
    }

    override fun onResume() {
        super.onResume()
        showPushReportIfNeeded()
    }

    override fun onRelease() {
        super.onRelease()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
    }

    override fun setReportsData(dailyReports: List<DailyReport>?) {
        if (dailyReports == null || dailyReports.isEmpty()) {
            return
        }
        mDailyAdapter.initAddAll(dailyReports)
    }

    override fun showReportAtTime(unixTime: Long) {
        onScrollToTime(unixTime)
    }

    override fun updateReportData(dailyReport: DailyReport) {
        dailyReport.needScrollToBottom = mNeedScrollToBottom
        mNeedScrollToBottom = false
        val updateItemPosition = mDailyAdapter.updateItem(dailyReport)
        if (updateItemPosition == mCurrentPosition) {
            mCurrentDailyReport = dailyReport
        }
    }

    override fun insertReportDataAtHead(dailyReports: List<DailyReport>) {
        mDailyAdapter.insertDataToHead(dailyReports)
    }

    override fun onRefresh(position: Int, dailyReport: DailyReport) {
        mPresenter.refreshReport(dailyReport.date.toLong())
    }

    override fun onScrollToTime(unixTime: Long) {
        val position = mDailyAdapter.getPosition(unixTime.toInt())
        if (position == -1) {
            val date = mDailyAdapter.getItem(0).date
            val intervalUnixTime = (24 * 60 * 60).toLong()//86400 一天的间隔时间戳
            val distance = date - unixTime
            val pageCount = (distance / intervalUnixTime).toInt()
            mPresenter.getReportsAndGotoTargetTime(date.toLong(), pageCount, false, unixTime)
        } else {
            mCurrentDailyReport = mDailyAdapter.getItem(position)
            mCurrentDailyReport!!.needScrollToBottom = mNeedScrollToBottom
            mNeedScrollToBottom = false
            recycler.scrollToPosition(position)
        }
    }

    override fun OnPageChanged(positionBeforeScroll: Int, position: Int) {
        mCurrentPosition = position
        mCurrentDailyReport = mDailyAdapter.getItem(position)
        if ((mCurrentDailyReport == null || mCurrentDailyReport!!.sleep_duration <= 0) && mIsSyncing) {
            //syncing_report_view.showSyncing()
        } else {
            // syncing_report_view.hide()
        }
        if (position < PRELOAD_THRESHOLD) {
            preloadData()
        }
    }

    override fun onBegin() {
        mActionLoadingDialog.show(activity!!.supportFragmentManager)
    }

    override fun onFinish() {
        mActionLoadingDialog.dismiss()
        stopRefreshing()
    }

    override fun onFailure(error: String) {
        showToast(error)
    }

    private fun stopRefreshing() {
        val viewHolder = getViewHolder(mCurrentPosition)
        viewHolder.setRefreshing(false)
    }

    override fun onEnter(data: String?) {
        // check monitor sync sleep data status
        AppManager.getReportModel().setOnSyncCallback(this).checkSyncStatus()
        showPushReportIfNeeded()
        LogManager.appendUserOperationLog("点击 '日报告' 界面")
        AppManager.getJobScheduler().checkJobScheduler()
    }

    override fun onSyncingCallback() {
        this.mIsSyncing = true
        val dailyReport = mDailyAdapter.getItem(mCurrentPosition)
        if (dailyReport.sleep_duration > 0) {
            return
        }
        // syncing_report_view.showSyncing()
    }

    override fun onSyncingErrorCallback() {
        this.mIsSyncing = false
        //syncing_report_view.showSyncError()
    }

    override fun onSyncFinishedCallback() {
        this.mIsSyncing = false
        //syncing_report_view.hide()
    }

    /**
     * 检查是否有推送消息，如果有，则显示对应数据，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    private fun showPushReportIfNeeded(): Boolean {
        return ReportPushManager.getInstance().checkDailyPushReportAndRun(context) { pushReport ->
            mNeedScrollToBottom = true
            val pushDate = pushReport.pushDate
            if (mCurrentDailyReport != null && mCurrentDailyReport!!.date == pushDate) {
                mPresenter.refreshReport(mCurrentDailyReport!!.date.toLong())
            } else {
                onScrollToTime(pushDate.toLong())
            }
        }
    }

    private fun initReceiver() {
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(mBroadcastReceiver, IntentFilter().apply {
            addAction(CalendarDialog.ACTION_SELECT_DATE)
        })
    }

    private fun initAdapter() {
        val dailyReport: DailyReport
        if (AppManager.getReportModel().isHaveTodayCache) {
            dailyReport = AppManager.getReportModel().cacheDailyReport
        } else {
            dailyReport = DailyReport()
            dailyReport.date = getInitToday().toInt()
            dailyReport.bedtime_state = ArrayList(0)
            dailyReport.packages = ArrayList<SleepPackage>(0)
        }
        mDailyAdapter.addItem(dailyReport)
    }

    private fun getViewHolder(position: Int): DailyAdapter.ViewHolder {
        return recycler.findViewHolderForAdapterPosition(position) as DailyAdapter.ViewHolder
    }

    private fun preloadData() {
        mPresenter.getPreloadReports(mDailyAdapter.getItemDate(0))
    }

    private fun getInitToday(): Long {
        val instance = Calendar.getInstance()
        val year = instance.get(Calendar.YEAR)
        val month = instance.get(Calendar.MONTH)
        val date = instance.get(Calendar.DATE)
        instance.set(year, month, date, 0, 0, 0)
        return instance.timeInMillis / 1000L
    }

}
