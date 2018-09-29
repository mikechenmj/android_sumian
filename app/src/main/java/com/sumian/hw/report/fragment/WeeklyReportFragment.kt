package com.sumian.hw.report.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils
import android.view.View
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager
import com.sumian.hw.log.LogManager
import com.sumian.sd.network.response.SleepDurationReport
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.report.adapter.WeeklyReportAdapter
import com.sumian.hw.report.calendar.CalendarDialog
import com.sumian.hw.report.contract.WeeklyReportContact
import com.sumian.hw.report.presenter.WeeklyReportPresenter
import com.sumian.hw.report.dialog.SleepAdviceDialog
import com.sumian.hw.widget.refresh.ActionLoadingDialog
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.theme.three.base.SkinBaseFragment
import kotlinx.android.synthetic.main.hw_fragment_week_report.*
import java.util.*

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:
 */

class WeeklyReportFragment : SkinBaseFragment<WeeklyReportPresenter>(), WeeklyReportContact.View, RecyclerViewPager.OnPageChangedListener, WeeklyReportAdapter.OnWeekReportCallback, OnEnterListener {

    companion object {

        private const val PRELOAD_THRESHOLD = 5

        fun newInstance(): WeeklyReportFragment {
            return WeeklyReportFragment()
        }
    }

    private var mCurrentReport: SleepDurationReport? = null

    private val mAdapter: WeeklyReportAdapter  by lazy {
        WeeklyReportAdapter().setWeekReportCallback(this)
    }

    private val mBroadcastReceiver: BroadcastReceiver  by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    CalendarDialog.ACTION_SELECT_DATE -> {
                        val selectUnixTime = intent.getLongExtra(CalendarDialog.EXTRA_DATE, 0)
                        scrollToTime(selectUnixTime * 1000L)
                    }
                }
            }
        }
    }
    private var mNeedScrollToBottom: Boolean = false
    private var mCurrentPosition: Int = 0

    private val mActionLoadingDialog: ActionLoadingDialog  by lazy {
        ActionLoadingDialog()
    }

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
    }

    override fun initData() {
        super.initData()
        initReceiver()
        val currentTimeMillis = System.currentTimeMillis()
        val showPushReport = showPushReportIfNeeded()
        if (!showPushReport) {
            mPresenter.getInitReports(currentTimeMillis)
        }
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        showPushReportIfNeeded()
    }

    override fun initPresenter() {
        super.initPresenter()
        WeeklyReportPresenter.init(this)
    }

    override fun onEnter(data: String?) {
        LogManager.appendUserOperationLog("点击 '周报告' 界面")
        showPushReportIfNeeded()
        AppManager.getJobScheduler().checkJobScheduler()
    }

    override fun onRelease() {
        super.onRelease()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
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

    override fun onBegin() {
        mActionLoadingDialog.show(activity!!.supportFragmentManager)
    }

    override fun onFinish() {
        dismissLoadingDialog()
        stopRefreshing()
    }

    private fun dismissLoadingDialog() {
        mActionLoadingDialog.dismiss()
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
        val filter = IntentFilter()
        filter.addAction(CalendarDialog.ACTION_SELECT_DATE)
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(mBroadcastReceiver, filter)
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

    /**
     * 检查是否有推送消息，如果有，则显示对应数据，同时清空推送消息
     *
     * @return 是否有推送消息
     */
    private fun showPushReportIfNeeded(): Boolean {
        return ReportPushManager.getInstance().checkWeeklyPushReportAndRun(context) { pushReport ->
            mNeedScrollToBottom = true
            var pushDate = pushReport.pushDate
            // 这里有坑
            // 服务器获取的周报列表信息，start_date是从周六晚8点开始计算的，start_date_show是从周一0点开始计算的
            // "start_date":"2018-04-14 20:00:00",
            // "end_date":"2018-04-21 19:59:59",
            // "start_date_show":1523721600, => 2018-04-15 00:00:00:00
            // "end_date_show":1524240000，=> 2018-04-21 00:00:00:00
            // 推送的pushDate是2018-04-14 20:00:00:00从周六晚8点开始计算的，要换算成周一0点开始计算的格式，否则后续无法匹配。
            pushDate += 3600 * 4 // 周六20:00 开始 => 周日00:00 开始
            if (mCurrentReport != null && mCurrentReport!!.start_date_show == pushDate) {
                mPresenter.refreshReport(mCurrentReport!!.start_date_show * 1000L)
            } else {
                scrollToTime(pushDate * 1000L)
            }
        }
    }

    private fun preloadData() {
        mPresenter.getPreloadReports(mAdapter.getItem(0).start_date_show * 1000L)
    }
}
