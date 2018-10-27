package com.sumian.hw.report.calendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.common.helper.ToastHelper
import com.sumian.hw.common.util.TimeUtil
import com.sumian.hw.report.widget.LoadViewPagerRecyclerView
import com.sumian.hw.widget.BaseDialogFragment
import com.sumian.sd.R
import java.util.*

@Suppress("DEPRECATION")
/**
 * Created by jzz
 * on 2018/3/14.
 * desc:
 */

class CalendarDialog : BaseDialogFragment(), CalendarView.OnCalenderListener, CalendarReportContract.View, LoadViewPagerRecyclerView.OnLoadCallback {

    private var mCalendarPager: LoadViewPagerRecyclerView? = null

    private var mDefaultCalendar: Calendar? = null
    private var mPresenter: CalendarReportContract.Presenter? = null
    private var mAdapter: CalendarAdapter? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var mCurrentShowMillis: Long = 0

    private//int date = calendar.get(Calendar.DATE);
    val requestUnixTime: Long
        get() {
            val year = mDefaultCalendar!!.get(Calendar.YEAR)
            val month = mDefaultCalendar!!.get(Calendar.MONTH)
            val calendar = mDefaultCalendar!!.clone() as Calendar
            calendar.set(year, month, 1, 0, 0, 0)
            return calendar.timeInMillis / 1000L
        }

    override fun getLayout(): Int {
        return R.layout.hw_lay_dialog_calendar
    }

    override fun initView(rootView: View) {
        super.initView(rootView)

        val cardView = rootView.findViewById<CardView>(R.id.card_view)
        cardView.setCardBackgroundColor(resources.getColor(R.color.b2_color_day))

        val arguments = arguments
        if (arguments != null) {
            mCurrentShowMillis = arguments.getLong(CURRENT_SHOW_MILLIS, 0L)
        }

        mCalendarPager = rootView.findViewById(R.id.calendar_pager)
        mCalendarPager!!.setOnLoadCallback(this)
        mCalendarPager!!.layoutManager = LinearLayoutManager(rootView.context, LinearLayoutManager.HORIZONTAL, false)
        mCalendarPager!!.itemAnimator = null
        mAdapter = CalendarAdapter()
        mCalendarPager!!.adapter = mAdapter
        mAdapter!!.setOnCalenderListener(this)
        mAdapter!!.setCurrentShowTime(mCurrentShowMillis)
        CalendarReportPresenter.init(this)
    }

    override fun initData() {
        super.initData()
        mDefaultCalendar = Calendar.getInstance()
        val timeInMillis = requestUnixTime
        mPresenter!!.getOneCalendarReportInfo(timeInMillis, true)
        val filter = IntentFilter()
        filter.addAction(CalendarAdapter.ViewHolder.CALENDAR_GO_NEXT_ACTION)
        filter.addAction(CalendarAdapter.ViewHolder.CALENDAR_GO_PRE_ACTION)
        filter.addAction(CalendarAdapter.ViewHolder.CALENDER_GO_BACK_TODAY_ACTION)
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    CalendarAdapter.ViewHolder.CALENDER_GO_BACK_TODAY_ACTION -> {
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val date = calendar.get(Calendar.DATE)
                        calendar.set(year, month, date, 0, 0, 0)
                        showCalender(calendar.timeInMillis / 1000L)
                    }
                    CalendarAdapter.ViewHolder.CALENDAR_GO_NEXT_ACTION, CalendarAdapter.ViewHolder.CALENDAR_GO_PRE_ACTION -> {
                        val position = intent.getIntExtra(CalendarAdapter.ViewHolder.EXTRA_POSITION, 0)
                        if (mCalendarPager != null) {
                            mCalendarPager!!.smoothScrollToPosition(position)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(context!!).registerReceiver(mBroadcastReceiver!!, filter)
    }

    override fun release() {
        val context = context
        if (mBroadcastReceiver != null && context != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver!!)
        }
        super.release()
    }

    override fun showCalender(unixTime: Long) {

        if (unixTime > Calendar.getInstance().timeInMillis / 1000L) {
            ToastHelper.show("手机时间有误，请检查手机时间")
            return
        }

        val intent = Intent(ACTION_SELECT_DATE)
        intent.putExtra(EXTRA_DATE, unixTime)

        val sendBroadcast = LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        //true  表示该广播已经被已经注册号的广播接收到
        if (sendBroadcast) {
            dismiss()
        }
    }

    override fun setPresenter(presenter: CalendarReportContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun onGetOneCalendarReportInfoSuccess(items: List<PagerCalendarItem>) {
        val itemCount = mAdapter!!.itemCount
        if (itemCount == 0) {
            mAdapter!!.addAllItems(items)
            var currentShowTimePosition = getCurrentShowTimePosition(items)
            if (currentShowTimePosition == -1) {
                currentShowTimePosition = mAdapter!!.itemCount - 1
            }
            mCalendarPager!!.scrollToPosition(currentShowTimePosition)
        } else {
            mAdapter!!.addAllHeads(items)
        }
    }

    private fun getCurrentShowTimePosition(items: List<PagerCalendarItem>): Int {
        val startDayOfMonthOfCurrentShowTime = TimeUtil.getStartDayOfMonth(mCurrentShowMillis)
        for (i in items.indices) {
            val startDayOfMonthOfItem = TimeUtil.getStartDayOfMonth(items[i].monthTimeInMillis)
            //            LogManager.appendFormatPhoneLog("calendar time: %s -- %s", TimeUtil.formatCalendar(startDayOfMonthOfCurrentShowTime), TimeUtil.formatCalendar(startDayOfMonthOfItem));
            if (startDayOfMonthOfCurrentShowTime.timeInMillis == startDayOfMonthOfItem.timeInMillis) {
                return i
            }
        }
        return -1
    }

    override fun loadPre() {
        val item = mAdapter!!.getItem(0)
        if (item.monthTimeUnix <= item.initTimeUnix) {
            return
        }
        val instance = Calendar.getInstance()
        instance.timeInMillis = item.monthTimeUnix * 1000L
        mPresenter!!.getOneCalendarReportInfo(instance.timeInMillis / 1000L, false)
    }

    override fun loadMore() {

    }

    companion object {

        const val ACTION_SELECT_DATE = "com.sumian.app.intent.action.SELECT_DATE"
        const val EXTRA_DATE = "com.sumian.app.intent.extra.SELECT_DATE"
        const val CURRENT_SHOW_MILLIS = "CURRENT_SHOW_MILLIS"

        fun create(currentShowMillis: Long): CalendarDialog {
            val bundle = Bundle()
            bundle.putLong(CURRENT_SHOW_MILLIS, currentShowMillis)
            val calendarDialog = CalendarDialog()
            calendarDialog.arguments = bundle
            return calendarDialog
        }
    }
}
