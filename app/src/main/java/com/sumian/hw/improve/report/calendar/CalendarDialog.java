package com.sumian.hw.improve.report.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.improve.widget.report.LoadViewPagerRecyclerView;
import com.sumian.hw.widget.BaseDialogFragment;
import com.sumian.sd.R;

import java.util.Calendar;
import java.util.List;

import static com.sumian.hw.improve.report.calendar.CalendarAdapter.ViewHolder.CALENDAR_GO_NEXT_ACTION;
import static com.sumian.hw.improve.report.calendar.CalendarAdapter.ViewHolder.CALENDAR_GO_PRE_ACTION;
import static com.sumian.hw.improve.report.calendar.CalendarAdapter.ViewHolder.CALENDER_GO_BACK_TODAY_ACTION;
import static com.sumian.hw.improve.report.calendar.CalendarAdapter.ViewHolder.EXTRA_POSITION;

/**
 * Created by jzz
 * on 2018/3/14.
 * desc:
 */

public class CalendarDialog extends BaseDialogFragment implements CalendarView.OnCalenderListener, CalendarReportContract.View, LoadViewPagerRecyclerView.OnLoadCallback {

    public static final String ACTION_SELECT_DATE = "com.sumian.app.intent.action.SELECT_DATE";
    public static final String EXTRA_DATE = "com.sumian.app.intent.extra.SELECT_DATE";
    public static final String CURRENT_SHOW_MILLIS = "CURRENT_SHOW_MILLIS";

    LoadViewPagerRecyclerView mCalendarPager;

    private Calendar mDefaultCalendar;
    private CalendarReportContract.Presenter mPresenter;
    private CalendarAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private long mCurrentShowMillis;

    public static CalendarDialog create(long currentShowMillis) {
        Bundle bundle = new Bundle();
        bundle.putLong(CURRENT_SHOW_MILLIS, currentShowMillis);
        CalendarDialog calendarDialog = new CalendarDialog();
        calendarDialog.setArguments(bundle);
        return calendarDialog;
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_dialog_calendar;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mCalendarPager = rootView.findViewById(R.id.calendar_pager);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mCurrentShowMillis = arguments.getLong(CURRENT_SHOW_MILLIS, 0L);
        }
        mCalendarPager.setOnLoadCallback(this);
        mCalendarPager.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mCalendarPager.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CalendarAdapter();
        mCalendarPager.setAdapter(mAdapter);
        mAdapter.setOnCalenderListener(this);
        mAdapter.setCurrentShowTime(mCurrentShowMillis);
        CalendarReportPresenter.init(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initData() {
        super.initData();
        mDefaultCalendar = Calendar.getInstance();
        long timeInMillis = getRequestUnixTime();
        mPresenter.getOneCalendarReportInfo(timeInMillis, true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CALENDAR_GO_NEXT_ACTION);
        filter.addAction(CALENDAR_GO_PRE_ACTION);
        filter.addAction(CALENDER_GO_BACK_TODAY_ACTION);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CALENDER_GO_BACK_TODAY_ACTION:
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int date = calendar.get(Calendar.DATE);
                        calendar.set(year, month, date, 0, 0, 0);
                        showCalender(calendar.getTimeInMillis() / 1000L);
                        break;
                    case CALENDAR_GO_NEXT_ACTION:
                    case CALENDAR_GO_PRE_ACTION:
                        int position = intent.getIntExtra(EXTRA_POSITION, 0);
                        if (mCalendarPager != null) {
                            mCalendarPager.smoothScrollToPosition(position);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void release() {
        Context context = getContext();
        if (mBroadcastReceiver != null && context != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
        }
        super.release();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void showCalender(long unixTime) {

        if (unixTime > Calendar.getInstance().getTimeInMillis() / 1000L) {
            ToastHelper.show("手机时间有误，请检查手机时间");
            return;
        }

        Intent intent = new Intent(ACTION_SELECT_DATE);
        intent.putExtra(EXTRA_DATE, unixTime);

        boolean sendBroadcast = LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        //true  表示该广播已经被已经注册号的广播接收到
        if (sendBroadcast) {
            dismiss();
        }
    }

    @Override
    public void setPresenter(CalendarReportContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onGetOneCalendarReportInfoSuccess(List<PagerCalendarItem> items) {
        int itemCount = mAdapter.getItemCount();
        if (itemCount == 0) {
            mAdapter.addAllItems(items);
            int currentShowTimePosition = getCurrentShowTimePosition(items);
            if (currentShowTimePosition == -1) {
                currentShowTimePosition = mAdapter.getItemCount() - 1;
            }
            mCalendarPager.scrollToPosition(currentShowTimePosition);
        } else {
            mAdapter.addAllHeads(items);
        }
    }

    private int getCurrentShowTimePosition(List<PagerCalendarItem> items) {
        Calendar startDayOfMonthOfCurrentShowTime = TimeUtil.getStartDayOfMonth(mCurrentShowMillis);
        for (int i = 0; i < items.size(); i++) {
            Calendar startDayOfMonthOfItem = TimeUtil.getStartDayOfMonth(items.get(i).getMonthTimeInMillis());
//            LogManager.appendFormatPhoneLog("calendar time: %s -- %s", TimeUtil.formatCalendar(startDayOfMonthOfCurrentShowTime), TimeUtil.formatCalendar(startDayOfMonthOfItem));
            if (startDayOfMonthOfCurrentShowTime.getTimeInMillis() == startDayOfMonthOfItem.getTimeInMillis()) {
                return i;
            }
        }
        return -1;
    }

    private long getRequestUnixTime() {
        int year = mDefaultCalendar.get(Calendar.YEAR);
        int month = mDefaultCalendar.get(Calendar.MONTH);
        //int date = calendar.get(Calendar.DATE);
        Calendar calendar = (Calendar) mDefaultCalendar.clone();
        calendar.set(year, month, 1, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000L;
    }

    @Override
    public void loadPre() {
        PagerCalendarItem item = mAdapter.getItem(0);
        if (item.monthTimeUnix <= item.initTimeUnix) {
            return;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(item.monthTimeUnix * 1000L);
        mPresenter.getOneCalendarReportInfo(instance.getTimeInMillis() / 1000L, false);
    }
}
