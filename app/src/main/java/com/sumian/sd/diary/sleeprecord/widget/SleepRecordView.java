package com.sumian.sd.diary.sleeprecord.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.common.utils.ColorCompatUtil;
import com.sumian.common.utils.TimeUtilV2;
import com.sumian.sd.R;
import com.sumian.sd.diary.sleeprecord.bean.SleepPill;
import com.sumian.sd.diary.sleeprecord.bean.SleepRecord;
import com.sumian.sd.diary.sleeprecord.bean.SleepRecordAnswer;
import com.sumian.sd.diary.sleeprecord.pill.PillsDialog;
import com.sumian.sd.setting.remind.SleepDiaryRemindSettingActivity;
import com.sumian.sd.setting.remind.bean.Reminder;
import com.sumian.sd.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/31 20:02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordView extends LinearLayout implements View.OnClickListener {
    TitleView titleViewSleepRecord;
    LinearLayout llProgress;
    TextView tvOnBedDuration;
    TextView tvSleepDuration;
    TextView tvFallAsleepDuration;
    TextView tvWakeupDuration;
    TextView tvSleepDesc;
    LinearLayout llSleepRecord;
    TitleView titleViewDoctorAdvise;
    TextView tvDoctorEvaluation;
    LinearLayout llDoctorEvaluation;
    LinearLayout llRoot;
    SleepRecordProgressView progressViewSleep;
    TextView tvSleepQuality;
    TextView tvPills;
    TextView tvLittleSleepDuration;
    TextView tvNoRecordDate;
    TextView btnGoRecord;
    LinearLayout llNoSleepRecord;
    TextView tvSleepRecordNotEnableHint;
    private SleepRecord mSleepRecord;
    private boolean mForceShowDoctorAdvice;
    private long mTime;

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
        findViewById(R.id.tv_go_to_set_diary_reminder).setOnClickListener((v) -> SleepDiaryRemindSettingActivity.launch(Reminder.TYPE_SLEEP_DIARY));
        titleViewSleepRecord = inflate.findViewById(R.id.title_view_sleep_record);
        llProgress = inflate.findViewById(R.id.ll_progress);
        tvOnBedDuration = inflate.findViewById(R.id.tv_on_bed_duration);
        tvSleepDuration = inflate.findViewById(R.id.tv_sleep_duration);
        tvFallAsleepDuration = inflate.findViewById(R.id.tv_fall_asleep_duration);
        tvWakeupDuration = inflate.findViewById(R.id.tv_night_wake_up_duration);
        tvSleepDesc = inflate.findViewById(R.id.tv_sleep_desc);
        llSleepRecord = inflate.findViewById(R.id.ll_sleep_record);
        titleViewDoctorAdvise = inflate.findViewById(R.id.title_view_doctor_advise);
        tvDoctorEvaluation = inflate.findViewById(R.id.tv_doctor_evaluation);
        llDoctorEvaluation = inflate.findViewById(R.id.ll_doctor_evaluation);
        llRoot = inflate.findViewById(R.id.ll_root);
        progressViewSleep = inflate.findViewById(R.id.progress_view_sleep);
        tvSleepQuality = inflate.findViewById(R.id.tv_sleep_quality);
        tvPills = inflate.findViewById(R.id.tv_pills);
        tvPills.setOnClickListener(this);
        tvLittleSleepDuration = inflate.findViewById(R.id.tv_little_sleep_duration);
        tvNoRecordDate = inflate.findViewById(R.id.tv_no_record_date);
        btnGoRecord = inflate.findViewById(R.id.btn_for_no_data);
        llNoSleepRecord = inflate.findViewById(R.id.ll_no_sleep_record);
        tvSleepRecordNotEnableHint = inflate.findViewById(R.id.tv_sleep_record_not_enable_hint);
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
        boolean isFillSleepRecordEnable = isFillSleepRecordEnable(mTime);
        btnGoRecord.setEnabled(isFillSleepRecordEnable);
        tvSleepRecordNotEnableHint.setVisibility(isFillSleepRecordEnable ? GONE : VISIBLE);
        titleViewSleepRecord.tvMenu.setTextColor(ColorCompatUtil.getColor(getContext(), isFillSleepRecordEnable ? R.color.t1_color : R.color.t2_color));
    }

    private void showSleepRecord(SleepRecord sleepRecord) {
        SleepRecordAnswer answer = sleepRecord.getAnswer();
        tvSleepQuality.setText(getSleepQualityString(answer.getEnergetic()));
        tvPills.setText(getPillsString(answer.getSleep_pills()));
        tvPills.setClickable(answer.getSleep_pills() != null && answer.getSleep_pills().size() != 0);
        tvLittleSleepDuration.setText(getDurationString("小睡：", answer.getOther_sleep_total_minutes()));

        tvOnBedDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getOn_bed_duration()));
        tvSleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getSleep_duration()));
        tvFallAsleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getFall_asleep_duration()));
        tvWakeupDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(answer.getWake_minutes() * 60));

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
        return "自我评价：" + qualityStrings[quality];
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
        llNoSleepRecord.setOnClickListener(listener);
    }

    public void setTime(long timeInMillis) {
        mTime = timeInMillis;
        tvNoRecordDate.setText(TimeUtil.formatDate("M月d日", timeInMillis));
    }

    public void setForceShowDoctorAdvice(boolean forceShowDoctorAdvice) {
        mForceShowDoctorAdvice = forceShowDoctorAdvice;
        setSleepRecord(mSleepRecord);
    }

    private boolean isFillSleepRecordEnable(long recordTime) {
        return TimeUtilV2.Companion.getDayDistance(System.currentTimeMillis(), recordTime) < 3;
    }

    @Override
    public void onClick(View v) {
        showPillsDialogIfNeed();
    }
}
