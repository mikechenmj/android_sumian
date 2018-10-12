package com.sumian.hw.report

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.sumian.hw.base.HwBasePresenter
import com.sumian.hw.guideline.dialog.ReportGuidelineDialog
import com.sumian.hw.guideline.utils.GuidelineUtils
import com.sumian.hw.log.LogManager
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.report.bean.PushReport
import com.sumian.hw.report.calendar.CalendarDialog
import com.sumian.hw.report.fragment.DailyReportFragment
import com.sumian.hw.report.fragment.WeeklyReportFragment
import com.sumian.hw.report.widget.tab.TabIndicatorView
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.theme.three.base.SkinBaseFragment
import com.sumian.sd.theme.three.loader.SkinManager
import kotlinx.android.synthetic.main.hw_fragment_main_report.*
import kotlinx.android.synthetic.main.hw_lay_tab_indicator_view.view.*

@Suppress("UNREACHABLE_CODE")
/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

class ReportFragment : SkinBaseFragment<HwBasePresenter>(), TabIndicatorView.OnSwitchIndicatorCallback, OnEnterListener {

    companion object {

        fun newInstance(): ReportFragment {
            return ReportFragment()
        }

    }

    private var mCurrentPosition = 0

    private val mFTags by lazy {
        arrayOf(DailyReportFragment::class.java.simpleName, WeeklyReportFragment::class.java.simpleName)
    }

    private val mBroadcastReceiver: BroadcastReceiver  by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    CalendarDialog.ACTION_SELECT_DATE -> {
                        tab_indicator_view.updateTabsUiBySelectPosition(0)
                        onSwitchIndicator(tab_indicator_view, 0)
                    }
                }
            }
        }
    }

    private val mReportGuidelineDialog: ReportGuidelineDialog  by lazy {
        ReportGuidelineDialog(context!!)
    }

    private val currentShowReportTime: Long
        get() {
            val fragmentManager = fragmentManager ?: return 0
            val dailyReportFragment = fragmentManager.findFragmentByTag(mFTags[0]) as DailyReportFragment
            return dailyReportFragment.currentReportTime
        }

    override fun getLayoutId(): Int {
        return R.layout.hw_fragment_main_report
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        tab_indicator_view?.let {
            it.setOnSwitchIndicatorCallback(this)
            dynamicAddView(it, "background", R.color.n1_color_day)
        }
    }

    override fun initData() {
        super.initData()
        val filter = IntentFilter()
        filter.addAction(CalendarDialog.ACTION_SELECT_DATE)
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(mBroadcastReceiver, filter)
        onEnter(null)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            if (mReportGuidelineDialog.isShowing) {
                mReportGuidelineDialog.dismiss()
            }
        }
    }

    override fun onRelease() {
        super.onRelease()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
    }

    override fun onEnter(data: String?) {
        LogManager.appendUserOperationLog("点击进入 '报告' 页面")
        // check job scheduler
        AppManager.getJobScheduler().checkJobScheduler()
        // check guideline
        if (GuidelineUtils.needShowDailyUserGuide()) {
            mReportGuidelineDialog.show()
        }
        tab_indicator_view?.iv_back?.visibility = if (SkinManager.getInstance().isNightMode) View.INVISIBLE else View.VISIBLE
    }

    override fun onBack(v: View) {
        activity?.finish()
    }

    override fun onSwitchIndicator(v: View, position: Int) {
        LogManager.appendPhoneLog("com.sumian.app.improve.report.ReportFragment.onSwitchIndicator position: $position")
        showFragmentByPosition(position)
        mCurrentPosition = position
    }

    override fun onShowCalendar(v: View) {
        CalendarDialog.create(currentShowReportTime).show(fragmentManager!!, CalendarDialog::class.java.simpleName)
    }

    private fun showFragmentByPosition(position: Int) {
        FragmentUtil.switchFragment(R.id.report_fragment_container,
                fragmentManager!!,
                mFTags,
                position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return if (position == 0) {
                            DailyReportFragment.newInstance()
                        } else {
                            WeeklyReportFragment.newInstance()
                        }
                    }
                }
                , FragmentUtil.DefaultRunOnCommitCallbackImpl())
    }
}
