package com.sumian.app.tab.report.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.common.util.TimeUtil;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.network.response.DaySleepReport;
import com.sumian.app.tab.report.activity.DaySleepDetailReportActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class SleepSegmentAdapter extends RecyclerView.Adapter<SleepSegmentAdapter.ViewHolder> {

    private List<DaySleepReport> mData;

    public SleepSegmentAdapter() {
        this.mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate
                (R.layout.hw_lay_sleep_segment_item, parent, false));
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DaySleepReport item = this.mData.get(position);
        holder.init(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<DaySleepReport> getData() {
        return mData;
    }

    public void addAll(List<DaySleepReport> sleepDetailReports) {
        int position = this.mData.size();
        this.mData.addAll(sleepDetailReports);
        notifyItemRangeInserted(position, sleepDetailReports.size());
    }

    public void clear() {
        notifyItemRangeRemoved(0, mData.size());
        this.mData.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mIvPoint;
        TextView mTvMonth;
        TextView mTvWeek;
        TextView mTvTimeSegment;
        TextView mTvSleepyDurationHour;
        TextView mTvSleepyDurationMin;

        private DaySleepReport mSleepDetailReport;


        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mIvPoint = itemView.findViewById(R.id.iv_point);
            mTvMonth = itemView.findViewById(R.id.tv_month);
            mTvWeek = itemView.findViewById(R.id.tv_week);
            mTvTimeSegment = itemView.findViewById(R.id.tv_time_segment);
            mTvSleepyDurationHour = itemView.findViewById(R.id.tv_sleep_duration_hour);
            mTvSleepyDurationMin = itemView.findViewById(R.id.tv_sleep_duration_min);
        }

        public void init(DaySleepReport item) {
            String doctorsEvaluation = item.getDoctors_evaluation();
            if (TextUtils.isEmpty(doctorsEvaluation)) {
                mIvPoint.setImageResource(R.mipmap.ic_report_graypoint);
            } else {
                mIvPoint.setImageResource(item.getIs_read() == 1 ? R.mipmap.ic_report_bluepoint : R.mipmap.ic_report_redpoint);
            }
            int toTime = item.getTo_time();//睡眠特征采集结束时间
            this.mTvMonth.setText(TimeUtil.formatDate(toTime));
            this.mTvWeek.setText(TimeUtil.formatWeek(toTime));

            int fromTime = item.getFrom_time();//数据开始采集时间
            this.mTvTimeSegment.setText(TimeUtil.formatFromTime2ToTime(fromTime, toTime));//数据采集时间段

            int sleepDuration = item.getSleep_duration();
            this.mTvSleepyDurationHour.setText(TimeUtil.calculateHour(sleepDuration));
            this.mTvSleepyDurationHour.setTypeface(UiUtil.getTypeface());
            this.mTvSleepyDurationMin.setText(TimeUtil.calculateMin(sleepDuration));
            this.mTvSleepyDurationMin.setTypeface(UiUtil.getTypeface());

            this.mSleepDetailReport = item;
        }

        @Override
        public void onClick(View v) {
            DaySleepDetailReportActivity.show(v.getContext(), mSleepDetailReport.getId());
        }
    }
}
