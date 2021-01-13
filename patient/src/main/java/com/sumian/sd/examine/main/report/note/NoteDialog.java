package com.sumian.sd.examine.main.report.note;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.sumian.common.helper.ToastHelper;
import com.sumian.common.widget.FlowLayout;
import com.sumian.sd.R;
import com.sumian.sd.buz.report.bean.DailyReport;
import com.sumian.sd.widget.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:
 */

public class NoteDialog extends BaseDialogFragment implements View.OnClickListener, WakeUpMoodView.OnSelectCallback {

    WakeUpMoodView mWakeUpMoodView;
    EditText mEtSleepRemark;
    TextView mTvInputCount;
    TextView mBtSubmit;
    FlowLayout mLayFlow;
    private List<String> mBedTimeStates = new ArrayList<>(0);
    private SleepNote mSleepNote;

    private int mMoodState = -1;

    public static NoteDialog newInstance(SleepNote sleepNote) {
        NoteDialog noteDialog = new NoteDialog();
        Bundle args = new Bundle();
        args.putSerializable("sleepNote", sleepNote);
        noteDialog.setArguments(args);
        return noteDialog;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
    }

    public void initSleepState() {
        mSleepNote = new SleepNote();
        mSleepNote.sleepId = 0;
        mSleepNote.wakeUpMood = 0;
        mSleepNote.bedtimeState = new ArrayList<>();
        mSleepNote.remark = "";
        ArrayList<String> bedtimeState = new ArrayList<>();
        String[] bedtimes = {"饮酒", "喝茶", "吃太饱", "有心事", "睡前运动过量", "今天太累", "身体不适", "陌生床", "天气太冷", "天气太热"};

        for (String bedtime : bedtimes) {
            bedtimeState.add(bedtime);
        }
        for (int i = 0, len = bedtimeState.size(); i < len; i++) {
            String option = bedtimeState.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.lay_options, mLayFlow, false);
            TextView tvOption = view.findViewById(R.id.tv_option);
            tvOption.setTag(i);
            tvOption.setOnClickListener(v -> {
                int position = (int) v.getTag();
                View child = mLayFlow.getChildAt(position);
                TextView textView = child.findViewById(R.id.tv_option);
                String text;
                if (textView.isActivated()) {
                    textView.setActivated(false);
                    text = textView.getText().toString().trim();
                    if (TextUtils.isEmpty(text)) return;
                    mBedTimeStates.remove(text);
                } else {
                    textView.setActivated(true);
                    text = textView.getText().toString().trim();
                    if (TextUtils.isEmpty(text)) return;
                    mBedTimeStates.add(text);
                }
            });

            if (isSelectOption(option)) {
                tvOption.setActivated(true);
                mBedTimeStates.add(option);
            } else {
                tvOption.setActivated(false);
                mBedTimeStates.remove(option);
            }
            tvOption.setText(option);
            mLayFlow.addView(view);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.lay_dialog_sleep_note;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mWakeUpMoodView = rootView.findViewById(R.id.wake_up_mood_view);
        mEtSleepRemark = rootView.findViewById(R.id.et_sleep_remark);
        mTvInputCount = rootView.findViewById(R.id.tv_input_count);
        mBtSubmit = rootView.findViewById(R.id.bt_submit);
        mLayFlow = rootView.findViewById(R.id.lay_flow);
        mWakeUpMoodView.setOnSelectCallback(this);
        mBtSubmit.setOnClickListener(this);
        rootView.findViewById(R.id.tv_ignore).setOnClickListener(this);
        mEtSleepRemark.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                @ColorRes int ColorId = !TextUtils.isEmpty(s) && s.length() <= 200 ? R.color.translucent_general_color : R.color.warn_color;
                mTvInputCount.setTextColor(getResources().getColor(ColorId));
                mTvInputCount.setVisibility(TextUtils.isEmpty(s) || s.length() <= 0 ? View.GONE : View.VISIBLE);
                if (!TextUtils.isEmpty(s)) {
                    mTvInputCount.setText(s.length() + "/" + "200");
                }
            }
        });
        initSleepState();
    }

    @Override
    protected void initData() {
        super.initData();
        if (mSleepNote != null) {
            mMoodState = mSleepNote.wakeUpMood;
            mWakeUpMoodView.setWakeUpMood(mMoodState);
            String remark = mSleepNote.remark;
            mEtSleepRemark.setText(remark);
            mTvInputCount.setVisibility(TextUtils.isEmpty(remark) || remark.length() <= 0 ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(remark)) {
                mTvInputCount.setText(String.format(Locale.getDefault(), "%d/200", remark.length()));
            }

            @ColorRes int ColorId = !TextUtils.isEmpty(remark) && remark.length() <= 200 ? R.color.translucent_general_color : R.color.warn_color;
            mTvInputCount.setTextColor(getResources().getColor(ColorId));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ignore:
                dismiss();
                break;
            case R.id.bt_submit:

                String remark = mEtSleepRemark.getText().toString().trim();

                if (mMoodState == -1 && mBedTimeStates.size() <= 0 && TextUtils.isEmpty(remark)) {
                    ToastHelper.show(R.string.note_not_null);
                    return;
                }

                if (remark.length() > 200) {
                    ToastHelper.show(R.string.sleep_note_error_three);
                    return;
                }

                ToastHelper.show("日志已上传");
                dismiss();

                break;
            default:
                break;
        }
    }

    private boolean isSelectOption(String option) {
        if (mSleepNote != null) {
            if (mSleepNote.bedtimeState != null) {
                for (String bedTimeState : mSleepNote.bedtimeState) {
                    if (option.equals(bedTimeState)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void selectWakeUpMood(int moodState) {
        this.mMoodState = moodState;
    }

    public interface OnWriteNoteCallback {

        void onWrite(DailyReport dailyReport);
    }
}
