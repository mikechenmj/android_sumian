package com.sumian.sd.diary.sleeprecord.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    TextView tvWakeupDuration;
    TextView tvSleepDesc;
    ViewGroup vgSleepDesc;
    LinearLayout llSleepRecord;
    LinearLayout llRoot;
    SleepRecordProgressView progressViewSleep;
    TextView tvSleepQuality;
    TextView tvPills;
    TextView tvLittleSleepDuration;
    TextView tvNoRecordDate;
    TextView btnGoRecord;
    LinearLayout llNoSleepRecord;
    TextView tvSleepRecordNotEnableHint;
    ImageView ivEmotion;
    SleepRecordDiagramView sleepRecordDiagramView;
    private SleepRecord mSleepRecord;
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
        findViewById(R.id.tv_go_to_set_diary_reminder).setOnClickListener((v) -> SleepDiaryRemindSettingActivity.launch(Reminder.TYPE_SLEEP_DIARY));
        titleViewSleepRecord = inflate.findViewById(R.id.title_view_sleep_record);
        llProgress = inflate.findViewById(R.id.ll_progress);
        tvOnBedDuration = inflate.findViewById(R.id.tv_on_bed_duration);
        tvSleepDuration = inflate.findViewById(R.id.tv_sleep_duration);
        tvWakeupDuration = inflate.findViewById(R.id.tv_night_wake_up_duration);
        tvSleepDesc = inflate.findViewById(R.id.tv_sleep_desc);
        vgSleepDesc = inflate.findViewById(R.id.vg_sleep_desc);
        llSleepRecord = inflate.findViewById(R.id.ll_sleep_record);
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
        ivEmotion = inflate.findViewById(R.id.iv_emotion);
        sleepRecordDiagramView = inflate.findViewById(R.id.sleep_record_diagram_view);
    }

    public void setSleepRecord(SleepRecord sleepRecord) {
        mSleepRecord = sleepRecord;
        boolean hasRecord = mSleepRecord != null;
        llSleepRecord.setVisibility(hasRecord ? VISIBLE : GONE);
        llNoSleepRecord.setVisibility(hasRecord ? GONE : VISIBLE);
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

    @SuppressLint("SetTextI18n")
    private void showSleepRecord(SleepRecord sleepRecord) {
        SleepRecordAnswer answer = sleepRecord.getAnswer();
        // 睡眠效率
        progressViewSleep.setProgress(sleepRecord.getSleep_efficiency());
        // 睡眠时长
        tvSleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getSleep_duration()));
        // 卧床时长
        tvOnBedDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getOn_bed_duration()));
        // 睡眠图
        sleepRecordDiagramView.setData(
                answer.getBedAtInMillis(), answer.getSleepAtInMillis(),
                answer.getWakeUpAtInMillis(), answer.getGetUpAtInMillis(),
                answer.getWake_times(), answer.getWakeDurationInMillis());
        // 情绪
        tvSleepQuality.setText(getSleepQualityString(answer.getEnergetic()));
        ivEmotion.setImageResource(getSleepQualityIcon(answer.getEnergetic()));
        // 夜醒
        tvWakeupDuration.setText(getWakeupOrOtherSleepString("没醒过", answer.getWake_times(), answer.getWake_minutes() * 60));
        // 小睡
        tvLittleSleepDuration.setText(getWakeupOrOtherSleepString("没小睡", answer.getOther_sleep_total_minutes(), answer.getOther_sleep_total_minutes() * 60));
        // 服药
        tvPills.setText(getPillsString(answer.getSleep_pills()));
        tvPills.setClickable(answer.getSleep_pills() != null && answer.getSleep_pills().size() != 0);
        // 睡眠备注
        vgSleepDesc.setVisibility(TextUtils.isEmpty(answer.getRemark()) ? GONE : VISIBLE);
        tvSleepDesc.setText(answer.getRemark());
    }

    @NonNull
    private String getWakeupOrOtherSleepString(String emptyString, int times, int duration) {
        if (times == 0) {
            return emptyString;
        } else {
            return "" + times + "次，" + TimeUtil.getHourMinuteStringFromSecondInZh(duration);
        }
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
        return qualityStrings[quality];
    }

    private int getSleepQualityIcon(int quality) {
        int[] qualityIcons = new int[]{
                R.drawable.record_icon_facial_1,
                R.drawable.record_icon_facial_2,
                R.drawable.record_icon_facial_3,
                R.drawable.record_icon_facial_4,
                R.drawable.record_icon_facial_5,
        };
        if (quality < 0 || quality >= qualityIcons.length) {
            throw new RuntimeException("Run sleep quality");
        }
        return qualityIcons[quality];
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
        if (sleep_pills == null || sleep_pills.size() == 0) {
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
