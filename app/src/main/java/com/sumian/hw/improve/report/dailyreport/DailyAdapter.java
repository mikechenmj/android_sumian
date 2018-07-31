package com.sumian.hw.improve.report.dailyreport;

import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.improve.report.note.SleepNote;
import com.sumian.hw.improve.widget.ReportSleepDurationView;
import com.sumian.hw.improve.widget.SwitchDateView;
import com.sumian.hw.improve.widget.TouchDailySleepHistogramView;
import com.sumian.hw.improve.widget.note.SleepNoteView;
import com.sumian.hw.widget.refresh.BlueRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2018/3/12.
 * desc:
 */

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private ArrayList<DailyReport> mData;
    private SwitchDateView.OnSwitchDateListener mOnSwitchDateListener;
    private View.OnClickListener mOnClickListener;
    private OnRefreshCallback mOnRefreshCallback;

    DailyAdapter() {
        this.mData = new ArrayList<>(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_lay_item_today_repot, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyReport dailyReport = mData.get(position);
        holder.initView(dailyReport, mOnSwitchDateListener, mOnClickListener, mOnRefreshCallback);
    }

    public DailyAdapter setOnSwitchDateListener(SwitchDateView.OnSwitchDateListener onSwitchDateListener) {
        mOnSwitchDateListener = onSwitchDateListener;
        return this;
    }

    public DailyAdapter setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return this;
    }

    public DailyAdapter setOnRefreshCallback(OnRefreshCallback onRefreshCallback) {
        mOnRefreshCallback = onRefreshCallback;
        return this;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(DailyReport dailyReport) {
        int size = mData.size();
        mData.add(dailyReport);
        notifyItemInserted(size);
    }

    public void initAddAll(List<DailyReport> dailyReports) {
        updateItem(0, dailyReports.get(dailyReports.size() - 1));
        insertDataToHead(dailyReports.subList(0, dailyReports.size() - 1));
    }

    public void addAll(List<DailyReport> dailyReports) {
        int size = mData.size();
        mData.addAll(dailyReports);
        notifyItemRangeInserted(size, dailyReports.size());
    }

    public void insertDataToHead(List<DailyReport> dailyReports) {
        mData.addAll(0, dailyReports);
        notifyItemRangeInserted(0, dailyReports.size());
    }

    public int updateItem(DailyReport dailyReport) {
        int position = getPosition(dailyReport.date);
        updateItem(position, dailyReport);
        return position;
    }

    public void updateItem(int position, DailyReport dailyReport) {
        if (position == -1) {
            return;
        }
        mData.set(position, dailyReport);
        notifyItemChanged(position);
    }

    public int getPosition(int unixTime) {
        for (int i = 0, length = mData.size(); i < length; i++) {
            DailyReport dailyReport = mData.get(i);
            if (dailyReport.date == unixTime) {
                return i;
            }
        }
        return -1;
    }

    public DailyReport getItem(int position) {
        return mData.get(position);
    }

    public long getItemDate(int position) {
        return mData.get(position).date;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements SwipeRefreshLayout.OnRefreshListener {

        SwitchDateView mSwitchDateView;
        BlueRefreshView mBlueRefreshView;
        NestedScrollView mNestedScrollView;
        TouchDailySleepHistogramView mDaySleepHistogramView;
        ReportSleepDurationView mReportSleepDurationView;
        SleepNoteView mSleepNoteView;
        CardView mDoctorEvaluate;
        TextView mTvDoctorEvaluate;

        private DailyReport mDailyReport;

        private OnRefreshCallback mOnRefreshCallback;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mSwitchDateView = itemView.findViewById(R.id.switch_date_view);
            mBlueRefreshView = itemView.findViewById(R.id.refresh);
            mNestedScrollView = itemView.findViewById(R.id.nested_scroll_view);
            mDaySleepHistogramView = itemView.findViewById(R.id.day_sleep_histogram_view);
            mReportSleepDurationView = itemView.findViewById(R.id.report_sleep_duration_view);
            mSleepNoteView = itemView.findViewById(R.id.sleep_note_view);
            mDoctorEvaluate = itemView.findViewById(R.id.doctor_evaluate);
            mTvDoctorEvaluate = itemView.findViewById(R.id.tv_doctor_evaluate);
        }

        public void initView(DailyReport dailyReport, SwitchDateView.OnSwitchDateListener switchDateListener, View.OnClickListener clickListener, OnRefreshCallback onRefreshCallback) {
            this.mDailyReport = dailyReport;
            this.mBlueRefreshView.setRefreshing(false);

            mOnRefreshCallback = onRefreshCallback;

            mSwitchDateView.setUnixTime(dailyReport.date);
            mSwitchDateView.setOnSwitchDateListener(switchDateListener);
            mBlueRefreshView.setOnRefreshListener(this);

            mDaySleepHistogramView.setData(dailyReport);
            mReportSleepDurationView.setSleepTodayDuration(mDailyReport.sleep_duration);
            mReportSleepDurationView.setLightSleepData(mDailyReport.light_duration, mDailyReport.light_duration_percent);
            mReportSleepDurationView.setDeepSleepData(mDailyReport.deep_duration, mDailyReport.deep_duration_percent);

            mSleepNoteView.addOnClickListener(clickListener);
            if (!TextUtils.isEmpty(mDailyReport.wrote_diary_at)) {

                SleepNote sleepNote = new SleepNote();
                sleepNote.wakeUpMood = mDailyReport.wake_up_mood;
                sleepNote.bedtimeState = mDailyReport.bedtime_state;
                sleepNote.remark = mDailyReport.remark;

                mSleepNoteView.setSleepNote(sleepNote);
                mSleepNoteView.show();
            } else {
                mSleepNoteView.hide();
            }

            if (!TextUtils.isEmpty(mDailyReport.doctors_evaluation)) {
                mDoctorEvaluate.setVisibility(View.VISIBLE);
                mTvDoctorEvaluate.setText(mDailyReport.doctors_evaluation);
            } else {
                mDoctorEvaluate.setVisibility(View.GONE);
            }

            if (mDailyReport.needScrollToBottom) {
                mNestedScrollView.post(() -> mNestedScrollView.fullScroll(ScrollView.FOCUS_DOWN));
                mDailyReport.needScrollToBottom = false;
            }
        }

        @Override
        public void onRefresh() {
            if (mOnRefreshCallback != null) {
                mOnRefreshCallback.onRefresh(getAdapterPosition(), mDailyReport);
            }
        }

        public void setRefreshing(boolean refreshing) {
            mBlueRefreshView.setRefreshing(refreshing);
        }
    }

    public interface OnRefreshCallback {

        void onRefresh(int position, DailyReport dailyReport);
    }
}
