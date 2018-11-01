package com.sumian.hw.report.calendar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.network.response.ErrorResponse;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.report.bean.ReadSleepRecordEvaluationResponse;
import com.sumian.hw.utils.TimeUtil;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<PagerCalendarItem> mItems;

    public void setOnCalenderListener(CalendarView.OnCalenderListener onCalenderListener) {
        mOnCalenderListener = onCalenderListener;
    }

    private CalendarView.OnCalenderListener mOnCalenderListener;
    private long mCurrentShowTime;

    CalendarAdapter() {
        this.mItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_lay_calendar_item, parent, false));
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PagerCalendarItem pagerCalendarItem = mItems.get(position);
        holder.initView(pagerCalendarItem);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addAllItems(List<PagerCalendarItem> items) {
        int size = mItems.size();
        mItems.addAll(items);
        notifyItemRangeInserted(size, items.size());
    }

    public void addAllHeads(List<PagerCalendarItem> items) {
        mItems.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    public PagerCalendarItem getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        static final String CALENDAR_GO_NEXT_ACTION = "com.sumian.app.action.GO_NEXT";
        static final String CALENDAR_GO_PRE_ACTION = "com.sumian.app.action.GO_PRE";
        static final String CALENDER_GO_BACK_TODAY_ACTION = "com.sumian.app.action.GO_BACK_TODAY";
        static final String EXTRA_POSITION = "com.sumian.app.extra.POSITION";

        private ImageView mIvPre;
        private TextView mTvDate;
        private ImageView mIvNext;
        private CalendarView mCalenderView;

        private PagerCalendarItem mItem;

        private CalendarItemSleepReport getReportByTime(long timeInMillis) {
            List<CalendarItemSleepReport> calendarItemSleepReports = mItem.mCalendarItemSleepReports;
            for (CalendarItemSleepReport report : calendarItemSleepReports) {
                LogManager.appendFormatPhoneLog("getReportByTime: %s --- %s", new Date(report.getDateInMillis()), new Date(timeInMillis));
                if (report.getDateInMillis() == timeInMillis) {
                    return report;
                }
            }
            return null;
        }

        private CalendarView.OnDateClickListener mOnDateClickListener = new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(long timeInMillis) {
                CalendarItemSleepReport report = getReportByTime(timeInMillis);
                if (report != null) {
                    boolean isRead = report.is_read;
                    if (!isRead) {
                        Map<String, Object> map = new HashMap<>(0);
                        map.put("id", String.valueOf(report.id));
                        AppManager
                                .getHwHttpService()
                                .readDayDoctorValuation(map)
                                .enqueue(new BaseSdResponseCallback<ReadSleepRecordEvaluationResponse>() {
                                    @Override
                                    protected void onFailure(@NotNull ErrorResponse errorResponse) {

                                    }

                                    @Override
                                    protected void onSuccess(ReadSleepRecordEvaluationResponse response) {
                                    }

                                });
                    }
                    if (mOnCalenderListener != null) {
                        mOnCalenderListener.showCalender(timeInMillis / 1000);
                    }
                } //else {
//                    ToastHelper.show("当前没有睡眠数据");
                // }
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.tv_go_today).setOnClickListener(this);
            mIvPre = itemView.findViewById(R.id.iv_pre);
            mIvPre.setOnClickListener(this);
            mTvDate = itemView.findViewById(R.id.tv_date);
            mIvNext = itemView.findViewById(R.id.iv_next);
            mIvNext.setOnClickListener(this);
            mCalenderView = itemView.findViewById(R.id.calender);
            itemView.findViewById(R.id.tv_all_read).setOnClickListener(this);
        }

        public void initView(PagerCalendarItem pagerCalendarItem) {
            mItem = pagerCalendarItem;
            setPreAndNextVisibility(pagerCalendarItem);
            setTopDateTv(pagerCalendarItem);
            setCalendarViewData(pagerCalendarItem);
            mCalenderView.setOnDateClickListener(mOnDateClickListener);
        }

        private void setPreAndNextVisibility(PagerCalendarItem pagerCalendarItem) {
            if (pagerCalendarItem.monthTimeUnix == pagerCalendarItem.initTimeUnix) {
                mIvPre.setVisibility(View.INVISIBLE);
            } else {
                mIvPre.setVisibility(View.VISIBLE);
            }
            //获取今天的unixTime,计算出当前月份的时间戳
            Calendar instance = Calendar.getInstance();
            int year = instance.get(Calendar.YEAR);
            int month = instance.get(Calendar.MONTH);
            instance.set(year, month, 1, 0, 0, 0);
            if (pagerCalendarItem.monthTimeUnix == instance.getTimeInMillis() / 1000L) {
                mIvNext.setVisibility(View.INVISIBLE);
            } else {
                mIvNext.setVisibility(View.VISIBLE);
            }
        }

        private void setTopDateTv(PagerCalendarItem pagerCalendarItem) {
            Calendar instance;
            int year;
            int month;
            int monthTimeMills = pagerCalendarItem.monthTimeUnix;
            instance = Calendar.getInstance();
            instance.setTimeInMillis(monthTimeMills * 1000L);
            year = instance.get(Calendar.YEAR);
            month = instance.get(Calendar.MONTH);
            mTvDate.setText(String.format(Locale.getDefault(), "%d%s%02d", year, "/", month + 1));
        }

        private void setCalendarViewData(PagerCalendarItem pagerCalendarItem) {
            long monthTimeInMillis = pagerCalendarItem.getMonthTimeInMillis();
            List<CalendarItemSleepReport> reports = pagerCalendarItem.mCalendarItemSleepReports;
            int size = reports.size();
            int[] highlightDays = new int[size];
            int[] highlightDays2 = new int[1];
            int[] drawUnderlineDays = new int[size];
            int[] drawDotDays = new int[size];
            int[] drawBgDays = new int[1];
            if (reports.size() > 0) {
                long firstItemTimeInMillis = reports.get(0).getDateInMillis();
                boolean isInTheSameMonth = TimeUtil.isInTheSameMonth(mCurrentShowTime, firstItemTimeInMillis);
                if (isInTheSameMonth) {
                    Calendar currentShowTime = TimeUtil.getCalendar(mCurrentShowTime);
                    int currentShowDay = currentShowTime.get(Calendar.DAY_OF_MONTH);
                    drawBgDays[0] = currentShowDay;
                }
            }
            for (int i = 0; i < size; i++) {
                CalendarItemSleepReport report = reports.get(i);
                int date = report.date;
                Calendar calendar = TimeUtil.getCalendar(date * 1000L);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                highlightDays[i] = dayOfMonth;
                if (report.has_doctors_evaluation) {
                    drawUnderlineDays[i] = dayOfMonth;
                    if (!report.is_read) {
                        drawDotDays[i] = dayOfMonth;
                    }
                }
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (TimeUtil.isInTheSameMonth(currentTimeMillis, monthTimeInMillis)) {
                highlightDays2[0] = TimeUtil.getCalendar(currentTimeMillis).get(Calendar.DAY_OF_MONTH);
            }
            mCalenderView.setTime(monthTimeInMillis);
            mCalenderView.setHighlightDays(highlightDays)
                    .setHighlightDays2(highlightDays2)
                    .setDrawUnderlineDays(drawUnderlineDays)
                    .setDrawBgDays(drawBgDays);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.tv_go_today:
                    intent.setAction(CALENDER_GO_BACK_TODAY_ACTION);
                    intent.putExtra(EXTRA_POSITION, getItemCount() - 1);
                    LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
                    break;
                case R.id.iv_pre:
                    intent.setAction(CALENDAR_GO_PRE_ACTION);
                    intent.putExtra(EXTRA_POSITION, getAdapterPosition() - 1);
                    LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
                    break;
                case R.id.iv_next:
                    intent.setAction(CALENDAR_GO_NEXT_ACTION);
                    intent.putExtra(EXTRA_POSITION, getAdapterPosition() + 1);
                    LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
                    break;
                case R.id.tv_all_read:
                    if (mItem.mCalendarItemSleepReports == null || mItem.mCalendarItemSleepReports.size() <= 0) {
                        return;
                    }
                    setCalendarViewData(mItem);
                    Map<String, Object> map = new HashMap<>(0);
                    AppManager
                            .getHwHttpService()
                            .readDayDoctorValuation(map)
                            .enqueue(new BaseSdResponseCallback<ReadSleepRecordEvaluationResponse>() {

                                @Override
                                protected void onFailure(@NotNull ErrorResponse errorResponse) {

                                }

                                @Override
                                protected void onSuccess(ReadSleepRecordEvaluationResponse response) {
                                    mCalenderView.setDrawDotDays(new int[]{});
                                    mCalenderView.invalidate();
                                }

                            });
                    break;
                default:
                    break;
            }
        }
    }

    public void setCurrentShowTime(long timeInMillis) {
        mCurrentShowTime = timeInMillis;
    }
}
