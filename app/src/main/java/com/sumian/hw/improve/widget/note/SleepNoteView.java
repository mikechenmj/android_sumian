package com.sumian.hw.improve.widget.note;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.improve.report.note.SleepNote;
import com.sumian.hw.improve.widget.base.BaseBlueCardView;

import java.util.List;

/**
 * Created by jzz
 * on 2018/3/21.
 * desc:
 */

public class SleepNoteView extends BaseBlueCardView {

    TextView mTvMoodLabel;
    ImageView mIvMood;
    TextView mTvMoodStatus;
    ImageView mIvModifyNote;
    TextView mTvSleepStatus;
    TextView mTvRemark;

    public SleepNoteView(@NonNull Context context) {
        this(context, null);
    }

    public SleepNoteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepNoteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.hw_lay_sleep_note_view;
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        mTvMoodLabel = inflate.findViewById(R.id.tv_mood_label);
        mIvMood = inflate.findViewById(R.id.iv_mood);
        mTvMoodStatus = inflate.findViewById(R.id.tv_mood_status);
        mIvModifyNote = inflate.findViewById(R.id.iv_modify_note);
        mTvSleepStatus = inflate.findViewById(R.id.tv_sleep_status);
        mTvRemark = inflate.findViewById(R.id.tv_remark);
    }

    public void setSleepNote(SleepNote sleepNote) {
        String wakeUpMood;
        @DrawableRes int wakeUpMoodId = 0;
        switch (sleepNote.wakeUpMood) {
            case -1:
                wakeUpMood = "----";
                break;
            case 0:
                wakeUpMood = "不太好";
                wakeUpMoodId = R.mipmap.ic_report_bad_selected;
                break;
            case 1:
                wakeUpMood = "一般般";
                wakeUpMoodId = R.mipmap.ic_report_general_selected;
                break;
            case 2:
                wakeUpMood = "还可以";
                wakeUpMoodId = R.mipmap.ic_report_good_selected;
                break;
            case 3:
                wakeUpMood = "好极了";
                wakeUpMoodId = R.mipmap.ic_report_very_selected;
                break;
            default:
                wakeUpMood = "----";
                break;
        }
        mTvMoodStatus.setText(wakeUpMood);
        if (wakeUpMoodId == 0) {
            mIvMood.setVisibility(GONE);
        } else {
            mIvMood.setImageResource(wakeUpMoodId);
            mIvMood.setVisibility(VISIBLE);
        }

        List<String> bedtimeState = sleepNote.bedtimeState;
        if (bedtimeState == null || bedtimeState.size() <= 0) {
            mTvSleepStatus.setText("睡前状态: ----");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bedtimeState.size(); i++) {
                String bedtime = bedtimeState.get(i);
                if (i == bedtimeState.size() - 1) {
                    sb.append(bedtime);
                } else {
                    sb.append(bedtime).append("、");
                }

            }

            mTvSleepStatus.setText(String.format("睡前状态:  %s", sb.toString()));
        }

        String remark = sleepNote.remark;
        mTvRemark.setText(TextUtils.isEmpty(remark) ? "睡前备注: ----" : ("睡前备注:  " + remark));
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void addOnClickListener(OnClickListener onClickListener) {
        mIvModifyNote.setOnClickListener(onClickListener);
    }
}
