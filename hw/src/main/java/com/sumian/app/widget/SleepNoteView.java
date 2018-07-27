package com.sumian.app.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.network.response.SleepDetailReport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:睡眠日记 view
 */

public class SleepNoteView extends LinearLayout {

    @BindView(R.id.iv_report_sleep_mood)
    ImageView mIvReportSleepMood;
    @BindView(R.id.tv_report_sleep_mood)
    TextView mTvReportSleepMood;
    @BindView(R.id.lay_flow)
    FlowLayout mLayFlow;
    @BindView(R.id.tv_sleep_remark)
    TextView mTvSleepRemark;
    @BindView(R.id.tv_sleep_evaluate)
    TextView mTvSleepEvaluate;

    @BindView(R.id.v_line_one)
    View mVLineOne;
    @BindView(R.id.v_line_two)
    View mVLineTwo;
    @BindView(R.id.v_line_three)
    View mVLineThree;
    @BindView(R.id.v_line_four)
    View mVLineFour;

    @BindView(R.id.lay_sleep_note_mood)
    LinearLayout mLaySleepNoteMood;
    @BindView(R.id.lay_sleep_status)
    LinearLayout mLaySleepStatus;
    @BindView(R.id.lay_sleep_remark)
    LinearLayout mLaySleepRemark;

    @BindView(R.id.tv_doctor_evaluate)
    TextView mTvDoctorEvaluate;


    public SleepNoteView(Context context) {
        this(context, null);
    }

    public SleepNoteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepNoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(LayoutInflater.from(context).inflate(R.layout.hw_lay_sleep_note, this, true));
        setVisibility(GONE);
    }

    public void addSleepNoteData(SleepDetailReport sleepDetailReport) {
        if (sleepDetailReport == null) {
            mLaySleepNoteMood.setVisibility(GONE);
            mLaySleepStatus.setVisibility(GONE);
            mLaySleepRemark.setVisibility(GONE);
            mTvSleepRemark.setText(R.string.none_sleep_note);
            mLayFlow.setVisibility(GONE);
            mVLineOne.setVisibility(GONE);
            mVLineTwo.setVisibility(GONE);
            mVLineThree.setVisibility(GONE);
            mTvDoctorEvaluate.setVisibility(GONE);
            mVLineFour.setVisibility(GONE);
            mTvSleepEvaluate.setVisibility(GONE);
            setVisibility(VISIBLE);
        } else {

            if (TextUtils.isEmpty(sleepDetailReport.getWrote_diary_at())) {//通过填写睡眠日记的时间点判断是否有睡眠日记
                mLaySleepNoteMood.setVisibility(GONE);
                mLaySleepStatus.setVisibility(GONE);
                mLaySleepRemark.setVisibility(GONE);
                mTvSleepRemark.setText(R.string.none_sleep_note);
                mLayFlow.setVisibility(GONE);
                mVLineOne.setVisibility(GONE);
                mVLineTwo.setVisibility(GONE);
                setVisibility(VISIBLE);
            } else {

                setSleepModeIcon(sleepDetailReport.getId());//苏醒情绪

                List<String> bedtimeState = sleepDetailReport.getBedtime_state();//睡眠日记入睡前的状态
                if (bedtimeState == null || bedtimeState.isEmpty()) {
                    mLaySleepStatus.setVisibility(GONE);
                    mVLineOne.setVisibility(GONE);
                    mLayFlow.setVisibility(GONE);
                } else {
                    for (String bedTime : bedtimeState) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_options,
                            mLayFlow, false);
                        TextView TvOption = view.findViewById(R.id.tv_option);
                        TvOption.setText(bedTime);
                        mLayFlow.addView(view);
                    }
                    mLayFlow.setVisibility(VISIBLE);
                }

                String remark = sleepDetailReport.getRemark();//睡眠日记备注
                if (TextUtils.isEmpty(remark)) {
                    mLaySleepRemark.setVisibility(GONE);
                } else {
                    mLaySleepRemark.setVisibility(VISIBLE);
                    mTvSleepRemark.setText(remark);
                    mTvSleepRemark.setVisibility(VISIBLE);
                }
            }

            String doctorsEvaluation = sleepDetailReport.getDoctors_evaluation();//医生评价
            if (TextUtils.isEmpty(doctorsEvaluation)) {
                mVLineThree.setVisibility(GONE);
                mTvDoctorEvaluate.setVisibility(GONE);
                mVLineFour.setVisibility(GONE);
                mTvSleepEvaluate.setVisibility(GONE);
            } else {
                mVLineThree.setVisibility(VISIBLE);
                mTvSleepEvaluate.setVisibility(VISIBLE);
                mVLineFour.setVisibility(VISIBLE);
                mTvSleepEvaluate.setText(doctorsEvaluation);
                mTvSleepEvaluate.setVisibility(VISIBLE);
            }
            setVisibility(VISIBLE);
        }

    }


    private void setSleepModeIcon(int sleepMood) {
        if (sleepMood == -1) {
            mLaySleepNoteMood.setVisibility(GONE);
        } else {
            @DrawableRes int DrawableId;
            @StringRes int stringId;
            switch (sleepMood) {
                case 0://bad
                    DrawableId = R.mipmap.ic_report_bad_selected;
                    stringId = R.string.not_bad;
                    break;
                case 1://general
                    DrawableId = R.mipmap.ic_report_general_selected;
                    stringId = R.string.general;
                    break;
                case 2://good
                    DrawableId = R.mipmap.ic_report_good_selected;
                    stringId = R.string.good;
                    break;
                case 3://very good
                    DrawableId = R.mipmap.ic_report_very_selected;
                    stringId = R.string.very_good;
                    break;
                default:
                    DrawableId = R.mipmap.ic_report_good_selected;
                    stringId = R.string.good;
                    break;
            }
            this.mIvReportSleepMood.setImageResource(DrawableId);
            this.mTvReportSleepMood.setText(stringId);
        }

        this.mIvReportSleepMood.setVisibility(sleepMood == -1 ? GONE : VISIBLE);
        this.mTvReportSleepMood.setVisibility(sleepMood == -1 ? GONE : VISIBLE);
    }
}
