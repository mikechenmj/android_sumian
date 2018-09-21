package com.sumian.hw.report.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumian.hw.log.LogManager;
import com.sumian.hw.network.response.SleepDurationReport;
import com.sumian.hw.report.widget.SleepAvgAndCompareView;
import com.sumian.hw.report.widget.histogram.SleepHistogramView;
import com.sumian.hw.widget.refresh.BlueRefreshView;
import com.sumian.sd.R;
import com.sumian.sd.theme.three.IDynamicNewView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeeklyReportAdapter extends RecyclerView.Adapter<WeeklyReportAdapter.ViewHolder> {

    private List<SleepDurationReport> mItems = new ArrayList<>(0);
    private OnWeekReportCallback mWeekReportCallback;

    public WeeklyReportAdapter setWeekReportCallback(OnWeekReportCallback weekReportCallback) {
        mWeekReportCallback = weekReportCallback;
        return this;
    }

    private void updateItem(int position, SleepDurationReport sleepDurationReport) {
        mItems.set(position, sleepDurationReport);
        notifyItemChanged(position);
    }

    public void updateItem(SleepDurationReport weekReport) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getStart_date_show() == weekReport.getStart_date_show()) {
                updateItem(i, weekReport);
            }
        }
    }

    public SleepDurationReport getItem(int position) {
        return mItems.get(position);
    }

    public int getPosition(long time) {
        for (int i = 0; i < mItems.size(); i++) {
            SleepDurationReport report = mItems.get(i);
            if (report.isTimeBetweenStartAndEnd(time)) {
                return i;
            }
        }
        return -1;
    }

    public void addAllDataAtHead(List<SleepDurationReport> weekReports) {
        if (weekReports == null || weekReports.size() == 0) {
            return;
        }
        boolean hasPlaceHoldData = false;
        if (mItems.size() == 1) {
            SleepDurationReport report = mItems.get(0);
            if (report.isPlaceHoldData) {
                hasPlaceHoldData = true;
            }
        }
        if (hasPlaceHoldData) {
            mItems.remove(0);
            mItems.addAll(weekReports);
            notifyDataSetChanged();
        } else {
            mItems.addAll(0, weekReports);
            notifyItemRangeInserted(0, weekReports.size());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_lay_item_week_report, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepDurationReport weekReport = mItems.get(position);
        LogManager.appendPhoneLog(String.format(Locale.getDefault(), "\n---position: %s, report: %s, dataSize: %d, list: %s", position, weekReport.getStart_date(), mItems.size(), getReportListString(mItems)));
        holder.initView(weekReport, mWeekReportCallback);
    }

    private String getReportListString(List<SleepDurationReport> durationReports) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SleepDurationReport report : durationReports) {
            stringBuilder.append(report.getStart_date()).append(",");
        }
        return stringBuilder.toString();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @SuppressWarnings("deprecation")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

        private BlueRefreshView mRefresh;
        private NestedScrollView mNestedScrollView;
        private ImageView mIvPre;
        private TextView mTvWeekQuantum;
        private ImageView mIvNext;
        private SleepHistogramView mSleepHistogramView;
        private SleepAvgAndCompareView mDailySleepAvgCompareView;
        private SleepAvgAndCompareView mDailySleepLightAvgCompareView;
        private SleepAvgAndCompareView mDailySleepDeepAvgCompareView;
        private SleepAvgAndCompareView mDailySleepAwakeAvgCompareView;
        private CardView mLaySleepDataLessContainer;

        private SleepDurationReport mItem;

        private OnWeekReportCallback mWeekReportCallback;

        public ViewHolder(View itemView) {
            super(itemView);
            mRefresh = itemView.findViewById(R.id.refresh);
            mNestedScrollView = itemView.findViewById(R.id.nested_scroll_view);
            mIvPre = itemView.findViewById(R.id.iv_pre);
            mTvWeekQuantum = itemView.findViewById(R.id.tv_week_quantum);
            mIvNext = itemView.findViewById(R.id.iv_next);
            mSleepHistogramView = itemView.findViewById(R.id.week_sleep_histogram_view);
            mDailySleepAvgCompareView = itemView.findViewById(R.id.daily_sleep_avg_compare_view);
            mDailySleepLightAvgCompareView = itemView.findViewById(R.id.daily_sleep_light_avg_compare_view);
            mDailySleepDeepAvgCompareView = itemView.findViewById(R.id.daily_sleep_deep_avg_compare_view);
            mDailySleepAwakeAvgCompareView = itemView.findViewById(R.id.daily_sleep_awake_avg_compare_view);

            mLaySleepDataLessContainer = itemView.findViewById(R.id.lay_sleep_data_less_container);
            mLaySleepDataLessContainer.setOnClickListener(this);

            itemView.findViewById(R.id.iv_pre).setOnClickListener(this);
            itemView.findViewById(R.id.iv_next).setOnClickListener(this);
        }

        public void initView(SleepDurationReport item, OnWeekReportCallback onWeekReportCallback) {
            this.mItem = item;

            this.mWeekReportCallback = onWeekReportCallback;

            mRefresh.setRefreshing(false);
            mRefresh.setOnRefreshListener(this);
            mIvPre.setEnabled(true);
            mIvPre.setVisibility(View.VISIBLE);
            mIvNext.setEnabled(true);
            mIvNext.setVisibility(View.VISIBLE);

            // 改为了可以无限往前滑动，所以注释掉下面的代码
//            String createdAt = AppManager.getAccountViewModel().getUserInfo().getCreated_at();
//            if (!TextUtils.isEmpty(createdAt)) {
//                try {
//                    Date parseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(createdAt);
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(parseDate);
//                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
//                    int weekOffset = calendar.get(Calendar.DAY_OF_WEEK);
//                    int weekCount = 7 - weekOffset;
//                    long dayUnixTime = 60 * 60 * 24 * 1000L * weekCount;
//                    long currentStartTime = calendar.getUnixTime() - dayUnixTime;
//                    this.mIvPre.setVisibility(item.getStart_date_show() * 1000L > currentStartTime ? View.VISIBLE : View.INVISIBLE);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }

            Calendar currentCalendar = Calendar.getInstance();

            int weekOffset = currentCalendar.get(Calendar.DAY_OF_WEEK);

            int weekCount = 7 - weekOffset;

            currentCalendar.set(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE), 0, 0, 0);

            long dayUnixTime = 60 * 60 * 24 * 1000L * weekCount;

            long currentStartTime = currentCalendar.getTimeInMillis() - dayUnixTime;

            long currentEndTime = (currentStartTime + 6 * 60 * 60 * 24 * 1000L);


            this.mIvNext.setVisibility(item.getEnd_date_show() * 1000L < currentEndTime ? View.VISIBLE : View.INVISIBLE);

            this.mSleepHistogramView.addSleepData(item.getSleeps());

            mDailySleepAvgCompareView.setAvgDuration(item.getAvg_sleep_duration()).setCompareDuration(item.getDiff_avg_sleep_duration());
            mDailySleepLightAvgCompareView.setAvgDuration(item.getAvg_light_duration()).setCompareDuration(item.getDiff_avg_light_duration());
            mDailySleepDeepAvgCompareView.setAvgDuration(item.getAvg_deep_duration()).setCompareDuration(item.getDiff_avg_deep_duration());
            mDailySleepAwakeAvgCompareView.setAvgDuration(item.getAvg_awake_duration()).setCompareDuration(item.getDiff_avg_awake_duration());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getStart_date_show() * 1000L);
            int preYear = calendar.get(Calendar.YEAR);
            int preMonth = calendar.get(Calendar.MONTH);
            int preDate = calendar.get(Calendar.DATE);

            calendar.setTimeInMillis(item.getEnd_date_show() * 1000L);
            int nextYear = calendar.get(Calendar.YEAR);
            int nextMonth = calendar.get(Calendar.MONTH);
            int nextDate = calendar.get(Calendar.DATE);

            this.mTvWeekQuantum.setText(String.format(Locale.getDefault(), "%d%s%02d%s%02d%s%d%s%02d%s%02d",
                    preYear, "/", (preMonth + 1), "/", preDate, " - ", nextYear, "/", (nextMonth + 1), "/", nextDate));

            this.mLaySleepDataLessContainer.setVisibility(item.getAdvice() == null ? View.GONE : View.VISIBLE);

            if (mItem.needScrollToBottom) {
                mNestedScrollView.post(() -> mNestedScrollView.fullScroll(ScrollView.FOCUS_DOWN));
                mItem.needScrollToBottom = false;
            }
            registerThemeChangeEvent();
        }

        private void registerThemeChangeEvent() {
            Context context = itemView.getContext();

            if (context instanceof IDynamicNewView) {

                IDynamicNewView iDynamicNewView = (IDynamicNewView) context;

                iDynamicNewView.dynamicAddView(mRefresh, "brv_progress_color", R.color.n2_color_day);
                iDynamicNewView.dynamicAddView(mRefresh, "brv_progress_bg_color", R.color.l2_color_day);

                iDynamicNewView.dynamicAddView(mSleepHistogramView, "deep_color", R.color.g1_color_day);
                iDynamicNewView.dynamicAddView(mSleepHistogramView, "light_color", R.color.g2_color_day);
                iDynamicNewView.dynamicAddView(mSleepHistogramView, "eog_color", R.color.g2_color_day);
                iDynamicNewView.dynamicAddView(mSleepHistogramView, "sober_color", R.color.g3_color_day);
                iDynamicNewView.dynamicAddView(mSleepHistogramView, "coordinate_color", R.color.l3_color_day);
                iDynamicNewView.dynamicAddView(mSleepHistogramView, "label_text_color", R.color.t2_color_day);

                iDynamicNewView.dynamicAddView(mDailySleepAvgCompareView, "label_icon", R.drawable.ic_home_sleep_time_day);
                iDynamicNewView.dynamicAddView(mDailySleepLightAvgCompareView, "label_icon", R.drawable.ic_report_light_sleep_day);
                iDynamicNewView.dynamicAddView(mDailySleepDeepAvgCompareView, "label_icon", R.drawable.ic_report_deep_sleep_day);
                iDynamicNewView.dynamicAddView(mDailySleepAwakeAvgCompareView, "label_icon", R.drawable.ic_report_awake_day);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_pre:
                case R.id.iv_next:
                    mWeekReportCallback.onSwitchWeek(v, getAdapterPosition(), mItem);
                    break;
                case R.id.lay_sleep_data_less_container:
                    mWeekReportCallback.onShowSleepAdvice(v, getAdapterPosition(), mItem);
                    break;
            }
        }

        @Override
        public void onRefresh() {
            mWeekReportCallback.onRefreshWeekReport(mRefresh, getAdapterPosition(), mItem);
        }

        public void setRefreshing(boolean refreshing) {
            mRefresh.setRefreshing(refreshing);
        }
    }

    public interface OnWeekReportCallback {

        void onSwitchWeek(View v, int position, SleepDurationReport item);

        void onRefreshWeekReport(View v, int position, SleepDurationReport item);

        void onShowSleepAdvice(View v, int position, SleepDurationReport item);

    }

}
