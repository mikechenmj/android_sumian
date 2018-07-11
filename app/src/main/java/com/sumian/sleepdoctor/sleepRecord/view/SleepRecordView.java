package com.sumian.sleepdoctor.sleepRecord.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.record.bean.SleepPill;
import com.sumian.sleepdoctor.record.bean.SleepRecord;
import com.sumian.sleepdoctor.record.bean.SleepRecordAnswer;
import com.sumian.sleepdoctor.utils.TimeUtil;
import com.sumian.sleepdoctor.widget.record.pill.PillsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/31 20:02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordView extends LinearLayout {
    @BindView(R.id.title_view_sleep_record)
    TitleView titleViewSleepRecord;
    @BindView(R.id.ll_progress)
    LinearLayout llProgress;
    @BindView(R.id.tv_actual_work_and_reset_time)
    TextView tvActualWorkAndResetTime;
    @BindView(R.id.tv_sleep_time)
    TextView tvSleepTime;
    @BindView(R.id.tv_sleep_duration)
    TextView tvSleepDuration;
    @BindView(R.id.tv_fall_asleep_duration)
    TextView tvFallAsleepDuration;
    @BindView(R.id.tv_sleep_desc)
    TextView tvSleepDesc;
    @BindView(R.id.ll_sleep_record)
    LinearLayout llSleepRecord;
    @BindView(R.id.title_view_doctor_advise)
    TitleView titleViewDoctorAdvise;
    @BindView(R.id.tv_doctor_evaluation)
    TextView tvDoctorEvaluation;
    @BindView(R.id.ll_doctor_evaluation)
    LinearLayout llDoctorEvaluation;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;
    @BindView(R.id.progress_view_sleep)
    SleepRecordProgressView progressViewSleep;
    @BindView(R.id.tv_sleep_quality)
    TextView tvSleepQuality;
    @BindView(R.id.tv_pills)
    TextView tvPills;
    @BindView(R.id.tv_wakeup_duration)
    TextView tvWakeupDuration;
    @BindView(R.id.tv_little_sleep_duration)
    TextView tvLittleSleepDuration;
    @BindView(R.id.tv_no_record_date)
    TextView tvNoRecordDate;
    @BindView(R.id.btn_go_record)
    Button btnGoRecord;
    @BindView(R.id.ll_no_sleep_record)
    LinearLayout llNoSleepRecord;
    private SleepRecord mSleepRecord;
    private boolean mForceShowDoctorAdvice;

    public SleepRecordView(Context context) {
        this(context, null);
    }

    public SleepRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View inflate = inflate(context, R.layout.view_sleep_record_view, this);
        ButterKnife.bind(this, inflate);
    }

    public void setSleepRecord(SleepRecord sleepRecord) {
        mSleepRecord = sleepRecord;
        boolean hasRecord = mSleepRecord != null;
        llSleepRecord.setVisibility(hasRecord ? VISIBLE : GONE);
        llNoSleepRecord.setVisibility(hasRecord ? GONE : VISIBLE);
        llDoctorEvaluation.setVisibility(hasRecord ? VISIBLE : GONE);
        titleViewSleepRecord.setVisibility(VISIBLE);
        boolean showRefill = hasRecord && TextUtils.isEmpty(sleepRecord.getDoctor_evaluation());
        titleViewSleepRecord.setTvMenuVisibility(showRefill ? VISIBLE : GONE);
        if (hasRecord) {
            showSleepRecord(sleepRecord);
        }
    }

    private void showSleepRecord(SleepRecord sleepRecord) {
        SleepRecordAnswer answer = sleepRecord.getAnswer();
        tvSleepQuality.setText(getSleepQualityString(answer.getEnergetic()));
        tvPills.setText(getPillsString(answer.getSleep_pills()));
        tvPills.setClickable(answer.getSleep_pills() != null && answer.getSleep_pills().size() != 0);
        tvWakeupDuration.setText(getDurationString("夜醒：", answer.getWake_minutes()));
        tvLittleSleepDuration.setText(getDurationString("小睡：", answer.getOther_sleep_total_minutes()));
        tvActualWorkAndResetTime.setText(String.format("%s-%s", answer.getBed_at(), answer.getGet_up_at()));
        tvSleepTime.setText(String.format("%s-%s", answer.getSleep_at(), answer.getWake_up_at()));
        tvSleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getSleep_duration()));
        tvFallAsleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getFall_asleep_duration()));
        tvSleepDesc.setText(answer.getRemark());
        tvSleepDesc.setVisibility(TextUtils.isEmpty(answer.getRemark()) ? GONE : VISIBLE);
        llDoctorEvaluation.setVisibility(sleepRecord.hasDoctorEvaluation() || mForceShowDoctorAdvice ? VISIBLE : GONE);
        tvDoctorEvaluation.setText(sleepRecord.hasDoctorEvaluation() ? sleepRecord.getDoctor_evaluation() : getContext().getString(R.string.no_doctor_evaluation_hint));
        progressViewSleep.setProgress(sleepRecord.getSleep_efficiency());
    }

    private String getSleepQualityString(int quality) {
        String[] qualityStrings = new String[]{
                "十分差",
                "较差",
                "正常",
                "较好",
                "超级棒",
        };
        if (quality < 0 || quality >= qualityStrings.length) {
            throw new RuntimeException("Run sleep quality");
        }
        return "醒来情绪" + qualityStrings[quality];
    }

    private String getDurationString(String label, int minutes) {
        return label + TimeUtil.getHourMinuteStringFromSecondInZh(minutes * 60);
    }

    private String getStringArray(List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        int size = strings.size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(strings.get(i));
            if (i != size - 1) {
                stringBuilder.append("、");
            }
        }
        return stringBuilder.toString();
    }

    private String getPillsString(List<SleepPill> pills) {
        if (pills == null || pills.size() == 0) {
            return getResources().getString(R.string.do_not_eat_pills);
        }
        List<String> strList = new ArrayList<>();
        for (SleepPill pill : pills) {
            strList.add(pill.getName());
        }
        return getStringArray(strList);
    }

    @OnClick(R.id.tv_pills)
    public void onViewClicked() {
        showPillsDialogIfNeed();
    }

    private void showPillsDialogIfNeed() {
        List<SleepPill> sleep_pills = mSleepRecord.getAnswer().getSleep_pills();
        if (sleep_pills.size() == 0) {
            return;
        }
        PillsDialog.show(getContext(), sleep_pills);
    }

    public void setOnClickRefillSleepRecordListener(OnClickListener listener) {
        titleViewSleepRecord.setOnMenuClickListener(listener);
    }

    public void setOnClickFillSleepRecordBtnListener(OnClickListener listener) {
        btnGoRecord.setOnClickListener(listener);
    }

    public void setTime(long timeInMillis) {
        tvNoRecordDate.setText(TimeUtil.formatDate("M月d日", timeInMillis));
    }

    public void setForceShowDoctorAdvice(boolean forceShowDoctorAdvice) {
        mForceShowDoctorAdvice = forceShowDoctorAdvice;
        setSleepRecord(mSleepRecord);
    }
}
